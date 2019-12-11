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

package org.apache.streampipes.codegeneration.api;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.streampipes.commons.zip.ZipFileGenerator;
import org.apache.streampipes.model.base.ConsumableStreamPipesEntity;
import org.apache.streampipes.model.client.deployment.DeploymentConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public abstract class ImplementationCodeGenerator extends CodeGenerator {

	protected String tempFolder;

	public ImplementationCodeGenerator(DeploymentConfiguration config, ConsumableStreamPipesEntity element) {
		super(config, element);
		this.tempFolder = RandomStringUtils.randomAlphabetic(8) + config.getArtifactId();
	}

	public File createProject() {
		createFolder(getTempDir());
		create();
		File result = toZip();
		deleteFolder(getTempDir());
		return result;
	}

	private void createFolder(String folder) {
		File file = new File(folder);
		file.mkdir();
	}
	
	private void deleteFolder(String folder) {
		try {
			FileUtils.deleteDirectory(new File(folder));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//TODO change this
	public File getGeneratedFile() {
		return createProject();
	}

	protected abstract void create();

	protected abstract void createDirectoryStructure();

	public abstract String getDeclareModel();

	protected String getTempDir() {
		return System.getProperty("user.home") + File.separator +".streampipes" +File.separator + tempFolder + File.separator;
	}

	protected File toZip() {
		String generatedProjects = System.getProperty("user.home") + File.separator +".streampipes" +File.separator + "generated_projects"
				+ File.separator;
		createFolder(generatedProjects);

		String zipFolder = generatedProjects + new Date().getTime() + "_";
		File outputFile = new File(zipFolder + config.getArtifactId() + ".zip");
		new ZipFileGenerator(new File(getTempDir()), outputFile).makeZipToFile();
		return outputFile;
	}
}
