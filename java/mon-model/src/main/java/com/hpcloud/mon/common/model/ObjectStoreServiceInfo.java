package com.hpcloud.mon.common.model;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Object-store namespace info.
 * 
 * @author Jonathan Halterman
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
