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

package org.streampipes.codegeneration.flink.sec;

import java.io.Serializable;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.codegeneration.Generator;
import org.streampipes.codegeneration.utils.JFC;

public class FlinkSecProgramGenerator extends Generator {

	public FlinkSecProgramGenerator(ConsumableStreamPipesEntity element, String name, String packageName) {
		super(element, name, packageName);
	}

	@Override
	public JavaFile build() {
		ParameterizedTypeName mapStringObject = ParameterizedTypeName.get(JFC.MAP, JFC.STRING, JFC.OBJECT);
		ParameterizedTypeName dataStreamSink = ParameterizedTypeName.get(JFC.DATA_STREAM_SINK, mapStringObject);
		ParameterizedTypeName dataStream = ParameterizedTypeName.get(JFC.DATA_STREAM, mapStringObject);

		MethodSpec constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
				.addParameter(JFC.SEC_INVOCATION, "graph").addStatement("super(graph)").build();

		MethodSpec constructorConfig = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
				.addParameter(JFC.SEC_INVOCATION, "graph").addParameter(JFC.FLINK_DEPLOYMENT_CONFIG, "config")
				.addStatement("super(graph, config)").build();

		MethodSpec getApplicationLogic = MethodSpec.methodBuilder("getSink").addAnnotation(JFC.OVERRIDE)
				.addModifiers(Modifier.PUBLIC).returns(dataStreamSink).addParameter(dataStream, "convertedStream")
				.addCode("// TODO implement\nreturn null;\n").build();

		TypeSpec programClass = TypeSpec.classBuilder(name + "Program").addModifiers(Modifier.PUBLIC)
				.superclass(JFC.FLINK_SEC_RUNTIME).addSuperinterface(Serializable.class).addMethod(constructor)
				.addMethod(constructorConfig).addMethod(getApplicationLogic).build();

		return JavaFile.builder(packageName, programClass).build();
	}

}
