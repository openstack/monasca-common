package com.hpcloud.collectd;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.collectd.api.DataSet;
import org.collectd.api.DataSource;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.hpcloud.util.Exceptions;

/**
 * CollectdTypesDB is a singleton that loads the types.db and hpcs_types.db resources into a map.
 * types.db contains number of values, dsnames, and dstypes, accessed by metric "type".
 */
public class CollectdTypesDB {
  private static volatile CollectdTypesDB TYPES_DB;
  private Map<String, List<DataSource>> typesMap = new HashMap<String, List<DataSource>>();

  public static CollectdTypesDB getInstance() {
    if (TYPES_DB == null) {
      synchronized (CollectdTypesDB.class) {
        if (TYPES_DB == null) {
          TYPES_DB = new CollectdTypesDB();
          try {
            TYPES_DB.load(Resources.getResource("collectd/types.db"));
            TYPES_DB.load(Resources.getResource("collectd/hpcs_types.db"));
          } catch (IOException e) {
            throw Exceptions.uncheck(e, "Error while loading collectd types");
          }
        }
      }
    }

    return TYPES_DB;
  }

  public List<DataSource> getType(String name) {
    return typesMap.get(name);
  }

  public Map<String, List<DataSource>> getTypes() {
    return typesMap;
  }

  public void load(URL url) throws IOException {
    List<String> lines = Resources.readLines(url, Charsets.UTF_8);

    for (String line : lines) {
      DataSet dataSet = DataSet.parseDataSet(line);
      if (dataSet != null) {
        String type = dataSet.getType();
        List<DataSource> dsrc = dataSet.getDataSources();
        this.typesMap.put(type, dsrc);
      }
    }
  }
}
