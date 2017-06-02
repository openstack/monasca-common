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

import pyparsing

from monasca_common.monasca_query_language import exceptions


class Dimension(object):
    def __init__(self, tokens):
        self.args = tokens
        self.key = tokens[0]
        self.operator = tokens[1]
        self.value = tokens[2]

    def __str__(self):
        return "Dimension(key={},operator='{}',value={})".format(
            self.key, self.operator, self.value)


class MetricSelector(object):
    def __init__(self, tokens):
        self.args = tokens
        self.name = None
        self.dimensions = {}
        _dimensions = []
        for token in tokens:
            if isinstance(token, str):
                self.name = token
            elif isinstance(token, pyparsing.ParseResults):
                _dimensions = token

        for dim in _dimensions:
            self.dimensions[dim.key] = dim.value
        if self.name is not None:
            self.dimensions["__metricName__"] = self.name

    def get_filters(self):
        return self.dimensions

    def __repr__(self):
        return "MetricSelector(name={},dimensions={})".format(
            self.name, self.dimensions)

    def __str__(self):
        return self.__repr__()


class LogicalExpression(object):
    def __init__(self, tokens):
        self.args = tokens
        self.left_operand = tokens[0][0]
        self.operator = None
        self.right_operand = None
        if len(tokens[0]) > 1:
            self.operator = tokens[0][1]
        if len(tokens[0]) > 2:
            self.right_operand = tokens[0][2]

    @property
    def normalized_operator(self):
        if self.operator == '&&':
            result = 'and'
        elif self.operator == '||':
            result = 'or'
        else:
            result = self.operator
        return result

    def get_filters(self):
        left_filters = self.left_operand.get_filters()
        right_filters = self.right_operand.get_filters()
        for key, value in right_filters.items():
            if key in left_filters and left_filters[key] != value:
                raise exceptions.InvalidExpressionException(
                    "Duplicate keys specified ".format(key))
            left_filters[key] = value
        return left_filters

    def __str__(self):
        return "LogicalExpression(left={},operator='{}',right={})".format(
            self.left_operand, self.operator, self.right_operand)
        return self.__repr__()


class SourceExpression(object):
    def __init__(self, tokens):
        self.args = tokens
        self.source = tokens[1]

    def get_filters(self):
        return self.source.get_filters()

    def __str__(self):
        return "SourceExpression(source={})".format(self.source)


class TargetsExpression(object):
    def __init__(self, tokens):
        self.args = tokens
        self.target = tokens[1]

    def get_filters(self):
        return self.target.get_filters()

    def __str__(self):
        return "TargetExpression(target={})".format(self.target)


class ExcludesExpression(object):
    def __init__(self, tokens):
        self.args = tokens
        self.exclude = tokens[1]

    def get_filters(self):
        return self.exclude.get_filters()

    def __str__(self):
        return "ExcludesExpression(exclude={})".format(self.exclude)


class GroupByExpression(object):
    def __init__(self, tokens):
        self.args = tokens
        self.group_keys = tokens[1:]

    def get_filters(self):
        return self.group_keys

    def __str__(self):
        return "GroupByExpression({})".format(self.group_keys)


class Rule(object):
    def __init__(self, tokens):
        self.source = None
        self.target = None
        self.excludes = None
        self.group_by = None
        for token in tokens:
            if isinstance(token, SourceExpression):
                self.source = token
            elif isinstance(token, TargetsExpression):
                self.target = token
            elif isinstance(token, ExcludesExpression):
                self.excludes = token
            elif isinstance(token, GroupByExpression):
                self.group_by = token

    def get_struct(self, _type):
        result = {}
        if _type == "silence":
            result['matchers'] = self.target.get_filters() if self.target is not None else {}
            if any([self.source, self.group_by, self.excludes]):
                raise exceptions.InvalidExpressionException(
                    "Silence rule contains unexpected elements")
        elif _type == "inhibit":
            result['source_match'] = self.source.get_filters() if self.source is not None else {}
            result['target_match'] = self.target.get_filters() if self.target is not None else {}
            result['equal'] = self.group_by.get_filters() if self.group_by is not None else []
            result['exclusions'] = self.excludes.get_filters() if self.excludes is not None else {}
        elif _type == "group":
            result['matchers'] = self.group_by.get_filters() if self.group_by is not None else []
            result['exclusions'] = self.excludes.get_filters() if self.excludes is not None else {}
            if any([self.source, self.target]):
                raise exceptions.InvalidExpressionException(
                    "Group rule contains unexpected elements")
        else:
            raise exceptions.InvalidExpressionException("Unknown type for expression")
        return result

    def __str__(self):
        return "Rule(source={},target={},excludes={},group_by={})".format(
            self.source, self.target, self.excludes, self.group_by)
