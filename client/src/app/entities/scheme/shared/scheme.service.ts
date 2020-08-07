import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {APP_CONFIG_TOKEN, AppConfig} from "../../../app.config";
import {Observable} from "rxjs";
import {Scheme} from "../../../shared/model/scheme.model";
import {SchemeDTO} from "./scheme-dto.model";
import {NotificationGroup} from "../../../shared/model/notification-group.model";

@Injectable({
    providedIn: 'root'
})
export class SchemeService {

    apiUrl: string;

    constructor(protected http: HttpClient, @Inject(APP_CONFIG_TOKEN) public appConfig: AppConfig) {
        this.apiUrl = appConfig.apiUrl + '/scheme';
    }

    findAll(): Observable<Scheme[]> {
        return this.http.get<Scheme[]>(this.apiUrl + 's');
    }

    findNotificationGroups(id: number): Observable<NotificationGroup[]> {
        return this.http.get<NotificationGroup[]>(`${this.apiUrl}/notification-groups/${id}`);
    }

    moveUpDown(id: number, direction: string): Observable<Scheme[]> {
        return this.http.put<Scheme[]>(`${this.apiUrl}/updown/${id}`, null, {params: {direction: direction}});
    }

    find(id: number): Observable<SchemeDTO> {
        return this.http.get<SchemeDTO>(`${this.apiUrl}/${id}`);
    }

    create(scheme: SchemeDTO): Observable<SchemeDTO> {
        return this.http.post<SchemeDTO>(this.apiUrl, scheme);
    }

    update(scheme: SchemeDTO): Observable<SchemeDTO> {
        return this.http.put<SchemeDTO>(this.apiUrl, scheme);
    }

    delete(id: number): Observable<any> {
        return this.http.delete<any>(`${this.apiUrl}/${id}`);
    }
}
