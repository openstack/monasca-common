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


from monasca_common.kafka import consumer, legacy_kafka_message


class LegacyKafkaConsumer(consumer.KafkaConsumer):
    def __iter__(self):
        """:return: Kafka message object compatible with Confluent Kafka client
        object
        """
        for raw_message in super(LegacyKafkaConsumer, self).__iter__():
            yield legacy_kafka_message.LegacyKafkaMessage(raw_message)
