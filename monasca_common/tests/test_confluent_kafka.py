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
        self.prod = producer.KafkaProducer(FAKE_KAFKA_TOPIC)

    def tearDown(self):
        super(TestConfluentKafkaProducer, self).tearDown()

    def test_kafka_producer_init(self):
        expected_config = {'bootstrap.servers': FAKE_KAFKA_TOPIC}

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
