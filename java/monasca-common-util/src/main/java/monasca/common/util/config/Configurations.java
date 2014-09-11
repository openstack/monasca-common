/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package monasca.common.util.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.google.common.base.Joiner;

public final class Configurations {
  private static final Joiner DOT_JOINER = Joiner.on(".");

  private Configurations() {
  }

  public static Map<String, String> configFor(String keyPrefix, Object source) {
    Map<String, String> config = new HashMap<String, String>();
    buildConfigFor(keyPrefix, config, new ObjectMapper().valueToTree(source));
    return config;
  }

  private static void buildConfigFor(String path, Map<String, String> config, JsonNode node) {
    for (Iterator<Map.Entry<String, JsonNode>> i = node.fields(); i.hasNext();) {
      Map.Entry<String, JsonNode> field = i.next();
      if (field.getValue() instanceof ValueNode) {
        ValueNode valueNode = (ValueNode) field.getValue();
        config.put(DOT_JOINER.join(path, field.getKey()), valueNode.asText());
      } else if (field.getValue() instanceof ArrayNode) {
        StringBuilder combinedValue = new StringBuilder();
        ArrayNode arrayNode = (ArrayNode) field.getValue();
        for (Iterator<JsonNode> it = arrayNode.elements(); it.hasNext();) {
          String value = it.next().asText().replaceAll("^\"|\"$", "");
          if (combinedValue.length() > 0)
            combinedValue.append(',');
          combinedValue.append(value);
        }

        config.put(DOT_JOINER.join(path, field.getKey()), combinedValue.toString());
      }

      buildConfigFor(DOT_JOINER.join(path, field.getKey()), config, field.getValue());
    }
  }
}
