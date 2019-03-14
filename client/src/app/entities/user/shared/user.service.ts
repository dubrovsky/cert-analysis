import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {APP_CONFIG_TOKEN, AppConfig} from "../../../app.config";
import {Observable} from "rxjs";
import {CurrentUserDTO} from "./current-user-dto.model";
import {UserDTO} from "./user-dto.model";
import {RoleDTO} from "./role-dto.model";

@Injectable({
    providedIn: 'root'
})
export class UserService {

    apiUrl: string;

    constructor(protected http: HttpClient, @Inject(APP_CONFIG_TOKEN) public appConfig: AppConfig) {
        this.apiUrl = appConfig.apiUrl + '/user';
    }

    findAll(): Observable<UserDTO[]> {
        return this.http.get<UserDTO[]>(this.apiUrl + 's');
    }

    findAllRoles(): Observable<RoleDTO[]> {
        return this.http.get<RoleDTO[]>(this.appConfig.apiUrl + '/roles');
    }

    find(id: number): Observable<UserDTO> {
        return this.http.get<UserDTO>(`${this.apiUrl}/${id}`);
    }

    create(scheme: UserDTO): Observable<UserDTO> {
        return this.http.post<UserDTO>(this.apiUrl, scheme);
    }

    update(scheme: UserDTO): Observable<UserDTO> {
        return this.http.put<UserDTO>(this.apiUrl, scheme);
    }

    delete(id: number): Observable<any> {
        return this.http.delete<any>(`${this.apiUrl}/${id}`);
    }

    fetchAccount(): Observable<CurrentUserDTO> {
        return this.http.get<CurrentUserDTO>(`${this.apiUrl}/account`);
    }
}
