import {Component, OnDestroy, OnInit} from '@angular/core';
import {CommunicationService} from "../communication/communication.service";
import {Subscription} from "rxjs";

@Component({
    selector: 'app-progress-spinner',
    templateUrl: './progress-spinner.component.html',
    styleUrls: ['./progress-spinner.component.css']
})
export class ProgressSpinnerComponent implements OnInit, OnDestroy {

    loading: boolean;
    private subscription: Subscription;

    constructor(private communicationService: CommunicationService ) {
    }

    ngOnInit() {
        this.subscription = this.communicationService.loading$.subscribe(value => {
            this.loading = value;
        });
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

}
