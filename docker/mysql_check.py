#!/usr/bin/env python
# coding=utf-8

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

"""Health check for MySQL returns 0 when all checks works properly.

It's checking if requested database already exists.

For using this script you need to set some environment variables:
* `MYSQL_HOST` for connection string to MySQL.
  Example: `mysql`, `192.168.10.6`.
  Default: `mysql`.
* `MYSQL_PORT` for connection string to MySQL port.
  Default: `3306`.
* `MYSQL_USER` for user that is cappable to connect to MySQL.
  Default: `monapi`.
* `MYSQL_PASSWORD` for user password.
  Default: `password`.
* `MYSQL_DB` for database that you need to have before starting service.
  Default: `mon`.

After making sure that this environment variables are set you can simply
execute this script in the following way:
`python3 mysql_check.py && ./start_service.sh`
`python3 mysql_check.py || exit 1`

Additional environment variables available are:
* `LOG_LEVEL` - default to `INFO`
* `MYSQL_WAIT_RETRIES` - number of retries, default to `24`
* `MYSQL_WAIT_INTERVAL` - in seconds, default to `5`
"""

import logging
import os
import sys
import time

import pymysql

# Run this script only with Python 3
if sys.version_info.major != 3:
    sys.stdout.write("Sorry, requires Python 3.x\n")
    sys.exit(1)

LOG_LEVEL = logging.getLevelName(os.environ.get('LOG_LEVEL', 'INFO'))
logging.basicConfig(level=LOG_LEVEL)

logger = logging.getLogger(__name__)

MYSQL_HOST = os.environ.get('MYSQL_HOST', 'mysql')
MYSQL_PORT = int(os.environ.get('MYSQL_PORT', 3306))
MYSQL_USER = os.environ.get('MYSQL_USER', 'monapi')
MYSQL_PASSWORD = os.environ.get('MYSQL_PASSWORD', 'password')
MYSQL_DB = os.environ.get('MYSQL_DB', 'mon')

MYSQL_WAIT_RETRIES = int(os.environ.get('MYSQL_WAIT_RETRIES', '24'))
MYSQL_WAIT_INTERVAL = int(os.environ.get('MYSQL_WAIT_INTERVAL', '5'))


def retry(retries=MYSQL_WAIT_RETRIES, delay=MYSQL_WAIT_INTERVAL,
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


@retry(check_exceptions=(pymysql.err.OperationalError,))
def connect_mysql(host, port, user, password, database):
    """Connect to MySQL with retries."""
    return pymysql.connect(
        host=host, port=port,
        user=user, passwd=password,
        db=database
    )


def main():
    """Start main part of the wait script."""
    logger.info('Waiting for database: `%s`', MYSQL_DB)

    connect_mysql(
        host=MYSQL_HOST, port=MYSQL_PORT,
        user=MYSQL_USER, password=MYSQL_PASSWORD,
        database=MYSQL_DB
    )

    logger.info('Database `%s` found', MYSQL_DB)


if __name__ == '__main__':
    main()
