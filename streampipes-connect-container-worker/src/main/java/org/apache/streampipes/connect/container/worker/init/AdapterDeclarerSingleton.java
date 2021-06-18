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

package org.apache.streampipes.connect.container.worker.init;

import org.apache.streampipes.connect.adapter.Adapter;
import org.apache.streampipes.connect.adapter.model.Connector;
import org.apache.streampipes.connect.adapter.model.generic.GenericDataSetAdapter;
import org.apache.streampipes.connect.adapter.model.generic.GenericDataStreamAdapter;
import org.apache.streampipes.connect.adapter.model.generic.Protocol;
import org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AdapterDeclarerSingleton {

    private Map<String, Protocol> allProtocols;
    private Map<String, Adapter> allAdapters;

    private static AdapterDeclarerSingleton instance;

    public AdapterDeclarerSingleton() {
        this.allProtocols = new HashMap<>();
        this.allAdapters = new HashMap<>();
        this.allAdapters.put(GenericAdapterStreamDescription.ID, new GenericDataStreamAdapter());
        this.allAdapters.put(GenericAdapterSetDescription.ID, new GenericDataSetAdapter());
    }

    public static AdapterDeclarerSingleton getInstance() {
        if (AdapterDeclarerSingleton.instance == null) {
            AdapterDeclarerSingleton.instance = new AdapterDeclarerSingleton();
        }

        return AdapterDeclarerSingleton.instance;
    }

    public AdapterDeclarerSingleton add(Connector c) {

        if (c instanceof Protocol) {
            this.allProtocols.put(((Protocol) c).getId(), (Protocol) c);
        } else if (c instanceof Adapter) {
            this.allAdapters.put(((Adapter) c).getId(), (Adapter) c);
        }

        return getInstance();
    }

    public Map<String, Protocol> getAllProtocolsMap() {
        return this.allProtocols;
    }

    public Map<String, Adapter> getAllAdaptersMap() {
        return this.allAdapters;
    }


    public Collection<Protocol> getAllProtocols() {
        return this.allProtocols.values();
    }

    public Collection<Adapter> getAllAdapters() {
        return this.allAdapters.values();
    }

    public Protocol getProtocol(String id) {
        return getAllProtocols().stream()
                .filter(protocol -> protocol.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    public Adapter getAdapter(String id) {
        return getAllAdapters().stream()
                .filter(adapter -> adapter.getId().equals(id))
                .findAny()
                .orElse(null);
    }

}
