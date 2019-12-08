/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.wrapper.standalone.declarer;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.wrapper.declarer.EventProcessorDeclarer;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.params.runtime.EventProcessorRuntimeParams;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.runtime.StandaloneEventProcessorRuntime;

public abstract class StandaloneEventProcessorDeclarerSingleton<B extends EventProcessorBindingParams> extends EventProcessorDeclarer<B, StandaloneEventProcessorRuntime> {

  @Override
  public StandaloneEventProcessorRuntime getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    ConfiguredEventProcessor<B> configuredEngine = onInvocation(graph, extractor);
    EventProcessorRuntimeParams<B> runtimeParams = new EventProcessorRuntimeParams<>
            (configuredEngine.getBindingParams(), true);

    return new StandaloneEventProcessorRuntime<>(configuredEngine.getEngineSupplier(),
            runtimeParams);
  }

  public abstract ConfiguredEventProcessor<B> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor);
}
