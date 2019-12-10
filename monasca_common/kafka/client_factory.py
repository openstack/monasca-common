#  Licensed under the Apache License, Version 2.0 (the "License"); you may
#  not use this file except in compliance with the License. You may obtain
#  a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
#  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
#  License for the specific language governing permissions and limitations
#  under the License.

from monasca_common.confluent_kafka import consumer
from monasca_common.confluent_kafka import producer
from monasca_common.kafka import legacy_kafka_consumer
from monasca_common.kafka import producer as legacy_kafka_producer


def get_kafka_consumer(kafka_url,
                       kafka_consumer_group,
                       kafka_topic,
                       zookeeper_url=None,
                       zookeeper_path=None,
                       use_legacy_client=False,
                       repartition_callback=None,
                       commit_callback=None,
                       max_commit_interval=30,
                       client_id=""):
    if use_legacy_client:
        return legacy_kafka_consumer.LegacyKafkaConsumer(
            kafka_url,
            ','.join(zookeeper_url),
            zookeeper_path,
            kafka_consumer_group,
            kafka_topic,
            repartition_callback=repartition_callback,
            commit_callback=commit_callback,
            commit_timeout=max_commit_interval
        )
    else:
        return consumer.KafkaConsumer(
            ",".join(kafka_url),
            kafka_consumer_group,
            kafka_topic,
            client_id=client_id,
            repartition_callback=repartition_callback,
            commit_callback=commit_callback,
            max_commit_interval=max_commit_interval
        )


def get_kafka_producer(kafka_url, use_legacy_client=False, **config):
    if use_legacy_client:
        return legacy_kafka_producer.KafkaProducer(kafka_url)
    else:
        return producer.KafkaProducer(",".join(kafka_url), **config)
