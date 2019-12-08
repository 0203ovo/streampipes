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

import {Component, EventEmitter, Output} from '@angular/core';
import {TransportProcessEventModel} from "./model/transport-process-event.model";
import {AppTransportMonitoringRestService} from "./services/app-transport-monitoring-rest.service";
import {ParcelInfoEventModel} from "./model/parcel-info-event.model";
import {DetectedBoxModel} from "./model/detected-box.model";

@Component({
    selector: 'app-transport-monitoring',
    templateUrl: './app-transport-monitoring.component.html',
    styleUrls: ['./app-transport-monitoring.component.css']
})
export class AppTransportMonitoringComponent {


    selectedIndex: number = 0;
    @Output() appOpened = new EventEmitter<boolean>();

    incomingExpanded: boolean = true;
    transportExpanded: boolean = true;
    outgoingExpanded: boolean = true;
    summaryExpanded: boolean = true;

    transportProcessSelected: boolean = false;
    selectedTransportProcess: TransportProcessEventModel;

    incomingParcelInfo: ParcelInfoEventModel[];
    outgoingParcelInfo: ParcelInfoEventModel[];

    incomingParcelInfoPresent: boolean = false;
    outgoingParcelInfoPresent: boolean = false;

    outgoingBoxCount: DetectedBoxModel = {totalBoxCount: 0, transparentBoxCount: 0, cardboardBoxCount: 0};
    incomingBoxCount: DetectedBoxModel = {totalBoxCount: 0, transparentBoxCount: 0, cardboardBoxCount: 0};

    constructor(private restService: AppTransportMonitoringRestService) {

    }

    ngOnInit() {
        this.appOpened.emit(true);
        this.incomingParcelInfo = [];
        this.outgoingParcelInfo = [];
    }

    selectedIndexChange(index: number) {
        this.selectedIndex = index;
    }

    selectTransportProcess(transportProcess: TransportProcessEventModel) {
        this.selectedTransportProcess = transportProcess;
        this.transportProcessSelected = true;
        this.fetchOutgoingParcelInfo();
        this.fetchIncomingParcelInfo();
    }

    fetchOutgoingParcelInfo() {
        this.restService.getOutgoingParcelInfo(this.selectedTransportProcess.startTime, this.selectedTransportProcess.endTime).subscribe(resp => {
            this.outgoingParcelInfo = resp.events;
            this.outgoingParcelInfoPresent = true;
        });
    }

    fetchIncomingParcelInfo() {
        this.restService.getIncomingParcelInfo(this.selectedTransportProcess.startTime, this.selectedTransportProcess.endTime).subscribe(resp => {
            this.incomingParcelInfo = resp.events;
            this.incomingParcelInfoPresent = true;
        });
    }

    truncateTransportProcessDatabase() {
        this.restService.truncateTransportProcessDb();
    }

    truncateIncomingGoodsDatabase() {
        this.restService.truncateIncomingGoodsDb();
    }

    truncateOutgoingGoodsDatabase() {
        this.restService.truncateOutgoingGoodsDb();
    }

    truncateParcelActivitiesDatabase() {
        this.restService.truncateParcelActivitiesDb();
    }

    truncateParcelOpenBoxDatabase() {
        this.restService.truncateParcelOpenBoxDb();
    }

    updateOutgoingBoxCount(boxCount: DetectedBoxModel) {
        console.log("outgoing");
        this.outgoingBoxCount = boxCount;
    }

    updateIncomingBoxCount(boxCount: DetectedBoxModel) {
        this.incomingBoxCount = boxCount;
    }


}