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

import datetime
import logging
import time

import confluent_kafka

log = logging.getLogger(__name__)


class KafkaConsumer(object):
    """Wrapper around high-level Kafka Consumer"""
    def __init__(self, bootstrap_servers, group_id, topic,
                 fetch_min_bytes=1048576, client_id="",
                 repartition_callback=None, commit_callback=None,
                 max_commit_interval=30, timeout=10000):
        """
        Create new high-level Consumer instance.

        :param str bootstrap_servers: Comma separated list of host/port pairs to
         use for establishing the initial connection to the Kafka cluster.
        :param str group_id: A unique string that identifies the consumer group
        this consumer belongs to.
        :param str topic: Topic to subscribe to.
        :param int fetch_min_bytes: The minimum amount of data the server
        should return for a fetch request.
        :param str client_id: An id string to pass to the server when making
        requests.
        :param callable repartition_callback: Callback function executed on the
        start of a rebalance operation.
        :param callable commit_callback: Callback function responsible for
        calling the commit() method.
        :param int max_commit_interval: Maximum time in seconds between commits.
        :param int timeout: Client group session and failure detection timeout.
        """

        consumer_config = {'bootstrap.servers': bootstrap_servers,
                           'group.id': group_id,
                           'session.timeout.ms': timeout,
                           'fetch.min.bytes': fetch_min_bytes,
                           'client.id': client_id,
                           'enable.auto.commit': False,
                           'default.topic.config':
                               {'auto.offset.reset': 'earliest'}
                           }
        self._commit_callback = commit_callback
        self._max_commit_interval = max_commit_interval
        self._consumer = confluent_kafka.Consumer(consumer_config)
        if repartition_callback:
            self._consumer.subscribe([topic], on_revoke=repartition_callback)
        else:
            self._consumer.subscribe([topic])
        self._last_commit = None

    def __iter__(self):
        self._last_commit = datetime.datetime.now()

        while True:
            message = self._consumer.poll(timeout=5)

            if message is None:
                time.sleep(0.1)
            elif not message.error():
                yield message
            elif message.error().code() == \
                    confluent_kafka.KafkaError._PARTITION_EOF:
                time.sleep(0.1)
                continue
            else:
                log.error("Kafka error: %s", message.error().str())
                raise confluent_kafka.KafkaException(message.error())

            if self._commit_callback:
                time_now = datetime.datetime.now()
                time_delta = time_now - self._last_commit
                if time_delta.total_seconds() > self._max_commit_interval:
                    self._commit_callback()

    def commit(self):
        self._last_commit = datetime.datetime.now()
        self._consumer.commit()
