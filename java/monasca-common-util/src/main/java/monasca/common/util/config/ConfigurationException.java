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

/**
 * An exception thrown where there is an error parsing a configuration object.
 */
public class ConfigurationException extends Exception {
  private static final long serialVersionUID = 5325162099634227047L;

  /**
   * Creates a new ConfigurationException for the given file with the given errors.
   *
   * @param file the bad configuration file
   * @param errors the errors in the file
   */
  public ConfigurationException(String file, Iterable<String> errors) {
    super(formatMessage(file, errors));
  }

  private static String formatMessage(String file, Iterable<String> errors) {
    final StringBuilder msg = new StringBuilder(file).append(" has the following errors:\n");
    for (String error : errors) {
      msg.append("  * ").append(error).append('\n');
    }
    return msg.toString();
  }
}
