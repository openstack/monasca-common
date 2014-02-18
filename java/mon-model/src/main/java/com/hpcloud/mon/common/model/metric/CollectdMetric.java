package com.hpcloud.mon.common.model.metric;

import java.util.Arrays;

/**
 * Collectd Metric.
 * 
 * @author Jonathan Halterman
 */
public class CollectdMetric {
  public String host;
  public String plugin;
  public String pluginInstance;
  public String type;
  public String typeInstance;
  public long time;
  public long interval;
  public String[] dsnames;
  public String[] dstypes;
  public double[] values;

  public CollectdMetric() {
  }

  public CollectdMetric(String host, String plugin, String pluginInstance, String type,
      String typeInstance, long time, long interval, String[] dsnames, String[] dstypes, 
      double[] values) {
    this.host = host;
    this.plugin = plugin;
    this.pluginInstance = pluginInstance;
    this.type = type;
    this.typeInstance = typeInstance;
    this.time = time;
    this.interval = interval;
    this.dsnames = dsnames;
    this.dstypes = dstypes;
    this.values = values;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CollectdMetric other = (CollectdMetric) obj;
    if (!Arrays.equals(dsnames, other.dsnames))
      return false;
    if (!Arrays.equals(dstypes, other.dstypes))
      return false;
    if (host == null) {
      if (other.host != null)
        return false;
    } else if (!host.equals(other.host))
      return false;
    if (interval != other.interval)
      return false;
    if (plugin == null) {
      if (other.plugin != null)
        return false;
    } else if (!plugin.equals(other.plugin))
      return false;
    if (pluginInstance == null) {
      if (other.pluginInstance != null)
        return false;
    } else if (!pluginInstance.equals(other.pluginInstance))
      return false;
    if (time != other.time)
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    if (typeInstance == null) {
      if (other.typeInstance != null)
        return false;
    } else if (!typeInstance.equals(other.typeInstance))
      return false;
    if (!Arrays.equals(values, other.values))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(dsnames);
    result = prime * result + Arrays.hashCode(dstypes);
    result = prime * result + ((host == null) ? 0 : host.hashCode());
    result = prime * result + (int) (interval ^ (interval >>> 32));
    result = prime * result + ((plugin == null) ? 0 : plugin.hashCode());
    result = prime * result + ((pluginInstance == null) ? 0 : pluginInstance.hashCode());
    result = prime * result + (int) (time ^ (time >>> 32));
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((typeInstance == null) ? 0 : typeInstance.hashCode());
    result = prime * result + Arrays.hashCode(values);
    return result;
  }

  @Override
  public String toString() {
    return String.format(
        "CollectdMetric [host=%s, plugin=%s, pluginInstance=%s, type=%s, typeInstance=%s, time=%s, interval=%s, dsnames=%s, dstypes=%s, values=%s]",
        host, plugin, pluginInstance, type, typeInstance, time, interval, Arrays.toString(dsnames),
        Arrays.toString(dstypes), Arrays.toString(values));
  }
}
