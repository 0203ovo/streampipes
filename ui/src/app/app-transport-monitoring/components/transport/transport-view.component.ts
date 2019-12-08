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

import {Component, Input} from '@angular/core';
import {AppTransportMonitoringRestService} from "../../services/app-transport-monitoring-rest.service";
import {ActivityDetectionModel} from "../../model/activity-detection.model";
import {TransportProcessEventModel} from "../../model/transport-process-event.model";
import {OpenBoxModel} from "../../model/open-box.model";

@Component({
    selector: 'transport-view',
    templateUrl: './transport-view.component.html',
    styleUrls: ['./transport-view.component.css']
})
export class TransportViewComponent {

    //@Input() transportProcess: TransportProcessEventModel;

    _transportProcess: TransportProcessEventModel;

    processActivities: ActivityDetectionModel;
    openBoxActivities: OpenBoxModel = {total: "0", events: []};

    activitiesPresent: boolean = false;
    fallActivities: number = 0;
    shakeActivities: number = 0;
    normalActivities: number = 0;
    normalActivitiesTotalTime: number = 0;
    shakeActivitiesTotalTime: number = 0;

    constructor(private restService: AppTransportMonitoringRestService) {

    }

    ngOnInit() {
    }

    @Input()
    set transportProcess(transportProcess: TransportProcessEventModel) {
        this._transportProcess = transportProcess;
        this.fetchProcessActivities();
    }

    fetchProcessActivities() {
        this.restService.getActivityDetection(this._transportProcess.startTime, this._transportProcess.endTime).subscribe(resp => {
            this.processActivities = resp;
            this.activitiesPresent = true;
            this.fallActivities = this.filterRaw('fall_down');
            this.shakeActivities = this.filterRaw('shake');
            this.normalActivitiesTotalTime = this.filter('shake');
            this.shakeActivitiesTotalTime = this.filter('fall_down');
        })

        this.restService.getBoxOpenModel(this._transportProcess.startTime, this._transportProcess.endTime).subscribe(resp => {
            this.openBoxActivities = resp;
        });
    }

    filterRaw(activity: string): number {
        return this.processActivities.events.filter(pa => pa.activity == activity).length;
    }

    filter(activity: string) {
        return this.processActivities.events.filter(pa => pa.activity == activity).length / this.processActivities.events.length;
    }

    getShakePercentage() {

    }

    getThrowPercentage() {
        return this.processActivities.events.filter(pa => pa.activity == 'throw').length / this.processActivities.events.length;
    }

    getNormalPercentage() {
        return this.processActivities.events.filter(pa => pa.activity == 'normal').length / this.processActivities.events.length;
    }


}