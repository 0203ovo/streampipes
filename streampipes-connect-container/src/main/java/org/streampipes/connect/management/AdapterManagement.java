package org.streampipes.connect.management;

import org.streampipes.connect.RunningAdapterInstances;
import org.streampipes.connect.config.ConnectContainerConfig;
import org.streampipes.connect.firstconnector.Adapter;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.modelconnect.AdapterDescription;
import org.streampipes.model.modelconnect.AdapterSetDescription;
import org.streampipes.model.modelconnect.AdapterStreamDescription;

public class AdapterManagement implements IAdapterManagement {

    public String invokeStreamAdapter(AdapterStreamDescription adapterStreamDescription) {

        String brokerUrl = ConnectContainerConfig.INSTANCE.getKafkaUrl();
        String topic = getTopicPrefix() + adapterStreamDescription.getName();

        Adapter adapter = new Adapter(brokerUrl, topic, false);
        RunningAdapterInstances.INSTANCE.addAdapter(adapterStreamDescription.getUri(), adapter);
        adapter.run(adapterStreamDescription);
        return "";
    }

    public String stopStreamAdapter(AdapterStreamDescription adapterStreamDescription) {
        return stopAdapter(adapterStreamDescription);

    }

    public String invokeSetAdapter (AdapterSetDescription adapterSetDescription) {
        SpDataSet dataSet = adapterSetDescription.getDataSet();

        String brokerUrl = dataSet.getBrokerHostname() + ":9092";
        String topic = dataSet.getActualTopicName();
        Adapter adapter = new Adapter(brokerUrl, topic, false);

        RunningAdapterInstances.INSTANCE.addAdapter(dataSet.getDatasetInvocationId(), adapter);

        adapter.run(adapterSetDescription);

        return "";
    }

    public String stopSetAdapter (AdapterSetDescription adapterSetDescription) {
        return stopAdapter(adapterSetDescription);
    }

    private String stopAdapter(AdapterDescription adapterDescription) {

        String adapterUri = adapterDescription.getUri();

        Adapter adapter = RunningAdapterInstances.INSTANCE.removeAdapter(adapterUri);

        if (adapter == null) {
            return "Adapter with id " + adapterUri + " was not found in this container and cannot be stopped.";
        }

        adapter.stop();

        return "";
    }

    private String getTopicPrefix() {
        return "org.streampipes.autogenerated.adapters.";
    }

}
