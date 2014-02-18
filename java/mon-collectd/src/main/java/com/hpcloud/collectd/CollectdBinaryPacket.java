package com.hpcloud.collectd;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.collectd.api.DataSource;
import org.collectd.api.DataSet;
import org.collectd.api.ValueList;

/**
 * CollectdBinaryPacket
 * 
 * @author Cindy O'Neill
 */
public class CollectdBinaryPacket {
  public static final int TYPE_HOST            = 0x0000;
  public static final int TYPE_TIME            = 0x0001;
  public static final int TYPE_PLUGIN          = 0x0002;
  public static final int TYPE_PLUGIN_INSTANCE = 0x0003;
  public static final int TYPE_TYPE            = 0x0004;
  public static final int TYPE_TYPE_INSTANCE   = 0x0005;
  public static final int TYPE_VALUES          = 0x0006;
  public static final int TYPE_INTERVAL        = 0x0007;
  public static final int TYPE_HIGH_RES_TIME   = 0x0008;
  public static final int TYPE_HIGH_RES_INTERVAL = 0x0009;
  public static final int TYPE_MESSAGE         = 0x0100;
  public static final int TYPE_SEVERITY        = 0x0101;

  public static final int UINT8_LEN  = 1;
  public static final int UINT16_LEN = UINT8_LEN * 2;
  public static final int UINT32_LEN = UINT16_LEN * 2;
  public static final int UINT64_LEN = UINT32_LEN * 2;
  public static final int HEADER_LEN = UINT16_LEN * 2;
  public static final int BUFFER_SIZE = 1500; 
  public static final long HIGH_RES_TIME_2_30TH = 1073741824;
  public static CollectdTypesDB cbTypes = CollectdTypesDB.getInstance();

  /**
   * parse - input a DatagramPacket received from a collectd agent (via UDP or Multicast).
   * 
   * returns a List of ValueList objects.  Each ValueList represents one collectd metric.
   * Throws an exception when it has a parsing error.
   */  
  public List<ValueList> parse(DatagramPacket packet) throws Exception {
    byte[] pdata = packet.getData();
    int total = pdata.length;
    ByteArrayInputStream buffer = new ByteArrayInputStream(pdata);
    DataInputStream is = new DataInputStream(buffer);
    List<ValueList> metrics = new ArrayList<ValueList>();
    ValueList vl = new ValueList(); 
    
    while ((0 < total) && (total > CollectdBinaryPacket.HEADER_LEN)) {
      
      int type = is.readUnsignedShort();
      int len = is.readUnsignedShort();
      if (len < CollectdBinaryPacket.HEADER_LEN) {
        break;  
      }

      total -= len;
      len -= CollectdBinaryPacket.HEADER_LEN;
      if (len > total) {
        throw new Exception("Can't parse. Invalid header len = " + len + " is greater that packet data total = " + total);
      }

      if (type == CollectdBinaryPacket.TYPE_VALUES) {
        // read values into ValueList
        readValues(is, vl);
        ValueList valueList = new ValueList(vl);
        // add to List of ValueList
        metrics.add(valueList);
        // clear re-usable valueList
        vl.clearValues();

      } else if (type == CollectdBinaryPacket.TYPE_TIME) {
        long ltime = is.readLong();
        // convert time to collectd format
        vl.setTime(ltime * CollectdBinaryPacket.HIGH_RES_TIME_2_30TH);
        // when outputting, shift right by 2^30 to get seconds since UTC (Jan 1,1970)
      } else if (type == CollectdBinaryPacket.TYPE_HIGH_RES_TIME) {
        vl.setTime(is.readLong());
      } else if (type == CollectdBinaryPacket.TYPE_HIGH_RES_INTERVAL) {
        vl.setInterval(is.readLong());
        // when outputting, shift right by 2^30 to get seconds since UTC (Jan 1,1970)
      } else if (type == CollectdBinaryPacket.TYPE_INTERVAL) {
        long linterval = is.readLong();
        // convert interval to collectd format
        vl.setInterval(linterval * CollectdBinaryPacket.HIGH_RES_TIME_2_30TH);
      } else if (type == CollectdBinaryPacket.TYPE_HOST) {
        String host = readString(is, len);
        vl.setHost(host);
      } else if (type == CollectdBinaryPacket.TYPE_PLUGIN) {
        String plugin = readString(is, len);
        vl.setPlugin(plugin);
      } else if (type == CollectdBinaryPacket.TYPE_PLUGIN_INSTANCE) {
        String pluginInstance = readString(is, len);
        vl.setPluginInstance(pluginInstance);
      } else if (type == CollectdBinaryPacket.TYPE_TYPE) {
        String mtype = readString(is, len);
        vl.setType(mtype);
        // init DataSources
        List<DataSource> ds = cbTypes.getType(mtype);
        if (ds == null) {
          System.out.println("Failed to parse: Metric type was not found in types.db file(s):" + mtype);
        }
        vl.setDataSet(new DataSet(mtype, ds));
      } else if (type == CollectdBinaryPacket.TYPE_TYPE_INSTANCE) {
        String typeInstance = readString(is, len);
        vl.setTypeInstance(typeInstance);
      } else {
        System.out.println("Unsupported collectd type received: " + type);
        for (int i = 0; i < len; i++) {
          // just moving the buffer pointer
          is.readByte();
        }
      }
    } // end while
    return metrics;
  }
  
  public static String dataSourceTypeToString(int dstype) {
    switch (dstype) {
      case DataSource.TYPE_COUNTER:
        return "counter";
      case DataSource.TYPE_ABSOLUTE:
        return "absolute";
      case DataSource.TYPE_DERIVE:
        return "derive";
      case DataSource.TYPE_GAUGE:
        return "gauge";
      default:
        return "unknown";
    }
  }

  private String readString(DataInputStream is, int len) throws IOException {
    byte[] buf = new byte[len];
    is.read(buf, 0, len);
    return new String(buf, 0, len - 1); // -1 -> skip \0
  }
 

  /**
   * readValues - reads value part(s) into ValueList
   */
  private void readValues(DataInputStream is, ValueList vl) throws IOException {
    int nvalues = is.readUnsignedShort();
    int[] types = new int[nvalues];
    for (int i = 0; i < nvalues; i++) {
      types[i] = is.readByte();
    }
    for (int i = 0; i < nvalues; i++) {
      Number val;
      if (types[i] == DataSource.TYPE_COUNTER) {
        val = new Double(is.readLong());
      } else if (types[i] == DataSource.TYPE_ABSOLUTE) {
        val = new Double(is.readLong());
      } else if (types[i] == DataSource.TYPE_DERIVE) {
        val = new Double(is.readLong());
      } else if (types[i] == DataSource.TYPE_GAUGE) {
        // documentation says collectd uses x86 host order for this type
        // dbuff holds the 64 bit value
        byte[] dbuff = new byte[8];
        is.read(dbuff);
        ByteBuffer bb = ByteBuffer.wrap(dbuff);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        val = new Double(bb.getDouble());
      } else {
        System.out.println("Unknown DataSource type found: " + types[i]);
        val = new Double(0);
      }
      vl.addValue(val);
    } // end for
  }

}
