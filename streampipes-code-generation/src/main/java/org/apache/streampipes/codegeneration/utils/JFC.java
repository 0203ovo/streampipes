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

package org.apache.streampipes.codegeneration.utils;


import com.squareup.javapoet.ClassName;
import org.apache.streampipes.container.init.DeclarersSingleton;
import org.apache.streampipes.container.util.StandardTransportFormat;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.output.AppendOutputStrategy;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.stream.SchemaBuilder;
import org.apache.streampipes.sdk.stream.StreamBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Java File Classes (JFC)
 * @author philipp
 *
 */
public abstract class JFC {

	private static final String STREAMPIPES_MODEL_PACKAGE = "org.apache.streampipes.model";
	private static final String STREAMPIPES_MODEL_PACKAGE_GRAPH = STREAMPIPES_MODEL_PACKAGE + ".graph";

	private static final String SP_FLINK_PACKAGE = "org.apache.streampipes.wrapper.flink";
	private static final String SP_WRAPPER_PACKAGE = "org.apache.streampipes.wrapper";

	public static ClassName MAP = ClassName.get(Map.class);
	public static ClassName LIST = ClassName.get(List.class);
	public static ClassName ARRAY_LIST = ClassName.get(ArrayList.class);

	public static ClassName STRING = ClassName.get("", "String");
	public static ClassName OVERRIDE = ClassName.get("", "Override");
	public static ClassName OBJECT = ClassName.get("", "Object");
	public static ClassName EXCEPTION = ClassName.get("", "Exception");

	public static ClassName DATA_STREAM = ClassName.get("org.apache.flink.streaming.api.datastream", "DataStream");
	public static ClassName DATA_STREAM_SINK = ClassName.get("org.apache.flink.streaming.api.datastream", "DataStreamSink");
	public static ClassName FLAT_MAP_FUNCTION = ClassName.get("org.apache.flink.api.common.functions", "FlatMapFunction");
	public static ClassName COLLECTOR = ClassName.get("org.apache.flink.util", "Collector");

	public static ClassName SEPA_DESCRIPTION = ClassName.get(STREAMPIPES_MODEL_PACKAGE_GRAPH, "DataProcessorInvocation");
	public static ClassName SEC_DESCRIPTION = ClassName.get(DataSinkDescription.class);
	public static ClassName SEPA_INVOCATION = ClassName.get(DataProcessorInvocation.class);
	public static ClassName SEC_INVOCATION = ClassName.get(DataSinkInvocation.class);
	public static ClassName EVENT_STREAM = ClassName.get(SpDataStream.class);
	public static ClassName STREAM_BUILDER = ClassName.get(StreamBuilder.class);
	public static ClassName SCHEMA_BUILDER = ClassName.get(SchemaBuilder.class);
	public static ClassName EVENT_PROPERTY = ClassName.get(EventProperty.class);
	//public static ClassName PRIMITIVE_PROPERTY_BUILDER = ClassName.get(PrimitivePropertyBuilder
	//				.class);
	public static ClassName APPEND_OUTPUT_STRATEGY = ClassName.get(AppendOutputStrategy.class);
	public static ClassName OUTPUT_STRATEGY = ClassName.get(OutputStrategy.class);
	public static ClassName EP_PROPERTIES = ClassName.get(EpProperties.class);
	public static ClassName STANDARD_TRANSPORT_FORMAT = ClassName.get(StandardTransportFormat.class);
//	public static ClassName EMBEDDED_MODEL_SUBMITTER = ClassName.get("EmbeddedModelSubmitter.class);
//	public static ClassName SEMANTIC_EVENT_PROCESSING_AGENT_DECLARER = ClassName.get(SemanticEventProcessingAgentDeclarer.class);
//	public static ClassName SEMANTIC_EVENT_PRODUCER_DECLARER = ClassName.get(SemanticEventProducerDeclarer.class);
//	public static ClassName SEMANTIC_EVENT_CONSUMER_DECLARER = ClassName.get(SemanticEventConsumerDeclarer.class);

	public static ClassName CONTAINER_MODEL_SUBMITTER = ClassName.get("de.fzi.cep.sepa.client.container.init", "ContainerModelSubmitter");
	public static ClassName DECLARERS_SINGLETON = ClassName.get(DeclarersSingleton.class);



	public static ClassName FLINK_DEPLOYMENT_CONFIG = ClassName.get(SP_FLINK_PACKAGE, "FlinkDeploymentConfig");
	public static ClassName FLINK_SEPA_RUNTIME =  ClassName.get(SP_FLINK_PACKAGE, "FlinkDataProcessorRuntime");
	public static ClassName FLINK_SEC_RUNTIME = ClassName.get(SP_FLINK_PACKAGE, "FlinkDataSinkRuntime");
	public static ClassName ABSTRACT_FLINK_AGENT_DECLARER =  ClassName.get(SP_FLINK_PACKAGE, "FlinkDataProcessorDeclarer");
	public static ClassName ABSTRACT_FLINK_CONSUMER_DECLARER =  ClassName.get(SP_FLINK_PACKAGE, "DataSinkDeclarer");

	public static ClassName EVENT_PROCESSOR_BINDING_PARAMS = ClassName.get(SP_WRAPPER_PACKAGE +".params.binding", "EventProcessorBindingParams");
}
