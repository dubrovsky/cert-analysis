import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {APP_CONFIG_TOKEN, AppConfig} from "../../app.config";
import {Observable} from "rxjs";
import {NotificationGroup} from "../model/notification-group.model";

@Injectable({
    providedIn: 'root'
})
export class NotificationGroupService {

    apiUrl: string;

    constructor(protected http: HttpClient, @Inject(APP_CONFIG_TOKEN) public appConfig: AppConfig) {
        this.apiUrl = appConfig.apiUrl + '/notification-group';
    }

    findAll(): Observable<NotificationGroup[]> {
        return this.http.get<NotificationGroup[]>(this.apiUrl + 's');
    }
}
