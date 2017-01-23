# Copyright (c) 2015 Hewlett-Packard Development Company, L.P.
#
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

import logging
import time

import monasca_common.kafka_lib.client as kafka_client
import monasca_common.kafka_lib.producer as kafka_producer

log = logging.getLogger(__name__)


class KafkaProducer(object):
    """Adds messages to a kafka topic

    """

    def __init__(self, url):
        """Init

             url - kafka connection details
        """
        self._kafka = kafka_client.KafkaClient(url)
        self._producer = kafka_producer.KeyedProducer(
            self._kafka,
            async=False,
            req_acks=kafka_producer.KeyedProducer.ACK_AFTER_LOCAL_WRITE,
            ack_timeout=2000)

    def publish(self, topic, messages, key=None):
        """Takes messages and puts them on the supplied kafka topic

        """

        if not isinstance(messages, list):
            messages = [messages]

        try:
            if key is None:
                key = int(time.time() * 1000)
            self._producer.send_messages(topic, str(key), *messages)
        except Exception:
            log.exception('Error publishing to {} topic.'.format(topic))
            raise
