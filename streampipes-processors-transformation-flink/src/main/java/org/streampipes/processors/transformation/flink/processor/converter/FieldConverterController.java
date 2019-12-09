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
package org.streampipes.processors.transformation.flink.processor.converter;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Locales;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.TransformOperations;
import org.streampipes.sdk.helpers.Tuple2;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.vocabulary.XSD;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class FieldConverterController extends
        FlinkDataProcessorDeclarer<FieldConverterParameters> {

  public static final String CONVERT_PROPERTY = "convert-property";
  public static final String TARGET_TYPE = "target-type";
  private static final String FIELD_TO_CONVERT_KEY = "fieldToConvert";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.transformation.flink.field-converter")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
//                    .requiredPropertyWithUnaryMapping(EpRequirements.stringReq(), Labels.withId
//                            (CONVERT_PROPERTY), PropertyScope.NONE)
                    .build())

            .naryMappingPropertyWithoutRequirement(
                    Labels.withId(FIELD_TO_CONVERT_KEY),
                    PropertyScope.NONE)

            .requiredSingleValueSelection(Labels.withId(TARGET_TYPE), Options.from
                    (new Tuple2<>("Float", XSD._float.toString()), new Tuple2<>
                            ("Integer", XSD._integer.toString())))
            .outputStrategy(OutputStrategies.transform(TransformOperations
                    .dynamicDatatypeTransformation(CONVERT_PROPERTY, TARGET_TYPE)))
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<FieldConverterParameters> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
    String convertProperty = extractor.mappingPropertyValue(CONVERT_PROPERTY);
    String targetDatatype =  extractor.selectedSingleValueInternalName(TARGET_TYPE, String.class);

    FieldConverterParameters staticParams = new FieldConverterParameters(
            graph,
            convertProperty,
            targetDatatype
    );

    return new FieldConverterProgram(staticParams);
  }
}
