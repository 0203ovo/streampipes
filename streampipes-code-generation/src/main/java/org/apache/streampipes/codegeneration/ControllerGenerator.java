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

package org.apache.streampipes.codegeneration;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import org.apache.streampipes.codegeneration.utils.JFC;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.ConsumableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.output.AppendOutputStrategy;
import org.apache.streampipes.model.output.CustomOutputStrategy;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.schema.EventProperty;

import java.util.List;

import javax.lang.model.element.Modifier;

public abstract class ControllerGenerator extends Generator {
	public ControllerGenerator(ConsumableStreamPipesEntity element, String name, String packageName) {
		super(element, name, packageName);
	}

	public Builder getEventStream(Builder b, SpDataStream spDataStream, int n) {
		b = getEventProperties(b, spDataStream.getEventSchema().getEventProperties(), n);
		b.addStatement(
				"$T stream$L = $T.createStream($S, $S, $S).schema($T.create().properties(eventProperties$L).build()).build()",
				JFC.EVENT_STREAM, n, JFC.STREAM_BUILDER, spDataStream.getName(), spDataStream.getDescription(),
				spDataStream.getUri(), JFC.SCHEMA_BUILDER, n);

		return b;
	}

	public Builder getEventProperties(Builder b, List<EventProperty> eventProperties, int n) {
		b.addStatement("$T<$T> eventProperties$L = new $T<$T>()", JFC.LIST, JFC.EVENT_PROPERTY, n, JFC.ARRAY_LIST,
				JFC.EVENT_PROPERTY);

		for (int i = 0; i < eventProperties.size(); i++) {
			// TODO check for type
			if (eventProperties.get(i).getDomainProperties() != null && eventProperties.get(i).getDomainProperties().size() > 0) {
//				b.addStatement("$T e$L = $T.createPropertyRestriction($S).build()", JFC.EVENT_PROPERTY, i,
//						JFC.PRIMITIVE_PROPERTY_BUILDER, eventProperties.get(i).getDomainProperties().get(0));
				b.addStatement("eventProperties$L.add(e$L)", n, i);
			}
		}

		return b;
	}

	public Builder getCustomOutputStrategy(Builder b, CustomOutputStrategy cos, int n) {
		b.addStatement("$T outputStrategy$L = new $T()", JFC.APPEND_OUTPUT_STRATEGY, n, JFC.APPEND_OUTPUT_STRATEGY);

		return b;
	}

	public Builder getAppendOutputStrategy(Builder b, AppendOutputStrategy aos, int n) {
		b.addStatement("$T outputStrategy$L = new $T()", JFC.APPEND_OUTPUT_STRATEGY, n, JFC.APPEND_OUTPUT_STRATEGY);
		b.addStatement("$T<$T> appendProperties = new $T<$T>()", JFC.LIST, JFC.EVENT_PROPERTY, JFC.ARRAY_LIST,
				JFC.EVENT_PROPERTY);

		for (EventProperty ep : aos.getEventProperties()) {
			// TODO
			b.addStatement("appendProperties.add($T.stringEp($S, $S))", JFC.EP_PROPERTIES, ep.getRuntimeName(),
					ep.getDomainProperties().get(0).toString());
		}

		b.addStatement("outputStrategy$L.setEventProperties(appendProperties)", n);

		return b;
	}

	public Builder getOutputStrategies(Builder b, List<OutputStrategy> outputStrategies) {
		b.addStatement("$T<$T> strategies = new $T<$T>()", JFC.LIST, JFC.OUTPUT_STRATEGY, JFC.ARRAY_LIST,
				JFC.OUTPUT_STRATEGY);
		for (int i = 0; i < outputStrategies.size(); i++) {
			OutputStrategy outputStrategy = outputStrategies.get(i);
			if (outputStrategy instanceof AppendOutputStrategy) {
				b = getAppendOutputStrategy(b, (AppendOutputStrategy) outputStrategy, i);
			} else if (outputStrategy instanceof CustomOutputStrategy) {
				b = getCustomOutputStrategy(b, (CustomOutputStrategy) outputStrategy, i);
			} else {
				// TODO add implementation for the other strategies
				try {
					throw new Exception("Not yet Implemented");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			b.addStatement("strategies.add(outputStrategy$L)", i);
		}

		return b;
	}

	public Builder getSupportedGrounding(Builder b, EventGrounding grounding) {
		return b.addStatement("desc.setSupportedGrounding($T.getSupportedGrounding())", JFC.STANDARD_TRANSPORT_FORMAT);
	}

	public Builder getDeclareModelCode(ClassName desc) {
		Builder b = MethodSpec.methodBuilder("declareModel").addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
				.returns(desc);

		b.addStatement("$T desc = new $T($S, $S, $S)", desc, desc, element.getUri(),
				element.getName(), element.getDescription());

		for (int i = 0; i < element.getSpDataStreams().size(); i++) {
			// TODO
			b = getEventStream(b, element.getSpDataStreams().get(i), i);
			b.addStatement("desc.addEventStream(stream$L)", i);
		}

		//TODO find better solution
		if (element instanceof DataProcessorDescription) {
			b = getOutputStrategies(b, ((DataProcessorDescription) element).getOutputStrategies());
			b.addStatement("desc.setOutputStrategies(strategies)");
		}

		b = getSupportedGrounding(b, element.getSupportedGrounding());

		b.addStatement("return desc");

		return b;
	}
}