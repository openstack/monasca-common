package com.hpcloud.mon.common.model;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Compute namespace info.
 * 
 * @author Jonathan Halterman
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
