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

import confluent_kafka
from oslo_utils import encodeutils

log = logging.getLogger(__name__)


class KafkaProducer(object):
    """Wrapper around asynchronous Kafka Producer"""

    def __init__(self, bootstrap_servers, **config):
        """
        Create new Producer wrapper instance.

        :param str bootstrap_servers: Initial list of brokers as a CSV
        list of broker host or host:port.
        :param config Configuration properties
        """

        config['bootstrap.servers'] = bootstrap_servers
        self._producer = confluent_kafka.Producer(config)

    @staticmethod
    def delivery_report(err, msg):
        """
        Callback called once for each produced message to indicate the final
        delivery result. Triggered by poll() or flush().

        :param confluent_kafka.KafkaError err: Information about any error
        that occurred whilst producing the message.
        :param confluent_kafka.Message msg: Information about the message
        produced.
        :returns: None
        :raises confluent_kafka.KafkaException
        """

        if err is not None:
            log.exception('Message delivery failed: {}'.format(err))
            raise confluent_kafka.KafkaException(err)
        else:
            log.debug('Message delivered to {} [{}]: {}'.format(
                msg.topic(), msg.partition(), msg.value()))

    def publish(self, topic, messages, key=None, timeout=2):
        """
        Publish messages to the topic.

        :param str topic: Topic to produce messages to.
        :param list(str) messages:  List of message payloads.
        :param str key: Message key.
        :param float timeout: Maximum time to block in seconds.
        :returns: Number of messages still in queue.
        :rtype int
        """

        if not isinstance(messages, list):
            messages = [messages]

        try:
            for m in messages:
                m = encodeutils.safe_encode(m, incoming='utf-8')
                self._producer.produce(topic, m, key,
                                       callback=KafkaProducer.delivery_report)
                self._producer.poll(0)

            return self._producer.flush(timeout)

        except (BufferError, confluent_kafka.KafkaException,
                NotImplementedError):
            log.exception(u'Error publishing to {} topic.'.format(topic))
            raise
