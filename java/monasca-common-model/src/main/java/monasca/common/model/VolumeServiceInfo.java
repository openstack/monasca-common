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
 * Compute namespace info.
 */
class VolumeServiceInfo implements ServiceInfo {
  private final Set<String> supportedMetricNames = Sets.newHashSet("volume_read_ops",
      "volume_write_ops", "volume_read_bytes", "volume_write_bytes", "volume_read_time",
      "volume_write_time", "volume_idle_time");
  private final List<String> supportedDims = Arrays.asList("instance_id", "disk");

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
