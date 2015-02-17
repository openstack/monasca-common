/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package monasca.common.streaming.storm;

import java.util.List;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;

import monasca.common.streaming.storm.TupleDeserializer;

public class TestSpout extends BaseRichSpout {
  private static final long serialVersionUID = 849564133745588803L;

  private final TupleDeserializer deserializer;
  private final TupleProvider tupleProvider;
  private SpoutOutputCollector collector;

  public TestSpout(TupleDeserializer deserializer, TupleProvider tupleProvider) {
    this.deserializer = deserializer;
    this.tupleProvider = tupleProvider;
  }

  public interface TupleProvider {
    List<Object> get();
  }

  @Override
  @SuppressWarnings("rawtypes")
  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    this.collector = collector;
  }

  @Override
  public void nextTuple() {
    collector.emit(tupleProvider.get());
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(deserializer.getOutputFields());
  }
}
