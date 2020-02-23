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

import {WidgetInstances} from '../../widget-instances.service'

'use strict';
declare const require: any;
trafficlightWidget.$inject = ['WidgetInstances'];

export default function trafficlightWidget(WidgetInstances) {
    return {
        restrict: 'A',
        replace: true,
        template: require('./trafficlight.html'),
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope) {
            WidgetInstances.get($scope.widgetId).then(function(data) {
                $scope.selectedNumberProperty = data.visualisation.schema.selectedNumberProperty.properties.runtimeName;
                $scope.criticalThresholdOperator = data.visualisation.config.critical.operator;
                $scope.criticalThresholdValue = data.visualisation.config.critical.value;
                $scope.criticalThresholdRange = data.visualisation.config.critical.range;
            });

            $scope.active = function(id, value) {
                if ((exceedsThreshold(value) && id == 0) ||
                    (isInWarningRange(value) && id == 1) ||
                    (isInOkRange(value) && id == 2)) {
                    return "active";
                } else return "";
            }

            var exceedsThreshold = function(value) {
                if ($scope.criticalThresholdOperator == 'ge') {
                    return value >= $scope.criticalThresholdValue;
                } else {
                    return value <= $scope.criticalThresholdValue;
                }
            }

            var isInWarningRange = function(value) {
                if (exceedsThreshold(value)) return false;
                else {
                    if ($scope.criticalThresholdOperator == 'ge') {
                        return value >= ($scope.criticalThresholdValue - $scope.criticalThresholdValue*($scope.criticalThresholdRange/100));
                    } else {
                        return value <= ($scope.criticalThresholdValue + $scope.criticalThresholdValue*($scope.criticalThresholdRange/100));
                    }
                }
            }
            var isInOkRange = function(value) {
                return !exceedsThreshold(value) && !isInWarningRange(value);
            }
        },
        link: function postLink(scope) {
            scope.$watch('data', function (data) {
                if (data) {
                    scope.item = data;
                }
            });
        }
    };
};
