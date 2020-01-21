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

import { WidgetInstances } from '../../widget-instances.service'
'use strict';
mapWidget.$inject = ['WidgetInstances', 'NgMap'];
declare let L;
declare const require: any;

export default function mapWidget(WidgetInstances, NgMap) {
    return {
        restrict: 'A',
        replace: true,
        template: require('./map.html'),
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope) {
            $scope.refocus = true;
            $scope.markers = {};
            $scope.markersTimeout = {};

            WidgetInstances.get($scope.widgetId).then(function (data) {
                $scope.widgetConfig = data.visualisation.schema.config;
            });

            $scope.map = L.map("map");

            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            }).addTo($scope.map);

            this.toggleRefocus = function () {
                $scope.refocus = !$scope.refocus;
            }
        },
        link: function postLink(scope) {
            var carIcon = L.icon({
                iconUrl: 'assets/img/pe_icons/car.png',

                iconSize:     [15, 15], // size of the icon
                shadowSize:   [0, 0], // size of the shadow
                iconAnchor:   [0, 0], // point of the icon which will correspond to marker's location
                shadowAnchor: [0, 0],  // the same for the shadow
                popupAnchor:  [0, 0] // point from which the popup should open relative to the iconAnchor
            });

            var getId = function (data) {
                return data[scope.widgetConfig.selectedLabelMapping.properties.runtimeName];
            };

            var getMarker = function(data) {


                var lat = data[scope.widgetConfig.selectedLatitudeMapping.properties.runtimeName];
                var long = data[scope.widgetConfig.selectedLongitudeMapping.properties.runtimeName];
                var text = "<h4>Id: " + getId(data) + "</h4></br>";
                for (var key in data) {;
                    text =  text.concat("<b>" +key +"</b>" +  ": " + data[key] + "<br>");
                }

                var marker;
                if (scope.widgetConfig.selectedMarkerType == "Default") {
                    marker = L.marker([lat, long])
                        .addTo(scope.map)
                        .bindPopup(text);
                } else {
                    marker = L.marker([lat, long], {icon: carIcon})
                        .addTo(scope.map)
                        .bindPopup(text);
                }

                return marker;

            };

            scope.$watch('data', function (data) {
                if (data) {

                    var marker = getMarker(data);
                    var id = getId(data);

                    if (id in scope.markers) {
                        scope.map.removeLayer(scope.markers[id]);
                    }

                    var currentDate = new Date().getTime();
                    scope.markers[id] = marker;
                    scope.markersTimeout[id] = currentDate;

                    if (scope.refocus) {
                        var group = new L.featureGroup(Object.values(scope.markers));
                        scope.map.fitBounds(group.getBounds());
                    }


                    for (var key in scope.markersTimeout) {

                        if (scope.markersTimeout[key] + 5000 < currentDate) {
                            scope.map.removeLayer(scope.markers[key]);
                        }
                    }


                    // scope.map.panTo(new L.LatLng(lat, long));

                }
            });
        },
        controllerAs: 'ctrl'
    };
};