/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {Component, Input} from '@angular/core';
import {DatalakeRestService} from '../../../core-services/datalake/datalake-rest.service';
import {MatDialog, MatSnackBar} from '@angular/material';
import {FormControl} from '@angular/forms';
import {DatalakeLineChartDataDownloadDialog} from './datadownloadDialog/datalake-lineChart-dataDownload.dialog';

@Component({
    selector: 'sp-datalake-lineChart',
    templateUrl: './datalake-lineChart.component.html',
    styleUrls: ['./datalake-lineChart.component.css']
})
export class DatalakeLineChartComponent {

    @Input() set index(value: string) {
        this._index = value;
        this.loadAllData();
        this.enablePaging = true;
        this.enableItemsPerPage = true;
        this.selectedTimeUnit = 'All';
    }
    _index: string;
    data;

    //Line Chart configs
    yAxesKeys: [] = undefined;
    xAxesKey = 'time';
    currentPage: number = 0;
    maxPage: number = 0;
    itemsPerPage = 50;
    enablePaging = false;
    enableItemsPerPage = false;
    isLoadingData = false;

    //index selections
    myControl = new FormControl();
    dataKeys: string[] = [];

    //timeunit selection
    selectedTimeUnit = 'All';

    //aggregation / advanced options
    //group by
    enableAdvanceOptions = false;
    groupbyUnit = 'd';
    groupbyValue = 1;

    //custom time range
    customStartDate = new Date();
    customEndDate = new Date(this.customStartDate.getTime() + 60000 * 60 * 24);

    constructor(private restService: DatalakeRestService, private snackBar: MatSnackBar, public dialog: MatDialog) {

    }

    paging(page) {
        this.isLoadingData = true;
        this.restService.getDataPage(this._index, this.itemsPerPage, page).subscribe(
            res => {
                if(res.events.length > 0) {
                    this.currentPage = res.page;
                    this.maxPage = res.pageSum;
                    this.data = res.events as [];
                } else {
                    this.openSnackBar('No data found on page ' + page);
                }
                this.isLoadingData = false;
            });
    }

    loadData() {
        if (this.selectedTimeUnit === 'All') {
            this.loadAllData();
            this.enablePaging = true;
            this.enableItemsPerPage = true;
        } else if(this.selectedTimeUnit == 'Custom') {
            this.loadCustomData();
            this.enablePaging = false;
            this.enableItemsPerPage = false;
        } else {
            this.enablePaging = false;
            this.enableItemsPerPage = false;
            this.loadLastData();
        }
    }

    loadAllData() {
        this.isLoadingData = true;
        this.restService.getDataPageWithoutPage(this._index,this.itemsPerPage).subscribe(
            res => {
                if(res.events.length > 0) {
                    this.currentPage = res.page;
                    this.maxPage = res.pageSum;
                    this.data = res.events as [];
                    this.setDataKeys(res.events)
                } else {
                    this.data = undefined;
                }
                this.isLoadingData = false;
            }
        );
    }

    loadLastData() {
        let timeunit = '';
        let timevalue = 0;
        if (this.selectedTimeUnit === '24 Hours') {
            timeunit = 'h';
            timevalue = 24;
        } else if (this.selectedTimeUnit === '1 Week') {
            timeunit = 'w';
            timevalue = 1;
        } else if (this.selectedTimeUnit === '1 Month') {
            timeunit = 'w';
            timevalue = 4;
        } else if (this.selectedTimeUnit === '1 Year') {
            timeunit = 'd';
            timevalue = 365;
        }

        this.isLoadingData = true;
        if (this.enableAdvanceOptions) {
            let groupbyUnit = this.groupbyUnit;
            let groupbyValue = this.groupbyValue;
            if (this.groupbyUnit === 'month') {
                groupbyUnit = 'd';
                groupbyValue = 30 * this.groupbyValue;
            } else if(this.groupbyUnit === 'year') {
                groupbyUnit = 'd';
                groupbyValue = 365 * this.groupbyValue;
            }

            this.restService.getLastData(this._index, timeunit, timevalue, groupbyUnit, groupbyValue).subscribe(
                res => this.processRevicedData(res)
            );
        } else {
            this.restService.getLastDataAutoAggregation(this._index, timeunit, timevalue).subscribe(
                res => this.processRevicedData(res)
            );
        }


    }

    loadCustomData() {
        this.isLoadingData = true;
        let groupbyUnit = this.groupbyUnit;
        let groupbyValue = this.groupbyValue;
        if (this.groupbyUnit === 'month') {
            groupbyUnit = 'w';
            this.groupbyValue = 4 * this.groupbyValue;
        } else if(this.groupbyUnit === 'year') {
            groupbyUnit = 'd';
            this.groupbyValue = 365 * this.groupbyValue;
        }
        if (this.enableAdvanceOptions) {
            this.restService.getData(this._index, this.customStartDate.getTime(), this.customEndDate.getTime(), groupbyUnit, groupbyValue).subscribe(
                res => this.processRevicedData(res)
            );
        } else {
            this.restService.getDataAutoAggergation(this._index, this.customStartDate.getTime(), this.customEndDate.getTime()).subscribe(
                res => this.processRevicedData(res)
            );
        }

    }

    processRevicedData(res) {
        if(res.events.length > 0) {
            this.data = res.events as [];
            this.setDataKeys(res.events);
            this.currentPage = undefined;
        } else {
            this.data = undefined;
        }
        this.isLoadingData = false;
    }

    selectKey(value) {
        this.yAxesKeys = value;
    }

    setDataKeys(events) {
        this.dataKeys = [];
        for (let event of events) {
            for (let key in event) {
                if (typeof event[key] == 'number') {
                    if (!this.dataKeys.includes(key)) {
                        this.dataKeys.push(key)
                    }
                }
            }
        }
    }

    selectTimeUnit(value) {
        this.selectedTimeUnit = value;

        if (this.selectedTimeUnit === '24 Hours') {
            this.groupbyUnit = 'm';
            this.groupbyValue = 1;
        } else if (this.selectedTimeUnit === '1 Week') {
            this.groupbyUnit = 'm';
            this.groupbyValue = 30;
        } else if (this.selectedTimeUnit === '1 Month') {
            this.groupbyUnit = 'h';
            this.groupbyValue = 4;
        } else if (this.selectedTimeUnit === '1 Year') {
            this.groupbyUnit = 'h';
            this.groupbyValue = 12;
        }

        this.loadData();
    }

    downloadDataAsFile() {
        const dialogRef = this.dialog.open(DatalakeLineChartDataDownloadDialog, {
            width: '600px',
            data: {data: this.data, xAxesKey: this.xAxesKey, yAxesKeys: this.yAxesKeys, index: this._index}

        });
    }


    handleItemsPerPageChange(value) {
        this.itemsPerPage = value;
        this.loadData()
    }

    handleNextPage() {
        this.paging(this.currentPage + 1);
    }

    handlePreviousPage() {
        if(this.currentPage >= 0)
            this.paging(this.currentPage - 1);
    }

    handleFirstPage() {
        this.paging(0);
    }

    handleLastPage() {
        this.loadAllData()
    }

    openSnackBar(message: string) {
        this.snackBar.open(message, 'Close', {
            duration: 2000,
        });
    }

}

