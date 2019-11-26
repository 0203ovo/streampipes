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

import { Injectable } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable, Subscribable, from } from 'rxjs';
import { map } from 'rxjs/operators';



import { ProtocolDescriptionList } from './model/connect/grounding/ProtocolDescriptionList';
import { AdapterDescription } from './model/connect/AdapterDescription';
import { FormatDescriptionList } from './model/connect/grounding/FormatDescriptionList';
import { EventProperty } from './schema-editor/model/EventProperty';
import { EventPropertyNested } from './schema-editor/model/EventPropertyNested';
import { GuessSchema } from './schema-editor/model/GuessSchema';
import { AuthStatusService } from '../services/auth-status.service';
import { StatusMessage } from "./model/message/StatusMessage";
import { UnitDescription } from './model/UnitDescription';
import { TsonLdSerializerService } from '../platform-services/tsonld-serializer.service';
import { RuntimeOptionsResponse } from "./model/connect/runtime/RuntimeOptionsResponse";

@Injectable()
export class RestService {
    private host = '/streampipes-backend/';

    constructor(
        private http: HttpClient,
        private authStatusService: AuthStatusService,
        private tsonLdSerializerService: TsonLdSerializerService,
    ) { }

    addAdapter(adapter: AdapterDescription): Observable<StatusMessage> {
        return this.addAdapterDescription(adapter, '/master/adapters');
    }

    addAdapterTemplate(adapter: AdapterDescription): Observable<StatusMessage> {
        return this.addAdapterDescription(adapter, '/master/adapters/template');
    }

    fetchRemoteOptions(resolvableOptionsParameterRequest: any, adapterId: string): Observable<RuntimeOptionsResponse> {
        let promise = new Promise<RuntimeOptionsResponse>((resolve, reject) => {
            this.tsonLdSerializerService.toJsonLd(resolvableOptionsParameterRequest).subscribe(serialized => {
                const httpOptions = {
                    headers: new HttpHeaders({
                        'Content-Type': 'application/ld+json',
                    }),
                };
                this.http.post("/streampipes-connect/api/v1/"
                    + this.authStatusService.email
                    + "/master/resolvable/"
                    + encodeURIComponent(adapterId)
                    + "/configurations", serialized, httpOptions).pipe(map(response => {
                        const r = this.tsonLdSerializerService.fromJsonLd(response, 'sp:RuntimeOptionsResponse');
                        resolve(r);
                    })).subscribe();
            });
        });
        return from(promise);

    }

    addAdapterDescription(adapter: AdapterDescription, url: String): Observable<StatusMessage> {
        adapter.userName = this.authStatusService.email;
        var self = this;


        let promise = new Promise<StatusMessage>(function (resolve, reject) {
            self.tsonLdSerializerService.toJsonLd(adapter).subscribe(res => {
                const httpOptions = {
                    headers: new HttpHeaders({
                        'Content-Type': 'application/ld+json',
                    }),
                };
                self.http
                    .post(
                        '/streampipes-connect/api/v1/' + self.authStatusService.email + url,
                        res,
                        httpOptions
                    )
                    .pipe(map(response => {
                        var statusMessage = response as StatusMessage;
                        resolve(statusMessage);
                    }))
                    .subscribe();
            });
        });
        return from(promise);
    }


    getGuessSchema(adapter: AdapterDescription): Observable<GuessSchema> {
        let promise = new Promise<GuessSchema>((resolve, reject) => {
            this.tsonLdSerializerService.toJsonLd(adapter).subscribe(res => {
                return this.http
                    .post('/streampipes-connect/api/v1/' + this.authStatusService.email + '/master/guess/schema', res)
                    .pipe(map(response => {
                        if (JSON.stringify(response).includes('sp:GuessSchema')) {
                            console.log(response)
                            const r = this.tsonLdSerializerService.fromJsonLd(response, 'sp:GuessSchema');
                            r.eventSchema.eventProperties.sort((a, b) => a.index - b.index);
                            this.removeHeaderKeys(r.eventSchema.eventProperties);
                            resolve(r);
                        } else {
                            const r = this.tsonLdSerializerService.fromJsonLd(response, 'sp:ErrorMessage');
                            reject(r);
                        }

                    }))
                    .subscribe();
            });
        });
        return from(promise);
    }

    removeHeaderKeys(eventProperties: EventProperty[]) {
        // remove header key form schema
        for (let ep of eventProperties) {
            if (ep instanceof EventPropertyNested) {
                this.removeHeaderKeys((<EventPropertyNested>ep).eventProperties);
            }
        }

    }

    getSourceDetails(sourceElementId): Observable<any> {
        return this.http
            .get(this.makeUserDependentBaseUrl() + "/sources/" + encodeURIComponent(sourceElementId));
    }

    getRuntimeInfo(sourceDescription): Observable<any> {
        return this.http.post(this.makeUserDependentBaseUrl() + "/pipeline-element/runtime", sourceDescription);
    }

    makeUserDependentBaseUrl() {
        return this.host + 'api/v2/users/' + this.authStatusService.email;
    }


    getFormats(): Observable<FormatDescriptionList> {
        var self = this;
        return this.http
            .get(
                '/streampipes-connect/api/v1/riemer@fzi.de/master/description/formats'
            )
            .pipe(map(response => {
                const res = self.tsonLdSerializerService.fromJsonLd(response, 'sp:FormatDescriptionList');
                return res;
            }));
    }

    getProtocols(): Observable<ProtocolDescriptionList> {
        var self = this;
        return this.http
            .get(this.host + 'api/v2/adapter/allProtocols')
            .pipe(map(response => {
                const res = this.tsonLdSerializerService.fromJsonLd(
                    response,
                    'sp:ProtocolDescriptionList'
                );
                return res;
            }));
    }

    getFittingUnits(unitDescription: UnitDescription): Observable<UnitDescription[]> {
        return this.http
            .post<UnitDescription[]>('/streampipes-connect/api/v1/' + this.authStatusService.email + '/master/unit', unitDescription)
            .pipe(map(response => {
                const descriptions = response as UnitDescription[];
                return descriptions.filter(entry => entry.resource != unitDescription.resource)
            }));
    }


}
