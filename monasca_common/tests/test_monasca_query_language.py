# (C) Copyright 2017 Hewlett Packard Enterprise Development LP
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
# implied.
# See the License for the specific language governing permissions and
# limitations under the License.

from oslotest import base
import pyparsing

from monasca_common.monasca_query_language import aql_parser
from monasca_common.monasca_query_language import exceptions
from monasca_common.monasca_query_language import query_structures


class TestMonascaQueryLanguage(base.BaseTestCase):

    def test_parse_group_expression(self):
        expressions = [
            "",
            "excluding metric_two",
            "group by hostname, service",
            "excluding metric_two group by hostname, service",
            "group by __severity__",
            "excluding {__severity__=HIGH} group by __severity__",
            "excluding {__severity__=HIGH, hostname=host1} group by __severity__, hostname",
            "group by excluding"  # excluding is an acceptable metric name
        ]
        negative_expressions = [
            "group by hostname excluding {__metricName__=metric_two}",
            "excluding metric_one excluding metric_two",
            "targets metric_one",
        ]
        matchers = [
            [],
            [],
            ["hostname", "service"],
            ["hostname", "service"],
            ["__severity__"],
            ["__severity__"],
            ["__severity__", "hostname"],
            ["excluding"]
        ]
        exclusions = [
            {},
            {"__metricName__": "metric_two"},
            {},
            {"__metricName__": "metric_two"},
            {},
            {"__severity__": "HIGH"},
            {"__severity__": "HIGH", "hostname": "host1"},
            {},
        ]
        for i in range(len(expressions)):
            result = aql_parser.RuleExpressionParser(expressions[i]).parse()
            result = result[0].get_struct("group")
            self.assertEqual(result['matchers'], matchers[i])
            self.assertEqual(result['exclusions'], exclusions[i])
        for negative_expression in negative_expressions:
            try:
                result = aql_parser.RuleExpressionParser(negative_expression)
                self.assertRaises(exceptions.InvalidExpressionException,
                                  result.parse())
            except TypeError:
                pass
            except pyparsing.ParseException:
                pass

    def test_parse_inhibit_rule(self):
        expressions = [
            "",
            "source metric_one",
            "targets metric_two",
            "source metric_one targets metric_two",
            "source metric_one targets metric_two excluding metric_three",
            "source metric_one targets metric_two excluding metric_three group by hostname",
            "source metric_one targets metric_two group by hostname",
            "source metric_one group by hostname",
            "source {__severity__=HIGH} targets {__severity__=LOW} excluding "
                "{__alarmName__=alarm_one} group by __alarmName__"
        ]
        negative_expressions = [
            "targets metric_two source_metric_one"
        ]
        source = [
            {},
            {"__metricName__": "metric_one"},
            {},
            {"__metricName__": "metric_one"},
            {"__metricName__": "metric_one"},
            {"__metricName__": "metric_one"},
            {"__metricName__": "metric_one"},
            {"__metricName__": "metric_one"},
            {"__severity__": "HIGH"},
        ]
        target = [
            {},
            {},
            {"__metricName__": "metric_two"},
            {"__metricName__": "metric_two"},
            {"__metricName__": "metric_two"},
            {"__metricName__": "metric_two"},
            {"__metricName__": "metric_two"},
            {},
            {"__severity__": "LOW"}
        ]
        equals = [
            [],
            [],
            [],
            [],
            [],
            ["hostname"],
            ["hostname"],
            ["hostname"],
            ["__alarmName__"]
        ]
        exclusions = [
            {},
            {},
            {},
            {},
            {"__metricName__": "metric_three"},
            {"__metricName__": "metric_three"},
            {},
            {},
            {"__alarmName__": "alarm_one"}
        ]
        for i in range(len(expressions)):
            result = aql_parser.RuleExpressionParser(expressions[i]).parse()
            result = result[0].get_struct("inhibit")
            self.assertEqual(result['source_match'], source[i])
            self.assertEqual(result['target_match'], target[i])
            self.assertEqual(result['equal'], equals[i])
            self.assertEqual(result['exclusions'], exclusions[i])

        for expression in negative_expressions:
            try:
                result = aql_parser.RuleExpressionParser(expression)
                self.assertRaises(exceptions.InvalidExpressionException,
                                  result.parse())
            except pyparsing.ParseException:
                pass

    def test_parse_silence_rule(self):
        expressions = [
            "",
            "targets metric_one",
            "targets metric_one{}",
            "targets metric_one{hostname=host_one}",
            "targets metric_one{hostname=host_one, region=region_one}",
        ]
        negative_expressions = [
            "excludes metric_one",
            "source metric_one",
            "group by hostname",
            "targets metric_one, {hostname=host_one}",
        ]
        matchers = [
            {},
            {"__metricName__": "metric_one"},
            {"__metricName__": "metric_one"},
            {"__metricName__": "metric_one", "hostname": "host_one"},
            {"__metricName__": "metric_one", "hostname": "host_one", "region": "region_one"},
        ]
        for i in range(len(expressions)):
            result = aql_parser.RuleExpressionParser(expressions[i]).parse()
            result = result[0].get_struct("silence")
            self.assertEqual(result['matchers'], matchers[i])

        for expression in negative_expressions:
            try:
                self.assertRaises(exceptions.InvalidExpressionException,
                                  aql_parser.RuleExpressionParser(expression).parse())
            except TypeError:
                pass
            except pyparsing.ParseException:
                pass
