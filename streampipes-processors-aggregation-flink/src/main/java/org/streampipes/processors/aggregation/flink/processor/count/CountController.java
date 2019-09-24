/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
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

package org.streampipes.processors.aggregation.flink.processor.count;

import org.apache.flink.streaming.api.windowing.time.Time;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.aggregation.flink.config.AggregationFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Locales;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.Tuple2;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class CountController extends FlinkDataProcessorDeclarer<CountParameters> {

  private static final String TIME_WINDOW_KEY = "time-window";
  private static final String SCALE_KEY = "scale";
  private static final String COUNT_MAPPING = "count-mapping";

  static final String HOURS_INTERNAL_NAME = "HOURS";
  static final String MINUTES_INTERNAL_NAME = "MINUTES";
  static final String SECONDS_INTERNAL_NAME = "SECONDS";

  @Override
  public DataProcessorDescription declareModel() {

    return ProcessingElementBuilder.create("org.streampipes.processors.aggregation.flink.count")
            .category(DataProcessorType.AGGREGATE)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .withLocales(Locales.EN)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(EpRequirements.anyProperty(),
                            Labels.withId(COUNT_MAPPING), PropertyScope.DIMENSION_PROPERTY)
                    .build())
            .outputStrategy(OutputStrategies.fixed(EpProperties.stringEp(Labels.empty(), "value", "http://schema.org/Text"), EpProperties.integerEp(Labels.empty(), "countValue",
                    "http://schema.org/Number")))
            .requiredIntegerParameter(Labels.withId(TIME_WINDOW_KEY))
            .requiredSingleValueSelection(Labels.withId(SCALE_KEY),
                    Options.from(new Tuple2<>("Hours", HOURS_INTERNAL_NAME),
                            new Tuple2<>("Minutes", MINUTES_INTERNAL_NAME),
                            new Tuple2<>("Seconds", SECONDS_INTERNAL_NAME)))
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<CountParameters> getRuntime(DataProcessorInvocation graph,
                                                               ProcessingElementParameterExtractor extractor) {

    Integer timeWindowSize = extractor.singleValueParameter(TIME_WINDOW_KEY, Integer.class);
    String scale = extractor.selectedSingleValueInternalName(SCALE_KEY, String.class);
    String fieldToCount = extractor.mappingPropertyValue(COUNT_MAPPING);

    Time time = new TimeScale(scale).toFlinkTime(timeWindowSize);


    CountParameters staticParam = new CountParameters(graph, time,
            fieldToCount);

    return new CountProgram(staticParam, AggregationFlinkConfig.INSTANCE.getDebug());

  }
}
