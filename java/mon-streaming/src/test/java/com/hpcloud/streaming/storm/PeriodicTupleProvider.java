package com.hpcloud.streaming.storm;

import java.util.List;

import com.hpcloud.streaming.storm.TestSpout.TupleProvider;

public class PeriodicTupleProvider implements TupleProvider {
  @Override
  public List<Object> get() {
    return null;
  }
}
