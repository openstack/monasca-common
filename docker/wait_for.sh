#!/bin/sh

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
  host=${var%:*}
  port=${var#*:}
  wait_for "$host" "$port"
done
