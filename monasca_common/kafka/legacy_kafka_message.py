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


class LegacyKafkaMessage(object):

    def __init__(self, raw_message):
        self.m_partition = raw_message[0]
        self.m_offset = raw_message[1].offset
        self.m_key = raw_message[1].message.key
        self.m_value = raw_message[1].message.value

    def key(self):
        return self.m_key

    def offset(self):
        return self.m_offset

    def partition(self):
        return self.m_partition

    def value(self):
        return self.m_value
