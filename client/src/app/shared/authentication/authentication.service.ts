import {Inject, Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {User} from "../model/user.model";
import {BrowserStorageService} from "../browser-storage/browser-storage.service";
import {APP_CONFIG_TOKEN, AppConfig} from "../../app.config";
import {map, switchMap} from "rxjs/operators";
import {UserService} from "../../entities/user/shared/user.service";
import {UserDTO} from "../../entities/user/shared/user-dto.model";

@Injectable({
    providedIn: 'root'
})
export class AuthenticationService {

    private currentUserSubject: BehaviorSubject<User>;
    public currentUser$: Observable<User>;

    constructor(
        private http: HttpClient,
        private browserStorageService: BrowserStorageService,
        private userService: UserService,
        @Inject(APP_CONFIG_TOKEN) private appConfig: AppConfig
    ) {
        this.currentUserSubject = new BehaviorSubject<User>(JSON.parse(browserStorageService.get('currentUser')));
        this.currentUser$ = this.currentUserSubject.asObservable();
    }

    public get currentUserValue(): User {
        return this.currentUserSubject.value;
    }

    login(username: string, password: string) {
        const data =
            `username=${encodeURIComponent(username)}` +
            `&password=${encodeURIComponent(password)}`;

        const headers = new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded');

        return this.http.post<any>(`${this.appConfig.apiUrl}/authentication`, data, {headers})
            .pipe(switchMap(() => {
                return this.userService.fetchAccount().pipe(map((user: UserDTO) => {
                    if (user) {
                        // store user details in local storage to keep user logged in between page refreshes
                        this.browserStorageService.set('currentUser', JSON.stringify(user));
                        this.currentUserSubject.next(user);
                    }
                    return user;
                }));
            }));
    }

    logout() {
        return this.http.post<any>(`${this.appConfig.apiUrl}/logout`, {})
            .pipe(map(() => {// remove user from local storage to log user out
                this.browserStorageService.remove('currentUser');
                this.currentUserSubject.next(null);
            }));
    }
}
