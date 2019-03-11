import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {APP_CONFIG_TOKEN, AppConfig} from "../../../app.config";
import {Observable} from "rxjs";
import {Scheme} from "../../../shared/model/scheme.model";
import {SchemeDTO} from "./scheme-dto.model";

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
