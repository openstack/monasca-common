#!/usr/bin/env python
# coding=utf-8

# (C) Copyright 2017 Hewlett Packard Enterprise Development LP
# (C) Copyright 2018 FUJITSU LIMITED
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may
# not use this file except in compliance with the License. You may obtain
# a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations
# under the License.

"""Wait for specific Kafka topics.

For using this script you need to set two environment variables:
* `KAFKA_URI` for connection string to Kafka together with port.
  Example: `kafka:9092`, `192.168.10.6:9092`.
* `KAFKA_WAIT_FOR_TOPICS` that contain topics that should exist in Kafka
  to consider it's working. Many topics should be separated with comma.
  Example: `retry-notifications,alarm-state-transitions`.

After making sure that this environment variables are set you can simply
execute this script in the following way:
`python3 kafka_wait_for_topics.py && ./start_service.sh`
`python3 kafka_wait_for_topics.py || exit 1`

Additional environment variables available are:
* `LOG_LEVEL` - default to `INFO`
* `KAFKA_WAIT_RETRIES` - number of retries, default to `24`
* `KAFKA_WAIT_INTERVAL` - in seconds, default to `5`
"""

import logging
import os
import sys
import time

from pykafka import KafkaClient
from pykafka.exceptions import NoBrokersAvailableError

# Run this script only with Python 3
if sys.version_info.major != 3:
    sys.stdout.write("Sorry, requires Python 3.x\n")
    sys.exit(1)

LOG_LEVEL = logging.getLevelName(os.environ.get('LOG_LEVEL', 'INFO'))
logging.basicConfig(level=LOG_LEVEL)

logger = logging.getLogger(__name__)

KAFKA_HOSTS = os.environ.get('KAFKA_URI', 'kafka:9092')

REQUIRED_TOPICS = os.environ.get('KAFKA_WAIT_FOR_TOPICS', '') \
                            .encode('utf-8').split(b',')

KAFKA_WAIT_RETRIES = int(os.environ.get('KAFKA_WAIT_RETRIES', '24'))
KAFKA_WAIT_INTERVAL = int(os.environ.get('KAFKA_WAIT_INTERVAL', '5'))


class TopicNoPartition(Exception):
    """Raise when topic has no partitions."""


class TopicNotFound(Exception):
    """Raise when topic was not found."""


def retry(retries=KAFKA_WAIT_RETRIES, delay=KAFKA_WAIT_INTERVAL,
          check_exceptions=()):
    """Retry decorator."""
    def decorator(func):
        """Decorator."""
        def f_retry(*args, **kwargs):
            """Retry running function on exception after delay."""
            for i in range(1, retries + 1):
                try:
                    return func(*args, **kwargs)
                # pylint: disable=W0703
                # We want to catch all exceptions here to retry.
                except check_exceptions + (Exception,) as exc:
                    if i < retries:
                        logger.info('Connection attempt %d of %d failed',
                                    i, retries)
                        if isinstance(exc, check_exceptions):
                            logger.debug('Caught known exception, retrying...',
                                         exc_info=True)
                        else:
                            logger.warn(
                                'Caught unknown exception, retrying...',
                                exc_info=True)
                    else:
                        logger.exception('Failed after %d attempts', retries)

                        raise

                # No exception so wait before retrying
                time.sleep(delay)

        return f_retry
    return decorator


@retry(check_exceptions=(TopicNoPartition, TopicNotFound))
def check_topics(client, req_topics):
    """Check for existence of provided topics in Kafka."""
    client.update_cluster()
    logger.debug('Found topics: %r', client.topics.keys())

    for req_topic in req_topics:
        if req_topic not in client.topics.keys():
            err_topic_not_found = 'Topic not found: {}'.format(req_topic)
            logger.warning(err_topic_not_found)
            raise TopicNotFound(err_topic_not_found)

        topic = client.topics[req_topic]
        if not topic.partitions:
            err_topic_no_part = 'Topic has no partitions: {}'.format(req_topic)
            logger.warning(err_topic_no_part)
            raise TopicNoPartition(err_topic_no_part)

        logger.info('Topic is ready: %s', req_topic)


@retry(check_exceptions=(NoBrokersAvailableError,))
def connect_kafka(hosts):
    """Connect to Kafka with retries."""
    return KafkaClient(hosts=hosts)


def main():
    """Start main part of the wait script."""
    logger.info('Checking for available topics: %r', repr(REQUIRED_TOPICS))

    client = connect_kafka(hosts=KAFKA_HOSTS)
    check_topics(client, REQUIRED_TOPICS)


if __name__ == '__main__':
    main()
