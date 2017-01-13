#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.

import mock

from oslotest import base

import monasca_common.repositories.exceptions as exceptions
from monasca_common.repositories.mysql import mysql_repository


class TestMySQLRepository(base.BaseTestCase):

    def setUp(self):
        super(TestMySQLRepository, self).setUp()
        self.cfg_patcher = mock.patch('oslo_config.cfg.CONF')

        self.mock_cfg = self.cfg_patcher.start()

    def tearDown(self):
        super(TestMySQLRepository, self).tearDown()
        self.cfg_patcher.stop()

    def test_init(self):
        mysql_repository_obj = mysql_repository.MySQLRepository()

        self.assertEqual(mysql_repository_obj.conf, self.mock_cfg)
        self.assertEqual(mysql_repository_obj.database_name,
                         self.mock_cfg.mysql.database_name)
        self.assertEqual(mysql_repository_obj.database_server,
                         self.mock_cfg.mysql.hostname)
        self.assertEqual(mysql_repository_obj.database_uid,
                         self.mock_cfg.mysql.username)
        self.assertEqual(mysql_repository_obj.database_pwd,
                         self.mock_cfg.mysql.password)

    def test_init_with_exception(self):
        self.mock_cfg.mysql = None

        self.assertRaises(exceptions.RepositoryException,
                          mysql_repository.MySQLRepository)

    @mock.patch('pymysql.cursors')
    @mock.patch('pymysql.connect')
    def test_execute_query(self, mock_pymysql_connect, mock_pymysql_cursors):
        query = mock.Mock()
        params = mock.Mock()
        ctx = mock_pymysql_connect.return_value
        cursor = ctx.cursor.return_value
        mysql_repository_obj = mysql_repository.MySQLRepository()

        mysql_repository_obj._execute_query(query, params)

        self.assertTrue(mock_pymysql_connect.called)
        ctx.cursor.assert_called_once_with(mock_pymysql_cursors.DictCursor)
        cursor.execute.assert_called_once_with(query, params)
        self.assertTrue(cursor.fetchall)

    def _test_mysql_try_catch_block_decorator_with_exception(
            self, exception, expected_exception_class=None):
        @mysql_repository.mysql_try_catch_block
        def raise_exception():
            raise exception

        if expected_exception_class is None:
            expected_exception_class = exception.__class__

        self.assertRaises(expected_exception_class, raise_exception)

    def test_mysql_try_catch_decorator_with_repository_exceptions(self):
        for exception in [exceptions.DoesNotExistException(),
                          exceptions.AlreadyExistsException(),
                          exceptions.InvalidUpdateException()]:
            self._test_mysql_try_catch_block_decorator_with_exception(
                exception)

    @mock.patch('monasca_common.repositories.mysql.mysql_repository.LOG')
    def test_mysql_try_catch_decorator_with_non_repository_exception(
            self, mock_log):
        class NonRepositoryException(Exception):
            pass
        exception = NonRepositoryException()

        self._test_mysql_try_catch_block_decorator_with_exception(
            exception, expected_exception_class=exceptions.RepositoryException)
        mock_log.exception.assert_called_once_with(exception)
