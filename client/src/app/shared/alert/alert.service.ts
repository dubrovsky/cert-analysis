import {Injectable} from '@angular/core';
import {NavigationStart, Router} from "@angular/router";
import {Observable, Subject} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class AlertService {

    private subject = new Subject<any>();
    private keepAfterNavigationChange = false;

    constructor(private router: Router) {
        // clear alert message on route change
        router.events.subscribe(event => {
            if (event instanceof NavigationStart) {
                if (this.keepAfterNavigationChange) {
                    // only keep for a single location change
                    this.keepAfterNavigationChange = false;
                } else {
                    // clear alert
                    this.subject.next();
                }
            }
        });
    }

    success(message: string, keepAfterNavigationChange = false) {
        this.keepAfterNavigationChange = keepAfterNavigationChange;
        this.subject.next({severity: 'success', summary: 'Операция прошла успешно', detail: message});
    }

    error(message: string, keepAfterNavigationChange = true) {
        this.keepAfterNavigationChange = keepAfterNavigationChange;
        this.subject.next({severity: 'error', summary: 'Ошибка: ', detail: message});
    }

    getMessage(): Observable<any> {
        return this.subject.asObservable();
    }
}
