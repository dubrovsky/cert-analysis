import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {APP_CONFIG_TOKEN, AppConfig} from "../../../app.config";
import {Observable} from "rxjs";
import {Scheme} from "../../../shared/model/scheme.model";

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

    find(id: number): Observable<Scheme> {
        return this.http.get<Scheme>(`${this.apiUrl}/${id}`);
    }

    create(scheme: Scheme): Observable<Scheme> {
        return this.http.post<Scheme>(this.apiUrl, scheme);
    }

    update(scheme: Scheme): Observable<Scheme> {
        return this.http.put<Scheme>(this.apiUrl, scheme);
    }

    delete(id: number): Observable<any> {
        return this.http.delete<any>(`${this.apiUrl}/${id}`);
    }
}
