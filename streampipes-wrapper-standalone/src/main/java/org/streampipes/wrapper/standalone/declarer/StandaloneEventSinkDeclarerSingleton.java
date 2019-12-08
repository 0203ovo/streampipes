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

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.wrapper.declarer.EventSinkDeclarer;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.params.runtime.EventSinkRuntimeParams;
import org.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.streampipes.wrapper.standalone.runtime.StandaloneEventSinkRuntime;

public abstract class StandaloneEventSinkDeclarerSingleton<B extends
        EventSinkBindingParams> extends EventSinkDeclarer<B, StandaloneEventSinkRuntime> {

  @Override
  public StandaloneEventSinkRuntime<B> getRuntime(DataSinkInvocation graph,
  DataSinkParameterExtractor extractor) {

    ConfiguredEventSink<B> configuredEngine = onInvocation(graph, extractor);
    EventSinkRuntimeParams<B> runtimeParams = new EventSinkRuntimeParams<>
            (configuredEngine.getBindingParams(), true);

    return new StandaloneEventSinkRuntime<>(configuredEngine.getEngineSupplier(), runtimeParams);
  }

  public abstract ConfiguredEventSink<B> onInvocation(DataSinkInvocation graph, DataSinkParameterExtractor extractor);
}
