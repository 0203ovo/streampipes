<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one or more
  ~ contributor license agreements.  See the NOTICE file distributed with
  ~ this work for additional information regarding copyright ownership.
  ~ The ASF licenses this file to You under the Apache License, Version 2.0
  ~ (the "License"); you may not use this file except in compliance with
  ~ the License.  You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  ~
  -->

# CLI tool for StreamPipes

All active services are defined in the system file.
All available services are in the services folder.

## Features Suggestion
* start (service-name) (--hostname "valueHostName") (--defaultip)
  * Starts StreamPipes or service
* stop (service-name) 
  * Stops StreamPipes and deletes containers
* restart (service-name) 
  * Restarts containers
* update (service-name) (--renew)
  * Downloads new docker images 
  * --renew restart containers after download
* set-template (template-name)
  * Replaces the systems file with file mode-name
* log (service-name)
  * Prints the logs of the service

* list-available
* list-active
* list-templates

* activate (service-name) (--all)
  * Adds service to system and starts
* add (service-name) (--all)
  * Adds service to system 
* deactivate {remove} (service-name)  (--all)
  * Stops container and removes from system file
* clean
  * Stops and cleans SP installation, remove networks
* remove-settings: 
  * Stops StreamPipes and deletes .env file
* set-version:
  * Change the StreamPipes version in the tmpl_env file

* generate-compose-file


## Flags

* ARG_OPTIONAL_SINGLE([hostname], , [The default hostname of your server], )
* ARG_OPTIONAL_BOOLEAN([defaultip],d, [When set the first ip is used as default])
* ARG_OPTIONAL_BOOLEAN([all],a, [Select all available StreamPipes services])


## Usage
~/argbash/argbash-2.7.0/bin/argbash sp.m4 -o sp


## Naming Files / Folders
* active-services
* services/
* system-configurations -> templates/
* tmpl_env
