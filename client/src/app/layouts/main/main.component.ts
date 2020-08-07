import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthenticationService} from "../../shared/authentication/authentication.service";
import {CurrentUserDTO} from "../../entities/user/shared/current-user-dto.model";
import {interval, Subscription} from "rxjs";

@Component({
    selector: 'app-main',
    templateUrl: './main.component.html',
    styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit, OnDestroy {

    currentUser: CurrentUserDTO;
    pingSubscription: Subscription;

    constructor(private authenticationService: AuthenticationService) {
    }

    ngOnInit() {
        this.authenticationService.currentUser$.subscribe(user => {
            this.currentUser = user;
        });
        this.pingSubscription = interval(10 * 60 * 1000).subscribe((val) => this.authenticationService.ping().subscribe());
    }

    ngOnDestroy(): void {
        this.pingSubscription.unsubscribe();
    }
}
