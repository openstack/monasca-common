#!/bin/sh

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

# This script will return 0 when on specific address (like 192.168.10.6:5000)
# scanning will reveal that port is responding.
#
# Example usage:
#   ./wait_for.sh 192.168.10.6:5000 && ./start_service.sh
#   ./wait_for.sh 192.168.10.6:5000 || exit 1
#
# By default this script will check up to 24 times every 5 seconds.
# You can overwrite this values with environment variables:
#   `WAIT_RETRIES`
#   `WAIT_INTERVAL`

: "${WAIT_RETRIES:=24}"
: "${WAIT_INTERVAL:=5}"

wait_for() {
  echo "Waiting for $1 to listen on $2..."

  for i in $(seq $WAIT_RETRIES)
  do
    nc -z "$1" "$2" && return
    echo "$1 not yet ready (attempt $i of $WAIT_RETRIES)"
    sleep "$WAIT_INTERVAL"
  done
  echo "$1 failed to become ready, exiting..."
  exit 1
}

for var in "$@"
do
  # nc does not work with links containing http(s), remove trailing slash
  clean_link=$(echo "$var" | sed -e "s/^http[s]*:\/\///" | sed 's:/*$::')
  host=${clean_link%:*}
  port=${clean_link#*:}
  wait_for "$host" "$port"
done
