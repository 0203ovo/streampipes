/*
 * Copyright 2018 FZI Forschungszentrum Informatik
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

package org.streampipes.manager.setup;

import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfigException;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.storage.management.StorageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SesameDbInstallationStep implements InstallationStep {

	public SesameDbInstallationStep() {

	}

	@Override
	public List<Message> install() {
		List<Message> msgs = new ArrayList<>();
		//RemoteRepositoryManager manager = new RemoteRepositoryManager(sesameUrl);
		try {
			//manager.initialize();
			msgs.add(Notifications.success("Connecting to Sesame Server..."));
			//if (!manager.hasRepositoryConfig(sesameDbName))
			//{
				msgs.add(Notifications.success("Retrieving Sesame databases..."));
				//RepositoryConfig config = new RepositoryConfig(sesameDbName, "StreamPipes DB");
				//SailImplConfig backendConfig = new MemoryStoreConfig(true, 10000);
				//backendConfig = new ForwardChainingRDFSInferencerConfig(backendConfig);
				//config.setRepositoryImplConfig(new SailRepositoryConfig(backendConfig));
				//manager.addRepositoryConfig(config);
				msgs.add(Notifications.success(getTitle()));
				boolean success = StorageManager.INSTANCE.getBackgroundKnowledgeStorage().initialize();
				if (success) msgs.add(Notifications.success("Adding schema..."));
				else msgs.add(Notifications.error("Adding schema..."));
			//}
		} catch (RepositoryException e) {
			e.printStackTrace();
			return Arrays.asList(Notifications.error("Connecting to Sesame Server..."));
		} catch (RepositoryConfigException e) {
			return Arrays.asList(Notifications.error("Retrieving Sesame databases..."));
		} 
		return msgs;
	}

	@Override
	public String getTitle() {
		return "Creating Sesame database...";
	}
}
