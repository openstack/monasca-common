# (C) Copyright 2015, 2017 Hewlett Packard Enterprise Development LP
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

from oslo_utils import encodeutils
from six import PY3

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
            is_async=False,
            req_acks=kafka_producer.KeyedProducer.ACK_AFTER_LOCAL_WRITE,
            ack_timeout=2000)

    def publish(self, topic, messages, key=None):
        """Takes messages and puts them on the supplied kafka topic

        """

        if not isinstance(messages, list):
            messages = [messages]

        first = True
        success = False
        if key is None:
            key = int(time.time() * 1000)

        messages = [encodeutils.to_utf8(m) for m in messages]

        key = bytes(str(key), 'utf-8') if PY3 else str(key)

        while not success:
            try:
                self._producer.send_messages(topic, key, *messages)
                success = True
            except Exception:
                if first:
                    # This is a warning because of all the other warning and
                    # error messages that are logged in this case. This way
                    # someone looking at the log file can see the retry
                    log.warn("Failed send on topic {}, clear metadata and retry"
                             .format(topic))

                    # If Kafka is running in Kubernetes, the cached metadata
                    # contains the IP Address of the Kafka pod. If the Kafka
                    # pod has restarted, the IP Address will have changed
                    # which would have caused the first publish to fail. So,
                    # clear the cached metadata and retry the publish
                    self._kafka.reset_topic_metadata(topic)
                    first = False
                    continue
                log.exception('Error publishing to {} topic.'.format(topic))
                raise
