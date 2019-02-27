import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {APP_CONFIG_TOKEN, AppConfig} from "../../../app.config";
import {Observable} from "rxjs";
import {UserDTO} from "./user-dto.model";

@Injectable({
    providedIn: 'root'
})
export class UserService {

    apiUrl: string;

    constructor(protected http: HttpClient, @Inject(APP_CONFIG_TOKEN) public appConfig: AppConfig) {
        this.apiUrl = appConfig.apiUrl + '/user';
    }

    fetchAccount(): Observable<UserDTO> {
        return this.http.get<UserDTO>(`${this.apiUrl}/account`);
    }
}
