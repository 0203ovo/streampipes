/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

package org.streampipes.connect.adapters.opcua;

import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.adapter.exception.ParseException;
import org.streampipes.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.streampipes.model.AdapterType;
import org.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.streampipes.sdk.helpers.Labels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpcUaAdapter extends SpecificDataStreamAdapter {

    public static final String ID = "http://streampipes.org/adapter/specific/opcua";

    private static final String OPC_SERVER_HOST = "OPC_SERVER_HOST";
    private static final String OPC_SERVER_PORT = "OPC_SERVER_PORT";
    private static final String NAMESPACE_INDEX = "NAMESPACE_INDEX";
    private static final String NODE_ID = "NODE_ID";

    private String opcUaServer;
    private String namespaceIndex;
    private String nodeId;
    private String port;

    private Map<String, Object> event;

    private OpcUa opcUa;

    private int numberProperties;


    public OpcUaAdapter() {
        this.event = new HashMap<>();
        this.numberProperties = 0;
    }

    public OpcUaAdapter(SpecificAdapterStreamDescription adapterDescription) {
        super(adapterDescription);

        getConfigurations(adapterDescription);

        this.event = new HashMap<>();
        this.numberProperties = 0;
    }

    @Override
    public SpecificAdapterStreamDescription declareModel() {

        SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID, "OPC UA", "Read values form an opc ua server")
                .iconUrl("opc.jpg")
                .category(AdapterType.Generic, AdapterType.Manufacturing)
                .requiredTextParameter(Labels.from(OPC_SERVER_HOST, "OPC Server", "Example: test-server.com (No leading opc.tcp://) "))
                .requiredTextParameter(Labels.from(OPC_SERVER_PORT, "OPC Server Port", "Example: 4840"))
                .requiredTextParameter(Labels.from(NAMESPACE_INDEX, "Namespace Index", "Example: 2"))
                .requiredTextParameter(Labels.from(NODE_ID, "Node Id", "Id of the Node to read the values from"))
                .build();
        description.setAppId(ID);


        return  description;
    }

    public void onSubscriptionValue(UaMonitoredItem item, DataValue value) {

        String[] keys = item.getReadValueId().getNodeId().getIdentifier().toString().split("\\.");
        String key;

        if (keys.length > 0) {
            key = keys[keys.length - 1];
        } else {
            key = item.getReadValueId().getNodeId().getIdentifier().toString();
        }

        event.put(key, value.getValue().getValue());

        if (event.keySet().size() >= this.numberProperties) {
            adapterPipeline.process(event);
//            System.out.println(event);
        }
    }


    @Override
    public void startAdapter() throws AdapterException {
        this.opcUa = new OpcUa(opcUaServer, Integer.parseInt(port), Integer.parseInt(namespaceIndex), nodeId);
        try {
            this.opcUa.connect();

            List<OpcNode> allNodes = this.opcUa.browseNode();
            List<NodeId> nodeIds = new ArrayList<>();

            for (OpcNode rd : allNodes) {
                nodeIds.add(rd.nodeId);
            }

            this.numberProperties = nodeIds.size();
            this.opcUa.createListSubscription(nodeIds, this);
        } catch (Exception e) {
            throw new AdapterException("Could not connect to OPC-UA server! Server: " + opcUaServer + " Port: " + port +
                    " NamespaceIndex: " + namespaceIndex + " NodeId: " + nodeId);
        }
    }

    @Override
    public void stopAdapter() throws AdapterException {
        // close connection
        this.opcUa.disconnect();
    }

    @Override
    public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
        return new OpcUaAdapter(adapterDescription);
    }

    @Override
    public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException, ParseException {

        GuessSchema guessSchema = new GuessSchema();
        EventSchema eventSchema = new EventSchema();
        List<EventProperty> allProperties = new ArrayList<>();


        getConfigurations(adapterDescription);

        OpcUa opc = new OpcUa(opcUaServer, Integer.parseInt(port), Integer.parseInt(namespaceIndex), nodeId);
        try {
            opc.connect();
            List<OpcNode> res =  opc.browseNode();


            if (res.size() > 0) {
                for (OpcNode opcNode : res) {
                    allProperties.add(PrimitivePropertyBuilder
                            .create(opcNode.getType(), opcNode.getRuntimeName())
                            .build());
                }
            }

            opc.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        eventSchema.setEventProperties(allProperties);
        guessSchema.setEventSchema(eventSchema);

        return guessSchema;
    }

    @Override
    public String getId() {
        return ID;
    }

    private void getConfigurations(SpecificAdapterStreamDescription adapterDescription) {
        ParameterExtractor extractor = new ParameterExtractor(adapterDescription.getConfig());

        this.opcUaServer = extractor.singleValue(OPC_SERVER_HOST, String.class);
        this.port = extractor.singleValue(OPC_SERVER_PORT, String.class);
        this.namespaceIndex = extractor.singleValue(NAMESPACE_INDEX, String.class);
        this.nodeId = extractor.singleValue(NODE_ID, String.class);
    }
}
