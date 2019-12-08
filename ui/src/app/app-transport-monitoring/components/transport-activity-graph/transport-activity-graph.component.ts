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

import {Component, Input} from "@angular/core";
import {ActivityEventModel} from "../../model/activity-event.model";
import {TimestampConverterService} from "../../services/timestamp-converter.service";

@Component({
    selector: 'transport-activity-graph',
    templateUrl: './transport-activity-graph.component.html',
    styleUrls: ['./transport-activity-graph.component.css']
})
export class TransportActivityGraphComponent {

    polarChartDataActivity: any = [];
    _activityEventModel: ActivityEventModel[];

    constructor(private timestampConverterService: TimestampConverterService) {

    }

    ngOnInit() {
    }

    @Input()
    set activityEventModel(activityEventModel: ActivityEventModel[]) {
        this._activityEventModel = activityEventModel;
        this.prepareNewPolarChart();
    }

    ngAfterViewInit() {
        //this.makeDummyData();

    }

    prepareNewPolarChart() {
        let normalSeries: any = [];
        let shakeSeries: any = [];
        let fallSeries: any = [];

        this._activityEventModel.forEach(activity => {
            normalSeries.push({"name": this.timestampConverterService.convertTimestampHoursOnly(activity.timestamp), "value": this.getActivityValue("normal", 1, activity.activity)});
            shakeSeries.push({"name": this.timestampConverterService.convertTimestampHoursOnly(activity.timestamp), "value": this.getActivityValue("shake", 2, activity.activity)});
            fallSeries.push({"name": this.timestampConverterService.convertTimestampHoursOnly(activity.timestamp), "value": this.getActivityValue("fall_down", 3, activity.activity)});
        });

        this.polarChartDataActivity = [{"name": "Normal", series: normalSeries}, {"name": "Shake", series: shakeSeries}, {"name": "Fall", series: fallSeries}];
    }

    getActivityValue(activeActivity, value, activity): number {
        return activity == activeActivity ? value : 0;
    }

    makeDummyData() {
        this.activityEventModel = [];
        let currentTimestamp = 0;
        let currentActivity = "normal";
        let frequency = 1000;
        for(let i = 0; i < 200; i++) {
            let nextActivity = this.getRandomActivity(currentActivity);
            this.activityEventModel.push({timestamp: currentTimestamp,  activity: nextActivity});
            currentTimestamp += frequency;
            currentActivity = nextActivity;
        }
    }

    getRandomActivity(currentActivity: string) {
        let random = Math.random();
        if (random < 0.4) {
            return currentActivity;
        } else if (random >=0.4 && random < 0.6) {
            return "normal";
        } else if (random >= 0.6 && random < 0.8) {
            return "shake";
        } else {
            return "fall";
        }
    }

}