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

package org.streampipes.wrapper.spark;

import java.io.Serializable;

public class SparkDeploymentConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private String jarFile;
    private String appName;
    private String sparkHost;
    private long sparkBatchDuration;
    private String kafkaHost;
    private boolean runLocal;

    public SparkDeploymentConfig(String jarFile, String appName, String sparkHost, boolean runLocal, long sparkBatchDuration, String kafkaHost) {
        super();

        this.jarFile = jarFile;
        this.appName = appName;
        this.sparkHost = sparkHost;
        this.runLocal = runLocal;
        this.sparkBatchDuration = sparkBatchDuration;
        this.kafkaHost = kafkaHost;//TODO: JMS berücksichtigen

    }

    public SparkDeploymentConfig(String jarFile, String appName, String sparkHost, long sparkBatchDuration, String kafkaHost) {
        this(jarFile, appName, sparkHost, false, sparkBatchDuration, kafkaHost);
    }

    public SparkDeploymentConfig(String jarFile, String appName, String sparkHost, String kafkaHost) {
        this(jarFile, appName, sparkHost, 1000, kafkaHost);
    }

    public String getJarFile() {
        return jarFile;
    }

    public void setJarFile(String jarFile) {
        this.jarFile = jarFile;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getSparkHost() {
        return sparkHost;
    }

    public void setSparkHost(String sparkHost) {
        this.sparkHost = sparkHost;
    }

    public boolean isRunLocal() {
        return runLocal;
    }

    public void setRunLocal(boolean runLocal) {
        this.runLocal = runLocal;
    }

    public long getSparkBatchDuration() {
        return sparkBatchDuration;
    }

    public void setSparkBatchDuration(long sparkBatchDuration) {
        this.sparkBatchDuration = sparkBatchDuration;
    }

    public String getKafkaHost() {
        return kafkaHost;
    }

    public void setKafkaHost(String kafkaHost) {
        this.kafkaHost = kafkaHost;
    }
}
