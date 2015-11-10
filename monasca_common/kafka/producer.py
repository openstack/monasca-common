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

import kafka.client
import kafka.producer
import logging
import time

log = logging.getLogger(__name__)


class KafkaProducer(object):
    """Adds messages to a kafka topic
    """

    def __init__(self, url):
        """Init
             url - kafka connection details
        """
        self._kafka = kafka.client.KafkaClient(url)
        self._producer = kafka.producer.KeyedProducer(
            self._kafka,
            async=False,
            req_acks=kafka.producer.KeyedProducer.ACK_AFTER_LOCAL_WRITE,
            ack_timeout=2000)

    def publish(self, topic, messages, key=None):
        """Takes messages and puts them on the supplied kafka topic
        """

        # Using a key producer to make sure we can distribute messages evenly
        # across all partitions.  In the kafka-python library, as of version
        # 0.9.2, it doesn't support sending message batches for keyed
        # producers.  Batching writes to kafka is important for performance so
        # we have to work around this limitation.  Using the _next_partition
        # function allows us to get proper distribution and the speed of the
        # send_messages function.

        if not isinstance(messages, list):
            messages = [messages]

        try:
            if key is None:
                key = time.time() * 1000
            partition = self._producer._next_partition(topic, key)
            self._producer.send_messages(topic, partition, *messages)
        except Exception:
            log.exception('Error publishing to {} topic.'.format(topic))
            raise
