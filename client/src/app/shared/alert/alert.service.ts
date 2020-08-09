import {Injectable} from '@angular/core';
import {NavigationStart, Router} from "@angular/router";
import {Observable, Subject} from "rxjs";
import {Message} from "primeng";

@Injectable({
    providedIn: 'root'
})
export class AlertService {

    private subject = new Subject<Message>();
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

    success(detail: string, data = {}, sticky = false, keepAfterNavigationChange = false, key = 'default', summary = 'Операция прошла успешно') {
        this.keepAfterNavigationChange = keepAfterNavigationChange;
        this.subject.next({key: key, severity: 'success', summary: summary, detail: detail, data: data, sticky: sticky});
    }

    error(message: string, keepAfterNavigationChange = false, key = 'default', summary = 'Ошибка') {
        this.keepAfterNavigationChange = keepAfterNavigationChange;
        this.subject.next({key: key, severity: 'error', summary: summary, detail: message});
    }

    getMessage(): Observable<Message> {
        return this.subject.asObservable();
    }
}
