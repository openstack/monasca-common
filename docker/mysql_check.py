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

After making sure that this environment variables are set you can simply
execute this script in the following way:
`python3 mysql_check.py && ./start_service.sh`
`python3 mysql_check.py || exit 1`
"""

import logging
import os
import sys

import pymysql

# Run this script only with Python 3
if sys.version_info.major != 3:
    sys.stdout.write("Sorry, requires Python 3.x\n")
    sys.exit(1)

LOG_LEVEL = logging.getLevelName(os.environ.get('LOG_LEVEL', 'INFO'))
logging.basicConfig(level=LOG_LEVEL)

logger = logging.getLogger(__name__)

MYSQL_HOST = os.environ.get('MYSQL_HOST', 'mysql')
MYSQL_PORT = os.environ.get('MYSQL_HOST', 3306)
MYSQL_USER = os.environ.get('MYSQL_USER', 'monapi')
MYSQL_PASSWORD = os.environ.get('MYSQL_PASSWORD', 'password')
MYSQL_DB = os.environ.get('MYSQL_DB', 'mon')

MYSQL_WAIT_RETRIES = int(os.environ.get('MYSQL_WAIT_RETRIES', '24'))
MYSQL_WAIT_INTERVAL = int(os.environ.get('MYSQL_WAIT_INTERVAL', '5'))

# TODO(Dobroslaw): All checks and retry.
db = pymysql.connect(
    host=MYSQL_HOST, port=MYSQL_PORT,
    user=MYSQL_USER, passwd=MYSQL_PASSWORD,
    db=MYSQL_DB
)
