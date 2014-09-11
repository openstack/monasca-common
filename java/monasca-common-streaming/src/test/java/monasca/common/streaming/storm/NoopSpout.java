package monasca.common.streaming.storm;

import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;

@SuppressWarnings("serial")
public class NoopSpout extends BaseRichSpout {
  private final Fields outputFields;

  public NoopSpout(Fields outputFields) {
    this.outputFields = outputFields;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
  }

  @Override
  public void nextTuple() {
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(outputFields);
  }
}
