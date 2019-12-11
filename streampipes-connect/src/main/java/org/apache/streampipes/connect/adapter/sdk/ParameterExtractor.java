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

package org.apache.streampipes.connect.adapter.sdk;

import com.github.drapostolos.typeparser.TypeParser;
import org.apache.streampipes.model.staticproperty.*;

import java.util.List;
import java.util.stream.Collectors;

public class ParameterExtractor {
    private List<StaticProperty> list;
    private TypeParser typeParser;

    public ParameterExtractor(List<StaticProperty> list) {
        this.list = list;
        this.typeParser = TypeParser.newBuilder().build();
    }

    public String singleValue(String internalName) {
        return (((FreeTextStaticProperty) getStaticPropertyByName(internalName))
                .getValue());
    }

    public String secretValue(String internalName) {
        return (((SecretStaticProperty) getStaticPropertyByName(internalName))
                .getValue());
    }

    public <V> V singleValue(String internalName, Class<V> targetClass) {
        return typeParser.parse(singleValue(internalName), targetClass);
    }

    public String selectedSingleValueInternalName(String internalName) {
        return ((SelectionStaticProperty) getStaticPropertyByName(internalName))
                .getOptions()
                .stream()
                .filter(Option::isSelected)
                .findFirst()
                .get()
                .getInternalName();
    }


    public List<String> selectedMultiValues(String internalName) {
        return ((SelectionStaticProperty) getStaticPropertyByName(internalName))
                .getOptions()
                .stream()
                .filter(Option::isSelected)
                .map(Option::getName)
                .collect(Collectors.toList());
    }

    public String selectedSingleValueOption(String internalName) {
        return selectedMultiValues(internalName).get(0);
    }

    public StaticProperty getStaticPropertyByName(String name)
    {
        for(StaticProperty p : list)
        {
            if (p.getInternalName().equals(name)) return p;
        }
        return null;
    }
}
