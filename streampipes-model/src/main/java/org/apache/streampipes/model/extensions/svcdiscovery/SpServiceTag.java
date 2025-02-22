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
package org.apache.streampipes.model.extensions.svcdiscovery;

public class SpServiceTag {

  private static final String COLON = ":";
  private SpServiceTagPrefix prefix;
  private String value;

  public SpServiceTag() {

  }

  private SpServiceTag(SpServiceTagPrefix prefix, String value) {
    this.prefix = prefix;
    this.value = value;
  }

  public static SpServiceTag create(SpServiceTagPrefix prefix,
                                    String value) {
    return new SpServiceTag(prefix, value);
  }

  public String asString() {
    return prefix.asString() + COLON + value;
  }

  public SpServiceTagPrefix getPrefix() {
    return prefix;
  }

  public void setPrefix(SpServiceTagPrefix prefix) {
    this.prefix = prefix;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
