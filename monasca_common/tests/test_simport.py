# Copyright (c) 2014 Dark Secret Software Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
# implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import sys
import unittest

import monasca_common.simport.simport as simport


# Internal functions and classes.
def dummy_function():
    pass


class DummyClass(object):
    def method_a(self):
        pass


class LocalClass(object):
    def my_method(self):
        pass


class TestSimport(unittest.TestCase):
    def test_bad_targets(self):
        self.assertRaises(simport.BadDirectory, simport._get_module,
                          "|foo.Class")

        self.assertRaises(simport.MissingModule, simport._get_module,
                          "missing|")

        self.assertRaises(simport.MissingModule, simport._get_module,
                          "simport_tests|")
        self.assertRaises(simport.MissingModule, simport._get_module,
                          "simport_tests|Foo")
        self.assertRaises(simport.BadDirectory, simport._get_module,
                          "/does/not/exist|foo:Class")

        self.assertFalse("AnyModuleName" in sys.modules)
        self.assertRaises(simport.MissingMethodOrFunction, simport._get_module,
                          "tests|AnyModuleName:")
        self.assertFalse("AnyModuleName" in sys.modules)

    def test_good_external_targets(self):
        self.assertEqual(("localmodule", "Foo", "method_a"),
                         simport._get_module("tests|"
                                             "localmodule:Foo.method_a"))

        self.assertRaises(simport.ImportFailed, simport._get_module,
                          "tests|that_module:function_a")

    def test_bad_load(self):
        self.assertRaises(AttributeError, simport.load,
                          "TestSimport:missing")

    def test_good_load_internal(self):
        self.assertEqual(dummy_function,
                         simport.load("TestSimport:dummy_function"))
        self.assertEqual(DummyClass.method_a,
                         simport.load("TestSimport:DummyClass.method_a"))

    def test_good_load_local(self):
        method = simport.load("tests|"
                              "localmodule:Foo.method_a")
        import localmodule

        self.assertEqual(method, localmodule.Foo.method_a)
        self.assertEqual(localmodule.function_a,
                         simport.load("localmodule:function_a"))

    def test_good_load_external(self):
        method = simport.load("tests/external|"
                              "external.externalmodule:Blah.method_b")

        self.assertTrue('external.externalmodule' in sys.modules)
        old = sys.modules['external.externalmodule']
        import external.externalmodule

        self.assertEqual(external.externalmodule,
                         sys.modules['external.externalmodule'])
        self.assertEqual(old, external.externalmodule)
        self.assertEqual(method, external.externalmodule.Blah.method_b)

    def test_import_class(self):
        klass = simport.load("tests/external|"
                             "external.externalmodule:Blah")
        import external.externalmodule

        self.assertEqual(klass, external.externalmodule.Blah)

    def test_local_class(self):
        klass = simport.load("LocalClass", __name__)
        self.assertEqual(klass, LocalClass)
