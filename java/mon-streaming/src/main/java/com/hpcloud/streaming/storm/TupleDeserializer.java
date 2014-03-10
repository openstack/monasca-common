package com.hpcloud.streaming.storm;

import java.util.List;
import java.util.Map;

import backtype.storm.tuple.Fields;

/**
 * Deserializes tuples. Similar to a Scheme, but allows for multiple records per
 * {@link #deserialize(byte[])} call.
 * 
 * @author Jonathan Halterman
 */
public interface TupleDeserializer {
  /**
   * Returns a list of deserialized tuples, consisting of a list of tuples each with a list of
   * fields, for the {@code tuple}, else null if the {@code tuple} cannot be deserialized.
   */
  List<List<?>> deserialize(byte[] tuple, Map<String, Object> headers);

  /**
   * Returns the output fields.
   */
  Fields getOutputFields();
}
