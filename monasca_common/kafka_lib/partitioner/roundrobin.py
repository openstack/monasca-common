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

from itertools import cycle

from .base import Partitioner


class RoundRobinPartitioner(Partitioner):
    """
    Implements a round robin partitioner which sends data to partitions
    in a round robin fashion
    """
    def __init__(self, partitions):
        super(RoundRobinPartitioner, self).__init__(partitions)
        self.iterpart = cycle(partitions)

    def _set_partitions(self, partitions):
        self.partitions = partitions
        self.iterpart = cycle(partitions)

    def partition(self, key, partitions=None):
        # Refresh the partition list if necessary
        if partitions and self.partitions != partitions:
            self._set_partitions(partitions)

        return next(self.iterpart)
