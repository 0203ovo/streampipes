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

package org.apache.streampipes.model.connect.rules.value;

import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@Namespaces({StreamPipes.NS_PREFIX, StreamPipes.NS})
@RdfsClass(StreamPipes.TIMESTAMP_TRANSFORMATION_RULE_DESCRIPTION)
@Entity
public class TimestampTranfsformationRuleDescription extends ValueTransformationRuleDescription {

    @RdfProperty(StreamPipes.RUNTIME_KEY)
    private String runtimeKey;

    @RdfProperty(StreamPipes.HAS_MODE)
    private String mode;

    @RdfProperty(StreamPipes.FORMAT_STRING)
    private String formatString;

    @RdfProperty(StreamPipes.MULTIPLIER)
    private long multiplier;

    public TimestampTranfsformationRuleDescription() {
        super();
    }

    public TimestampTranfsformationRuleDescription(String runtimeKey, String mode,
                                                   String formatString, Long multiplier) {
        this.runtimeKey = runtimeKey;
        this.mode = mode;
        this.formatString = formatString;
        this.multiplier = multiplier;
    }

    public TimestampTranfsformationRuleDescription(TimestampTranfsformationRuleDescription other) {
        super(other);
        this.runtimeKey = other.getRuntimeKey();
        this.mode = other.getMode();
        this.formatString = other.getFormatString();
        this.multiplier = other.getMultiplier();
    }

    public String getRuntimeKey() {
        return runtimeKey;
    }

    public void setRuntimeKey(String runtimeKey) {
        this.runtimeKey = runtimeKey;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getFormatString() {
        return formatString;
    }

    public void setFormatString(String formatString) {
        this.formatString = formatString;
    }

    public long getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(long multiplier) {
        this.multiplier = multiplier;
    }
}
