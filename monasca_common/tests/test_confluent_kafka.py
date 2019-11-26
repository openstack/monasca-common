# -*- coding: utf-8 -*-
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
# implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import mock

from monasca_common.confluent_kafka import consumer
from monasca_common.confluent_kafka import producer

import confluent_kafka
from oslotest import base

FAKE_KAFKA_TOPIC = 'topic'
FAKE_KAFKA_URL = 'FAKE_KAFKA_URL'


class TestConfluentKafkaProducer(base.BaseTestCase):

    @mock.patch('confluent_kafka.Producer')
    def setUp(self, mock_confluent_producer):
        super(TestConfluentKafkaProducer, self).setUp()
        self.mock_confluent_producer = mock_confluent_producer
        self.prod = producer.KafkaProducer(FAKE_KAFKA_URL)

    def tearDown(self):
        super(TestConfluentKafkaProducer, self).tearDown()

    def test_kafka_producer_init(self):
        expected_config = {'bootstrap.servers': FAKE_KAFKA_URL}

        self.mock_confluent_producer.assert_called_once_with(expected_config)
        self.assertEqual(self.mock_confluent_producer.return_value,
                         self.prod._producer)

    def test_kafka_producer_publish(self):
        topic = FAKE_KAFKA_TOPIC
        messages = [u'message']
        expected_message = b'message'

        self.prod.publish(topic, messages)

        produce_callback = producer.KafkaProducer.delivery_report
        self.prod._producer.produce.assert_called_once_with(topic,
                                                            expected_message,
                                                            None,
                                                            callback=produce_callback)
        self.prod._producer.flush.assert_called_once()

    def test_kafka_producer_publish_one_message_with_key(self):
        topic = FAKE_KAFKA_TOPIC
        one_message = u'message'
        key = u'1000'
        expected_message = b'message'

        self.prod.publish(topic, one_message, key)

        produce_callback = producer.KafkaProducer.delivery_report
        self.prod._producer.produce.assert_called_once_with(topic,
                                                            expected_message,
                                                            key,
                                                            callback=produce_callback)
        self.prod._producer.flush.assert_called_once()

    def test_kafka_producer_publish_exception(self):
        topic = FAKE_KAFKA_TOPIC
        messages = [u'message']
        self.prod._producer.produce.side_effect = \
            confluent_kafka.KafkaException

        self.assertRaises(confluent_kafka.KafkaException, self.prod.publish,
                          topic, messages)

    @mock.patch('monasca_common.confluent_kafka.producer.log')
    @mock.patch('confluent_kafka.Message')
    def test_delivery_report_exception(self, mock_message, mock_logger):
        self.assertRaises(confluent_kafka.KafkaException,
                          self.prod.delivery_report,
                          confluent_kafka.KafkaError,
                          confluent_kafka.Message)
        mock_logger.exception.assert_called_once()

    @mock.patch('monasca_common.confluent_kafka.producer.log')
    @mock.patch('confluent_kafka.Message')
    def test_delivery_report(self, mock_message, mock_logger):
        self.prod.delivery_report(None, confluent_kafka.Message)
        mock_logger.debug.assert_called_once()

    @mock.patch('monasca_common.confluent_kafka.producer.log')
    @mock.patch('confluent_kafka.Message')
    def test_delivery_report_with_unicode(self, mock_message, mock_logger):
        mock_message.topic.return_value = 'test_topic'
        mock_message.partition.return_value = '1'
        mock_message.value.return_value = 'gęś'
        self.prod.delivery_report(None, mock_message)
        mock_logger.debug.assert_called_once_with('Message delivered to '
                                                  'test_topic [1]: '
                                                  'gęś')


class TestConfluentKafkaConsumer(base.BaseTestCase):

    @mock.patch('confluent_kafka.Consumer')
    def setUp(self, mock_confluent_consumer):
        super(TestConfluentKafkaConsumer, self).setUp()
        self.mock_confluent_consumer = mock_confluent_consumer
        self.consumer = consumer.KafkaConsumer('fake_server1,fake_server2',
                                               'fake_group',
                                               FAKE_KAFKA_TOPIC, 128,
                                               'test_client',
                                               TestConfluentKafkaConsumer.rep_callback,
                                               TestConfluentKafkaConsumer.com_callback,
                                               5)

    @staticmethod
    def rep_callback(consumer, partitions):
        pass

    @staticmethod
    def com_callback(consumer, partitions):
        pass

    def tearDown(self):
        super(TestConfluentKafkaConsumer, self).tearDown()

    def test_kafka_consumer_init(self):
        expected_config = {'group.id': 'fake_group',
                           'session.timeout.ms': 10000,
                           'bootstrap.servers': 'fake_server1,fake_server2',
                           'fetch.min.bytes': 128,
                           'client.id': 'test_client',
                           'enable.auto.commit': False,
                           'default.topic.config':
                               {'auto.offset.reset': 'earliest'}
                           }

        self.mock_confluent_consumer.assert_called_once_with(expected_config)
        self.assertEqual(self.consumer._consumer,
                         self.mock_confluent_consumer.return_value)
        self.assertEqual(self.consumer._commit_callback,
                         TestConfluentKafkaConsumer.com_callback)
        self.assertEqual(self.consumer._max_commit_interval, 5)
        self.mock_confluent_consumer.return_value.subscribe \
            .assert_called_once_with([FAKE_KAFKA_TOPIC],
                                     on_revoke=TestConfluentKafkaConsumer.rep_callback)

    @mock.patch('confluent_kafka.Message')
    def test_kafka_consumer_iteration(self, mock_kafka_message):
        mock_kafka_message.return_value.error.return_value = None
        messages = []
        for i in range(5):
            m = mock_kafka_message.return_value
            m.set_value("message{}".format(i))
            messages.append(m)
        self.consumer._consumer.poll.side_effect = messages
        try:
            for index, message in enumerate(self.consumer):
                self.assertEqual(message, messages[index])
        except RuntimeError as re:
            if 'generator raised StopIteration' in str(re):
                pass

    @mock.patch('confluent_kafka.Message')
    @mock.patch('confluent_kafka.KafkaError')
    def test_kafka_consumer_poll_exception(self,
                                           mock_kafka_error,
                                           mock_kafka_message):
        mock_kafka_error.return_value.str = 'fake error message'
        mock_kafka_message.return_value.error.return_value = \
            mock_kafka_error
        messages = [mock_kafka_message.return_value]

        self.consumer._consumer.poll.side_effect = messages
        try:
            list(self.consumer)
        except Exception as ex:
            self.assertIsInstance(ex, confluent_kafka.KafkaException)

    @mock.patch('datetime.datetime')
    def test_kafka_commit(self, mock_datetime):
        self.consumer.commit()
        mock_datetime.now.assert_called_once()
        self.mock_confluent_consumer.return_value.commit.assert_called_once()
