import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {AlertService} from "./alert.service";
import {MessageService} from "primeng/api";

@Component({
    selector: 'app-alert',
    templateUrl: './alert.component.html',
    styleUrls: ['./alert.component.css']
})
export class AlertComponent implements OnInit, OnDestroy {

    private subscription: Subscription;

    constructor(
        private alertService: AlertService,
        private messageService: MessageService
    ) {
    }

    ngOnInit() {
        this.subscription = this.alertService.getMessage().subscribe(message => {
            this.messageService.clear();
            if (message) {
                this.messageService.add(message);
            }
        });
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

}
