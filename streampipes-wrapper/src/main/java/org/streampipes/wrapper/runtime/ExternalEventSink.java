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
package org.streampipes.wrapper.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public interface ExternalEventSink<B extends EventSinkBindingParams> extends PipelineElement<B,
        DataSinkInvocation> {

  void onInvocation(B parameters, EventSinkRuntimeContext runtimeContext) throws
          SpRuntimeException;
}
