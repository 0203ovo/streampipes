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

import {Component, Inject} from "@angular/core";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestApi} from "../../../services/rest-api.service";
import {RestService} from "../../services/rest.service";
import {ElementIconText} from "../../../services/get-element-icon-text.service";
import {SelectedVisualizationData} from "../../model/selected-visualization-data.model";

@Component({
    selector: 'add-pipeline-dialog-component',
    templateUrl: './add-pipeline-dialog.component.html',
    styleUrls: ['./add-pipeline-dialog.component.css']
})
export class AddPipelineDialogComponent {

    pages = [{
        type: "select-pipeline",
        title: "Select Pipeline",
        description: "Select a pipeline you'd like to visualize"
    }, {
        type: "select-measurement",
        title: "Measurement Value",
        description: "Select measurement"
    }, {
        type: "select-label",
        title: "Label",
        description: "Choose label"
    }];

    visualizablePipelines = [];

    selectedVisualization: any;
    selectedType: any;
    selectedMeasurement: any;
    page: any = "select-pipeline";

    selectedLabelBackgroundColor: string = "#FFFFFF";
    selectedLabelTextColor: string = "#1B1464";
    selectedMeasurementBackgroundColor: string = "#39B54A";
    selectedMeasurementTextColor: string = "#FFFFFF";
    selectedLabel: string;


    constructor(
        public dialogRef: MatDialogRef<AddPipelineDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: SelectedVisualizationData,
        private restApi: RestApi,
        private restService: RestService,
        public elementIconText: ElementIconText) {
    }

    ngOnInit() {
        this.restService.getVisualizablePipelines().subscribe(visualizations => {
            visualizations.rows.forEach(vis => {
                this.restService.getPipeline(vis.doc.pipelineId)
                    .subscribe(pipeline => {
                        vis.doc.name = pipeline.name;
                        this.visualizablePipelines.push(vis);
                    });
            });
        });
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    getSelectedPipelineCss(vis) {
        return this.getSelectedCss(this.selectedVisualization, vis);
    }

    getSelectedVisTypeCss(type) {
        return this.getSelectedCss(this.selectedType, type);
    }

    getSelectedCss(selected, current) {
        if (selected == current) {
            return "wizard-preview wizard-preview-selected";
        } else {
            return "wizard-preview";
        }
    }

    getTabCss(page) {
        if (page == this.page) return "md-fab md-accent";
        else return "md-fab md-accent wizard-inactive";
    }

    selectPipeline(vis) {
        this.selectedVisualization = vis;
        this.next();

    }

    next() {
        if (this.page == 'select-pipeline') {
            this.page = 'select-measurement';
        } else if (this.page == 'select-measurement') {
            this.page = 'select-label';
        } else {

            let selectedConfig:SelectedVisualizationData = {} as SelectedVisualizationData;
            selectedConfig.labelBackgroundColor = this.selectedLabelBackgroundColor;
            selectedConfig.labelTextColor = this.selectedLabelTextColor;
            selectedConfig.measurementBackgroundColor = this.selectedMeasurementBackgroundColor;
            selectedConfig.measurementTextColor = this.selectedMeasurementTextColor;
            selectedConfig.measurement = this.selectedMeasurement;
            selectedConfig.visualizationId = this.selectedVisualization.pipelineId;
            selectedConfig.label = this.selectedLabel;
            selectedConfig.brokerUrl = this.selectedVisualization.broker;
            selectedConfig.topic = this.selectedVisualization.topic;

            this.dialogRef.close(selectedConfig);
        }
    }

    back() {
        if (this.page == 'select-measurement') {
            this.page = 'select-pipeline';
        } else if (this.page == 'select-label') {
            this.page = 'select-measurement';
        }
    }

    iconText(s) {
        return this.elementIconText.getElementIconText(s);
    }

}