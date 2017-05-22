# (C) Copyright 2016-2017 Hewlett Packard Enterprise Development LP
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
import six

from monasca_common.validation import metrics as metric_validator

# a few valid characters to test
valid_name_chars = ".'_-"
invalid_name_chars = " <>={}(),\"\\\\;&"

# a few valid characters to test
valid_dimension_chars = " .'_-"
invalid_dimension_chars = "<>={}(),\"\\\\;&"


class TestMetricValidation(base.BaseTestCase):
    def test_valid_single_metric(self):
        metric = {"name": "test_metric_name",
                  "dimensions": {"key1": "value1",
                                 "key2": "value2"},
                  "timestamp": 1405630174123,
                  "value": 5}
        metric_validator.validate(metric)

    def test_valid_metrics(self):
        metrics = [
            {"name": "name1",
             "dimensions": {"key1": "value1",
                            "key2": "value2"},
             "timestamp": 1405630174123,
             "value": 1.0},
            {"name": "name2",
             "dimensions": {"key1": "value1",
                            "key2": "value2"},
             "value_meta": {"key1": "value1",
                            "key2": "value2"},
             "timestamp": 1405630174123,
             "value": 2.0}
        ]
        metric_validator.validate(metrics)

    def test_valid_metric_unicode_dimension_value(self):
        metric = {"name": "test_metric_name",
                  "timestamp": 1405630174123,
                  "dimensions": {six.unichr(2440): 'B', 'B': 'C', 'D': 'E'},
                  "value": 5}
        metric_validator.validate(metric)

    def test_valid_metric_unicode_dimension_key(self):
        metric = {"name": 'test_metric_name',
                  "dimensions": {'A': 'B', 'B': six.unichr(920), 'D': 'E'},
                  "timestamp": 1405630174123,
                  "value": 5}
        metric_validator.validate(metric)

    def test_valid_metric_unicode_metric_name(self):
        metric = {"name": six.unichr(6021),
                  "dimensions": {"key1": "value1",
                                 "key2": "value2"},
                  "timestamp": 1405630174123,
                  "value": 5}
        metric_validator.validate(metric)

    def test_invalid_metric_name(self):
        metric = {'name': "TooLarge" * 255,
                  "dimensions": {"key1": "value1",
                                 "key2": "value2"},
                  "timestamp": 1405630174123,
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidMetricName,
            "invalid length for metric name",
            metric_validator.validate, metric)

    def test_invalid_metric_name_empty(self):
        metric = {"name": "",
                  "dimensions": {"key1": "value1",
                                 "key2": "value2"},
                  "timestamp": 1405630174123,
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidMetricName,
            "invalid length for metric name",
            metric_validator.validate, metric)

    def test_invalid_metric_name_non_str(self):
        metric = {"name": 133,
                  "dimensions": {"key1": "value1",
                                 "key2": "value2"},
                  "timestamp": 1405630174123,
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidMetricName,
            "invalid metric name type",
            metric_validator.validate,
            metric)

    def test_invalid_metric_restricted_characters(self):
        metric = {"name": '"Foo"',
                  "dimensions": {"key1": "value1",
                                 "key2": "value2"},
                  "timestamp": 1405630174123,
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidMetricName,
            "invalid characters in metric name",
            metric_validator.validate, metric)

    def test_invalid_dimension_empty_key(self):
        metric = {"name": "test_metric_name",
                  "dimensions": {'A': 'B', '': 'C', 'D': 'E'},
                  "timestamp": 1405630174123,
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidDimensionKey,
            "invalid length \(0\) for dimension key",
            metric_validator.validate, metric)

    def test_invalid_dimension_empty_value(self):
        metric = {"name": "test_metric_name",
                  "dimensions": {'A': 'B', 'B': 'C', 'D': ''},
                  "timestamp": 1405630174123,
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidDimensionValue,
            "invalid length \(0\) for dimension value",
            metric_validator.validate, metric)

    def test_invalid_dimension_non_str_key(self):
        metric = {"name": "test_metric_name",
                  "dimensions": {'A': 'B', 4: 'C', 'D': 'E'},
                  "timestamp": 1405630174123,
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidDimensionKey,
            "invalid dimension key type",
            metric_validator.validate, metric)

    def test_invalid_dimension_non_str_value(self):
        metric = {"name": "test_metric_name",
                  "dimensions": {'A': 13.3, 'B': 'C', 'D': 'E'},
                  "timestamp": 1405630174123,
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidDimensionValue,
            "invalid dimension value type",
            metric_validator.validate, metric)

    def test_invalid_dimension_key_length(self):
        metric = {"name": "test_metric_name",
                  "dimensions": {'A'*256: 'B', 'B': 'C', 'D': 'E'},
                  "timestamp": 1405630174123,
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidDimensionKey,
            "invalid length \(256\) for dimension key",
            metric_validator.validate, metric)

    def test_invalid_dimension_value_length(self):
        metric = {"name": "test_metric_name",
                  "dimensions": {'A': 'B', 'B': 'C'*256, 'D': 'E'},
                  "timestamp": 1405630174123,
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidDimensionValue,
            "invalid length \(256\) for dimension value",
            metric_validator.validate, metric)

    def test_invalid_dimension_key_restricted_characters(self):
        metric = {"name": "test_metric_name",
                  "dimensions": {'A': 'B', 'B': 'C', '(D)': 'E'},
                  "timestamp": 1405630174123,
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidDimensionKey,
            "invalid characters in dimension key",
            metric_validator.validate, metric)

    def test_invalid_dimension_value_restricted_characters(self):
        metric = {"name": "test_metric_name",
                  "dimensions": {'A': 'B;', 'B': 'C', 'D': 'E'},
                  "timestamp": 1405630174123,
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidDimensionValue,
            "invalid characters in dimension value",
            metric_validator.validate, metric)

    def test_invalid_dimension_key_leading_underscore(self):
        metric = {"name": "test_metric_name",
                  "dimensions": {'_A': 'B', 'B': 'C', 'D': 'E'},
                  "timestamp": 1405630174123,
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidDimensionKey,
            "invalid characters in dimension key",
            metric_validator.validate, metric)

    def test_invalid_value_type(self):
        metric = {"name": "test_metric_name",
                  "dimensions": {"key1": "value1",
                                 "key2": "value2"},
                  "timestamp": 1405630174123,
                  "value": "value"}
        self.assertRaisesRegex(
            metric_validator.InvalidValue,
            "invalid value type",
            metric_validator.validate, metric)

    def test_invalid_value(self):
        metric = {"name": "test_metric_name",
                  "dimensions": {"key1": "value1",
                                 "key2": "value2"},
                  "timestamp": 1405630174123,
                  "value": None}

        for value in ('nan', 'inf', '-inf'):
            metric['value'] = float(value)
            self.assertRaisesRegex(
                metric_validator.InvalidValue,
                value,
                metric_validator.validate, metric)

    def test_valid_name_chars(self):
        for c in valid_name_chars:
            metric = {"name": 'test{}counter'.format(c),
                      "dimensions": {"key1": "value1",
                                     "key2": "value2"},
                      "timestamp": 1405630174123,
                      "value": 5}
            metric_validator.validate(metric)

    def test_invalid_name_chars(self):
        for c in invalid_name_chars:
            metric = {"name": 'test{}counter'.format(c),
                      "dimensions": {"key1": "value1",
                                     "key2": "value2"},
                      "timestamp": 1405630174123,
                      "value": 5}
            self.assertRaisesRegex(
                metric_validator.InvalidMetricName,
                "invalid characters in metric name",
                metric_validator.validate, metric)

    def test_valid_dimension_chars(self):
        for c in valid_dimension_chars:
            metric = {"name": "test_name",
                      "dimensions":
                          {"test{}key".format(c): "test{}value".format(c)},
                      "timestamp": 1405630174123,
                      "value": 5}
            metric_validator.validate(metric)

    def test_invalid_dimension_key_chars(self):
        for c in invalid_dimension_chars:
            metric = {"name": "test_name",
                      "dimensions": {'test{}key'.format(c): 'test-value'},
                      "timestamp": 1405630174123,
                      "value": 5}
            self.assertRaisesRegex(
                metric_validator.InvalidDimensionKey,
                "invalid characters in dimension key",
                metric_validator.validate, metric)

    def test_invalid_dimension_value_chars(self):
        for c in invalid_dimension_chars:
            metric = {"name": "test_name",
                      "dimensions":  {'test-key': 'test{}value'.format(c)},
                      "timestamp": 1405630174123,
                      "value": 5}
            self.assertRaisesRegex(
                metric_validator.InvalidDimensionValue,
                "invalid characters in dimension value",
                metric_validator.validate, metric)

    def test_invalid_too_many_value_meta(self):
        value_meta = {}
        for i in six.moves.range(0, 17):
            value_meta['key{}'.format(i)] = 'value{}'.format(i)
        metric = {"name": "test_metric_name",
                  "dimensions": {"key1": "value1",
                                 "key2": "value2"},
                  "value_meta": value_meta,
                  "timestamp": 1405630174123,
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidValueMeta,
            "Too many valueMeta entries",
            metric_validator.validate, metric)

    def test_invalid_empty_value_meta_key(self):
        metric = {"name": "test_metric_name",
                  "dimensions": {"key1": "value1",
                                 "key2": "value2"},
                  "value_meta": {'': 'BBB'},
                  "timestamp": 1405630174123,
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidValueMeta,
            "valueMeta name cannot be empty",
            metric_validator.validate, metric)

    def test_invalid_too_long_value_meta_key(self):
        key = "K"
        for i in six.moves.range(0, metric_validator.VALUE_META_NAME_MAX_LENGTH):
            key = "{}{}".format(key, "1")
        value_meta = {key: 'BBB'}
        metric = {"name": "test_metric_name",
                  "dimensions": {"key1": "value1",
                                 "key2": "value2"},
                  "value_meta": value_meta,
                  "timestamp": 1405630174123,
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidValueMeta,
            "valueMeta name too long",
            metric_validator.validate, metric)

    def test_invalid_too_large_value_meta(self):
        value_meta_value = ""
        num_value_meta = 10
        for i in six.moves.range(0, int(metric_validator.VALUE_META_VALUE_MAX_LENGTH / num_value_meta)):
            value_meta_value = '{}{}'.format(value_meta_value, '1')
        value_meta = {}
        for i in six.moves.range(0, num_value_meta):
            value_meta['key{}'.format(i)] = value_meta_value
        metric = {"name": "test_metric_name",
                  "dimensions": {"key1": "value1",
                                 "key2": "value2"},
                  "value_meta": value_meta,
                  "timestamp": 1405630174123,
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidValueMeta,
            "Unable to serialize valueMeta into JSON",
            metric_validator.validate, metric)

    def test_invalid_timestamp(self):
        metric = {'name': 'test_metric_name',
                  "dimensions": {"key1": "value1",
                                 "key2": "value2"},
                  "timestamp": "invalid_timestamp",
                  "value": 5}
        self.assertRaisesRegex(
            metric_validator.InvalidTimeStamp,
            "invalid timestamp type",
            metric_validator.validate, metric)

    def test_valid_metrics_by_components(self):
        metrics = [
            {"name": "name1",
             "dimensions": {"key1": "value1",
                            "key2": "value2"},
             "timestamp": 1405630174123,
             "value": 1.0},
            {"name": "name2",
             "dimensions": {"key1": "value1",
                            "key2": "value2"},
             "value_meta": {"key1": "value1",
                            "key2": "value2"},
             "timestamp": 1405630174123,
             "value": 2.0}
        ]
        for i in six.moves.range(len(metrics)):
            metric_validator.validate_name(metrics[i]['name'])
            metric_validator.validate_value(metrics[i]['value'])
            metric_validator.validate_timestamp(metrics[i]['timestamp'])
            if 'dimensions' in metrics[i]:
                metric_validator.validate_dimensions(metrics[i]['dimensions'])
            if 'value_meta' in metrics[i]:
                metric_validator.validate_value_meta(metrics[i]['value_meta'])
