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
package monasca.common.model;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Object-store namespace info.
 */
class ObjectStoreServiceInfo implements ServiceInfo {
  private final Set<String> supportedMetricNames = Sets.newHashSet("project_write_bytes",
      "project_read_bytes", "project_put_ops", "project_get_ops", "container_write_bytes",
      "container_read_bytes", "container_put_ops", "container_get_ops",
      "container_write_bytes_proxy", "container_read_bytes_proxy", "container_put_ops_proxy",
      "container_get_ops_proxy", "project_bytes_used", "container_bytes_used", "number_of_objects",
      "number_of_containers", "projects_bytes_used_replica", "container_bytes_used_replica",
      "number_of_objects_replica", "number_of_containers_replica");
  private final List<String> supportedDims = Arrays.asList("container");

  @Override
  public String getResourceIdDimension() {
    return "container";
  }

  @Override
  public List<String> getSupportedDimensions() {
    return supportedDims;
  }

  @Override
  public Set<String> getSupportedMetricNames() {
    return supportedMetricNames;
  }
}
