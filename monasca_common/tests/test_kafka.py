# Licensed under the Apache License, Version 2.0 (the "License"); you may
# not use this file except in compliance with the License. You may obtain
# a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations
# under the License.

import mock

from oslotest import base

from monasca_common.kafka import consumer
from monasca_common.kafka import producer


FAKE_KAFKA_URL = "kafka_url"
FAKE_ZOOKEEPER_URL = "zookeeper_url"
FAKE_ZOOKEEPER_PATH = "zookeeper_path"
FAKE_KAFKA_CONSUMER_GROUP = "group"
FAKE_KAFKA_TOPIC = "topic"


class TestKafkaProducer(base.BaseTestCase):

    def setUp(self):
        super(TestKafkaProducer, self).setUp()

        self.kafka_client_patcher = mock.patch('monasca_common.kafka.producer.kafka_client')
        self.kafka_producer_patcher = mock.patch('monasca_common.kafka.producer.kafka_producer')
        self.mock_kafka_client = self.kafka_client_patcher.start()
        self.mock_kafka_producer = self.kafka_producer_patcher.start()
        self.producer = self.mock_kafka_producer.KeyedProducer.return_value
        self.client = self.mock_kafka_client.KafkaClient.return_value
        self.monasca_kafka_producer = producer.KafkaProducer(FAKE_KAFKA_URL)

    def tearDown(self):
        super(TestKafkaProducer, self).tearDown()

        self.kafka_producer_patcher.stop()
        self.kafka_client_patcher.stop()

    def test_kafka_producer_init(self):
        self.assertTrue(self.mock_kafka_client.KafkaClient.called)
        self.assertTrue(self.mock_kafka_producer.KeyedProducer.called)

    def test_kafka_producer_publish(self):
        topic = FAKE_KAFKA_TOPIC
        messages = ['message']
        key = 'key'

        self.monasca_kafka_producer.publish(topic, messages, key)

        self.producer.send_messages.assert_called_once_with(
            topic, key, *messages)

    @mock.patch('monasca_common.kafka.producer.time')
    def test_kafka_producer_publish_one_message_without_key(self, mock_time):
        topic = FAKE_KAFKA_TOPIC
        message = 'not_a_list'
        mock_time.time.return_value = 1
        expected_key = '1000'

        self.monasca_kafka_producer.publish(topic, message)

        self.assertTrue(mock_time.time.called)
        self.producer.send_messages.assert_called_once_with(
            topic, expected_key, message)

    @mock.patch('monasca_common.kafka.producer.log')
    def test_kafka_producer_publish_exception(self, mock_logger):
        class MockException(Exception):
            pass

        topic = FAKE_KAFKA_TOPIC
        messages = ['message']
        key = 'key'
        self.producer.send_messages.side_effect = MockException

        self.assertRaises(MockException, self.monasca_kafka_producer.publish,
                          topic, messages, key)

        mock_logger.exception.assert_called_once_with(
            'Error publishing to {} topic.'. format(topic))


class TestKafkaConsumer(base.BaseTestCase):

    def setUp(self):
        super(TestKafkaConsumer, self).setUp()

        self.kafka_client_patcher = mock.patch('monasca_common.kafka.consumer.kafka_client')
        self.kafka_common_patcher = mock.patch('monasca_common.kafka.consumer.kafka_common')
        self.kafka_consumer_patcher = mock.patch('monasca_common.kafka.consumer.kafka_consumer')
        self.kazoo_patcher = mock.patch(
            'monasca_common.kafka.consumer.KazooClient')

        self.mock_kafka_client = self.kafka_client_patcher.start()
        self.mock_kafka_common = self.kafka_common_patcher.start()
        self.mock_kafka_consumer = self.kafka_consumer_patcher.start()
        self.kazoo_patcher.start()

        self.client = self.mock_kafka_client.KafkaClient.return_value
        self.consumer = self.mock_kafka_consumer.SimpleConsumer.return_value

        self.monasca_kafka_consumer = consumer.KafkaConsumer(
            FAKE_KAFKA_URL, FAKE_ZOOKEEPER_URL, FAKE_ZOOKEEPER_PATH,
            FAKE_KAFKA_CONSUMER_GROUP, FAKE_KAFKA_TOPIC)

    def tearDown(self):
        super(TestKafkaConsumer, self).tearDown()

        self.kafka_client_patcher.stop()
        self.kafka_common_patcher.stop()
        self.kafka_consumer_patcher.stop()
        self.kazoo_patcher.stop()

    def test_kafka_consumer_init(self):
        self.assertTrue(self.mock_kafka_client.KafkaClient.called)
        self.assertTrue(self.mock_kafka_consumer.SimpleConsumer.called)

    @mock.patch('monasca_common.kafka.consumer.SetPartitioner')
    def test_kafka_consumer_process_messages(self, mock_set_partitioner):
        messages = []
        for i in range(5):
            messages.append("message{}".format(i))
        self.consumer.get_message.side_effect = messages
        mock_set_partitioner.return_value.failed = False
        mock_set_partitioner.return_value.release = False
        mock_set_partitioner.return_value.acquired = True
        mock_set_partitioner.return_value.__iter__.return_value = [1]

        for index, message in enumerate(self.monasca_kafka_consumer):
            self.assertEqual(message, messages[index])

    @mock.patch('monasca_common.kafka.consumer.datetime')
    def test_commit(self, mock_datetime):
        self.monasca_kafka_consumer.commit()

        self.assertTrue(mock_datetime.datetime.now.called)
        self.consumer.commit.assert_called_once_with(
            partitions=self.monasca_kafka_consumer._partitions)

    @mock.patch('monasca_common.kafka.consumer.SetPartitioner')
    def test_iteration_failed_to_acquire_partition(self, mock_set_partitioner):
        mock_set_partitioner.return_value.failed = True

        try:
            list(self.monasca_kafka_consumer)
        except Exception as e:
            self.assertEqual(str(e), "Failed to acquire partition")

    @mock.patch('monasca_common.kafka.consumer.SetPartitioner')
    def test_kafka_consumer_reset_when_offset_out_of_range(
            self, mock_set_partitioner):
        class OffsetOutOfRangeError(Exception):
            pass

        self.mock_kafka_common.OffsetOutOfRangeError = OffsetOutOfRangeError
        self.consumer.get_message.side_effect = [OffsetOutOfRangeError,
                                                 "message"]
        mock_set_partitioner.return_value.failed = False
        mock_set_partitioner.return_value.release = False
        mock_set_partitioner.return_value.acquired = True
        mock_set_partitioner.return_value.__iter__.return_value = [1]

        list(self.monasca_kafka_consumer)

        self.consumer.seek.assert_called_once_with(0, 0)
