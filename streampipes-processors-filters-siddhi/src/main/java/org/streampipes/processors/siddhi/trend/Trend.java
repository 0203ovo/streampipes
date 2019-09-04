/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.processors.siddhi.trend;

import org.streampipes.wrapper.siddhi.engine.SiddhiDebugCallback;
import org.streampipes.wrapper.siddhi.engine.SiddhiEventEngine;

import java.util.List;

public class Trend extends SiddhiEventEngine<TrendParameters> {

  public Trend() {
    super();
  }

  public Trend(SiddhiDebugCallback callback) {
    super(callback);
  }

  @Override
  protected String fromStatement(List<String> inputStreamNames, TrendParameters params) {
      String mappingProperty = prepareName(params.getMapping());
      int duration = params.getDuration();
      String inequaloperator;
      String operator;

      double increase = Double.valueOf(params.getIncrease());
      increase = (increase / 100) + 1;

      if (params.getOperation() == TrendOperator.INCREASE) {
          inequaloperator = "<=";
          operator = "/";

      } else {
          inequaloperator = ">=";
          operator = "*";
      }

      String s = "from every(e1="
              + inputStreamNames.get(0)
              +") -> e2="
              +inputStreamNames.get(0)
              + "[e1." + mappingProperty
              + inequaloperator
              + "("
              + mappingProperty
              + operator
              + increase
              +")"
              + "]<1>"
              + " within " + duration + " sec";

    //String s = "from e1="+inputStreamNames.get(0) + "[e1.s0randomValue > 5]";

    return s;
  }

  @Override
  protected String selectStatement(TrendParameters params) {
      return getCustomOutputSelectStatement(params.getGraph());
  }

}
