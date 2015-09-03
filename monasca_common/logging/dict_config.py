# (C) Copyright 2015 HP Development Company, L.P.
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
LOG_FORMAT = '%(process)d %(asctime)s %(levelname)s %(name)s %(message)s'


def get_config(conf):
    log_config = {
        'version': 1,
        'disable_existing_loggers': False,
        'formatters': {
            'default': {
                'format': LOG_FORMAT
            }
        },
        'handlers': {
            'console': {
                'class': "logging.StreamHandler",
                'formatter': "default"
            },
            'file': {
                'class': "logging.handlers.RotatingFileHandler",
                'filename': conf.logging.file,
                'formatter': "default",
                'maxBytes': conf.logging.size,
                'backupCount': conf.logging.backup
            },
        },
        'loggers': {
            'kazoo': {'level': conf.logging.kazoo},
            'kafka': {'level': conf.logging.kafka},
            'statsd': {'level': conf.logging.statsd},
            'iso8601': {'level': conf.logging.iso8601}
        },
        'root': {
            'handlers': ['console'],
            'level': conf.logging.level
        }
    }
    return log_config
