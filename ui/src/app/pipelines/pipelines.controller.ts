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

import * as angular from 'angular';
import * as FileSaver from 'file-saver';

declare const jsPlumb: any;
declare const require: any;

import {StartAllPipelinesController} from './dialog/start-all-pipelines-dialog.controller';
import {PipelineCategoriesDialogController} from './dialog/pipeline-categories-dialog.controller';
import {ElementIconText} from "../services/get-element-icon-text.service";
import {ImportPipelineDialogController} from "./dialog/import-pipeline-dialog.controller";

export class PipelineCtrl {

    RestApi: any;
    $mdDialog: any;
    $state: any;
    $timeout: any;
    $stateParams: any;
    ImageChecker: any;
    ElementIconText: any;
    pipeline: any;
    pipelines: any;
    systemPipelines: any;
    pipelinShowing: any;
    pipelinePlumb: any;
    starting: any;
    stopping: any;
    pipelineCategories: any;
    activeCategory: any;
    startPipelineDirectly: any;


    constructor(RestApi, $mdDialog, $state, $timeout, $stateParams, ImageChecker, ElementIconText) {
        this.RestApi = RestApi;
        this.$mdDialog = $mdDialog;
        this.$state = $state;
        this.$timeout = $timeout;
        this.$stateParams = $stateParams;
        this.ImageChecker = ImageChecker;
        this.ElementIconText = ElementIconText;

        this.pipeline = {};
        this.pipelines = [];
        this.systemPipelines = [];
        this.pipelinShowing = false;
        this.pipelinePlumb = jsPlumb.getInstance({Container: "pipelineDisplay"});
        this.starting = false;
        this.stopping = false;

        this.pipelineCategories = [];
        this.activeCategory = "";

        this.startPipelineDirectly = $stateParams.pipeline;
    }

    $onInit() {
        this.getPipelineCategories();
        this.getPipelines();
        this.getSystemPipelines();
    }

    $onDestroy() {
        this.pipelinePlumb.deleteEveryEndpoint();
    }

    setSelectedTab(categoryId) {
        this.activeCategory = categoryId;
    }

    exportPipelines() {
        let blob = new Blob([JSON.stringify(this.pipelines)], {type: "application/json"})
        FileSaver.saveAs(blob, "pipelines.json");
    }


    getPipelines() {
        this.RestApi.getOwnPipelines()
            .then(pipelines => {
                this.pipelines = pipelines.data;
                if (this.startPipelineDirectly != "") {
                    angular.forEach(this.pipelines, pipeline => {
                        if (pipeline._id == this.startPipelineDirectly) {
                            pipeline.immediateStart = true;
                        }
                    });
                    this.startPipelineDirectly = "";
                }
            });

    };

    getSystemPipelines() {
        this.RestApi.getSystemPipelines()
            .then(pipelines => {
                this.systemPipelines = pipelines.data;
            });
    }

    getPipelineCategories() {
        this.RestApi.getPipelineCategories()
            .then(pipelineCategories => {
                this.pipelineCategories = pipelineCategories.data;
            });

    };

    isTextIconShown(element) {
        return element.iconUrl == null || element.iconUrl == 'http://localhost:8080/img' || typeof element.iconUrl === 'undefined';

    };

    activeClass(pipeline) {
        return 'active-pipeline';
    }

    checkCurrentSelectionStatus(status) {
        var active = true;
        angular.forEach(this.pipelines, pipeline => {
            if (this.activeCategory == "" || pipeline.pipelineCategories == this.activeCategory) {
                if (pipeline.running == status) {
                    active = false;
                }
            }
        });

        return active;
    }

    openImportPipelinesDialog() {
        this.$mdDialog.show({
            controller: ImportPipelineDialogController,
            controllerAs: 'ctrl',
            template: require('./dialog/import-pipeline-dialog.tmpl.html'),
            parent: angular.element(document.body),
            clickOutsideToClose: false,
            locals: {
                pipelines: this.pipelines,
                refreshPipelines: () => {
                    return this.refreshPipelines();
                }
            },
            bindToController: true
        })
    }

    startAllPipelines(action) {
        this.$mdDialog.show({
            controller: StartAllPipelinesController,
            controllerAs: 'ctrl',
            template: require('./dialog/start-all-pipelines-dialog.tmpl.html'),
            parent: angular.element(document.body),
            clickOutsideToClose: false,
            locals: {
                pipelines: this.pipelines,
                action: action,
                activeCategory: this.activeCategory,
                refreshPipelines: () => {
                    return this.refreshPipelines();
                }
            },
            bindToController: true
        })
    }

    showPipelineCategoriesDialog() {
        this.$mdDialog.show({
            controller: PipelineCategoriesDialogController,
            controllerAs: 'ctrl',
            template: require('./dialog/pipeline-categories-dialog.tmpl.html'),
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            locals: {
                pipelines: this.pipelines,
                getPipelineCategories: () => {
                    return this.getPipelineCategories();
                },
                refreshPipelines: () => {
                    return this.refreshPipelines();
                }
            },
            bindToController: true
        })
    };

    refreshPipelines() {
        this.getPipelines();
        this.getSystemPipelines();
    }


    showPipeline(pipeline) {
        pipeline.display = !pipeline.display;
    }


    addImageOrTextIcon($element, json) {
        this.ImageChecker.imageExists(json.properties.iconUrl, function (exists) {
            if (exists) {
                var $img = $('<img>')
                    .attr("src", json.properties.iconUrl)
                    .addClass('pipeline-display-element-img');
                $element.append($img);
            } else {
                var $span = $("<span>")
                    .text((<any>ElementIconText).getElementIconText(json.properties.name) || "N/A")
                    .addClass("element-text-icon")
                $element.append($span);
            }
        });
    }

    elementTextIcon(string) {
        var result = "";
        if (string.length <= 4) {
            result = string;
        } else {
            var words = string.split(" ");
            words.forEach(function (word, i) {
                result += word.charAt(0);
            });
        }
        return result.toUpperCase();
    };


}

PipelineCtrl.$inject = ['RestApi', '$mdDialog', '$state', '$timeout', '$stateParams', 'ImageChecker', 'ElementIconText'];