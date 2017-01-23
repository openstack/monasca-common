# (C) Copyright 2014-2016 Hewlett Packard Enterprise Development Company LP
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

import datetime
import logging
import threading
import time

import monasca_common.kafka_lib.client as kafka_client
import monasca_common.kafka_lib.common as kafka_common
import monasca_common.kafka_lib.consumer as kafka_consumer

from kazoo.client import KazooClient
from kazoo.recipe.partitioner import SetPartitioner

log = logging.getLogger(__name__)

"""Kafka consumer interface

Kafka consumer class that will automatically share partitions between processes
using the same zookeeper path.

For performance it is often required that data from the kafka queue be
batched before being processed.  There are two important concerns to
keep in mind when dealing with batched data.

    1. Negotiating partitions takes a rather long amount of time so when
    the negotiation process begins a defined repartition_callback will be
    called.  This is a good time to process whatever has been batched.
    2. If the traffic across the kafka topic is low enough it will take a long
    time to build a batch of data.  A commit_callback is available that will
    fire when the commit_timeout duration has elapsed since the last commit.
"""


class KafkaConsumer(object):
    def __init__(self, kafka_url,
                 zookeeper_url, zookeeper_path,
                 group, topic,
                 fetch_size=1048576,
                 repartition_callback=None,
                 commit_callback=None,
                 commit_timeout=30):
        """Init

             kafka_url            - Kafka location
             zookeeper_url        - Zookeeper location
             zookeeper_path       - Zookeeper path used for partition
                                    negotiation
             group                - Kafka consumer group
             topic                - Kafka topic
             repartition_callback - Callback to run when the Kafka consumer
                                    group changes.  Repartitioning takes a
                                    relatively long time so this is a good
                                    time to flush and commit any data.
             commit_callback      - Callback to run when the commit_timeout
                                    has elapsed between commits.
             commit_timeout       - Timeout between commits.
        """

        self._kazoo_client = None
        self._set_partitioner = None

        self._repartition_callback = repartition_callback

        self._commit_callback = commit_callback
        self._commit_timeout = commit_timeout

        self._last_commit = 0

        self._partitions = []

        self._kafka_group = group
        self._kafka_topic = topic
        self._kafka_fetch_size = fetch_size

        self._zookeeper_url = zookeeper_url
        self._zookeeper_path = zookeeper_path

        self._kafka = kafka_client.KafkaClient(kafka_url)

        self._consumer = self._create_kafka_consumer()

    def _create_kafka_consumer(self, partitions=None):
        # No auto-commit so that commits only happen after the message is processed.
        consumer = kafka_consumer.SimpleConsumer(
            self._kafka,
            self._kafka_group,
            self._kafka_topic,
            auto_commit=False,
            partitions=partitions,
            iter_timeout=5,
            fetch_size_bytes=self._kafka_fetch_size,
            buffer_size=self._kafka_fetch_size,
            max_buffer_size=None)

        consumer.provide_partition_info()
        consumer.fetch_last_known_offsets()
        return consumer

    def __iter__(self):
        self._partition()

        self._last_commit = datetime.datetime.now()

        while 1:
            if self._repartition():
                if self._repartition_callback:
                    self._repartition_callback()
                self._partition()

            # When Kafka resizes the partitions it's possible that it
            # will remove data at our current offset.  When this
            # happens the next attempt to read from Kafka will generate
            # an OffsetOutOfRangeError.  We trap this error and seek to
            # the head of the current Kafka data.  Because this error
            # only happens when Kafka removes data we're currently
            # pointing at we're gauranteed that we won't read any
            # duplicate data however we will lose any information
            # between our current offset and the new Kafka head.

            try:
                message = self._consumer.get_message()
                if message:
                    yield message
                else:
                    time.sleep(0.01)

                if self._commit_callback:
                    time_now = datetime.datetime.now()
                    time_delta = time_now - self._last_commit
                    if time_delta.total_seconds() > self._commit_timeout:
                        self._commit_callback()

            except kafka_common.OffsetOutOfRangeError:
                log.error("Kafka OffsetOutOfRange.  Jumping to head.")
                self._consumer.seek(0, 0)

    def _repartition(self):
        return not self._set_partitioner.acquired

    def _partition(self):
        """Consume messages from kafka

           Consume messages from kafka using the Kazoo SetPartitioner to
           allow multiple consumer processes to negotiate access to the kafka
           partitions
        """

        # KazooClient and SetPartitioner objects need to be instantiated after
        # the consumer process has forked.  Instantiating prior to forking
        # gives the appearance that things are working but after forking the
        # connection to zookeeper is lost and no state changes are visible

        if not self._kazoo_client:
            self._kazoo_client = KazooClient(hosts=self._zookeeper_url)
            self._kazoo_client.start()

            state_change_event = threading.Event()

            self._set_partitioner = (
                SetPartitioner(self._kazoo_client,
                               path=self._zookeeper_path,
                               set=self._consumer.fetch_offsets.keys(),
                               state_change_event=state_change_event,
                               identifier=str(datetime.datetime.now())))

        try:
            while 1:
                if self._set_partitioner.failed:
                    raise Exception("Failed to acquire partition")

                elif self._set_partitioner.release:
                    log.info("Releasing locks on partition set {} "
                             "for topic {}".format(self._partitions,
                                                   self._kafka_topic))
                    self._set_partitioner.release_set()

                    self._partitions = []

                elif self._set_partitioner.acquired:
                    if not self._partitions:
                        self._partitions = [p for p in self._set_partitioner]

                        if not self._partitions:
                            log.info("Not assigned any partitions on topic {},"
                                     " waiting for a Partitioner state change"
                                     .format(self._kafka_topic))
                            state_change_event.wait()
                            state_change_event.clear()
                            continue

                        log.info("Acquired locks on partition set {} "
                                 "for topic {}".format(self._partitions, self._kafka_topic))

                        # Reconstruct the kafka consumer object because the
                        # consumer has no API that allows the set of partitons
                        # to be updated outside of construction.
                        self._consumer.stop()
                        self._consumer = self._create_kafka_consumer(self._partitions)
                        return

                elif self._set_partitioner.allocating:
                    log.info("Waiting to acquire locks on partition set")
                    self._set_partitioner.wait_for_acquire()

        except Exception:
            log.exception('KafkaConsumer encountered fatal exception '
                          'processing messages.')
            raise

    def commit(self):
        self._last_commit = datetime.datetime.now()
        self._consumer.commit(partitions=self._partitions)
