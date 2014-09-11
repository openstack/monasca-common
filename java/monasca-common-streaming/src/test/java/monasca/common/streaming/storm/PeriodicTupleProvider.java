package monasca.common.streaming.storm;

import java.util.List;

import monasca.common.streaming.storm.TestSpout.TupleProvider;

public class PeriodicTupleProvider implements TupleProvider {
  @Override
  public List<Object> get() {
    return null;
  }
}
