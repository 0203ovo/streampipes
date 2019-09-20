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

package org.streampipes.processors.transformation.jvm.processor.booloperator.timer;

import org.streampipes.logging.api.Logger;
import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

public class BooleanTimer implements EventProcessor<BooleanTimerParameters> {

  private static Logger LOG;

  private String fieldName;
  private boolean measureTrue;

  private Long timestamp;


  @Override
  public void onInvocation(BooleanTimerParameters booleanInverterParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {
    LOG = booleanInverterParameters.getGraph().getLogger(BooleanTimer.class);
    this.fieldName = booleanInverterParameters.getFieldName();
    this.measureTrue = booleanInverterParameters.isMeasureTrue();
    this.timestamp = Long.MIN_VALUE;
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {

    boolean field = inputEvent.getFieldBySelector(this.fieldName).getAsPrimitive().getAsBoolean();

    if (this.measureTrue == field) {
      if (timestamp == Long.MIN_VALUE) {
        timestamp = System.currentTimeMillis();
      }
    } else {
      if (timestamp != Long.MIN_VALUE) {
        Long difference = System.currentTimeMillis() - timestamp;

        inputEvent.addField("measured_time", difference);
        timestamp = Long.MIN_VALUE;
        out.collect(inputEvent);
      }
    }
    
  }

  @Override
  public void onDetach() {
  }
}
