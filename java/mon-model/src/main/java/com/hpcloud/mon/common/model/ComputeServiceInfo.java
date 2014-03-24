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
class ComputeServiceInfo implements ServiceInfo {
  private final Set<String> supportedMetricNames = Sets.newHashSet("cpu_time", "cpu_utilization",
      "disk_read_ops", "disk_read_ops_count", "disk_write_ops", "disk_write_ops_count",
      "disk_read_bytes", "disk_read_bytes_count", "disk_write_bytes", "disk_write_bytes_count",
      "net_in_bytes", "net_in_bytes_count", "net_out_bytes", "net_out_bytes_count",
      "net_in_packets", "net_in_packets_count", "net_out_packets", "net_out_packets_count",
      "net_in_dropped", "net_in_dropped_count", "net_out_dropped", "net_out_dropped_count",
      "net_in_errors", "net_in_errors_count", "net_out_errors", "net_out_errors_count");
  private final List<String> supportedDims = Arrays.asList("instance_id");

  @Override
  public String getResourceIdDimension() {
    return "instance_id";
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
