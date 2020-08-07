import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthenticationService} from "../authentication/authentication.service";
import {CommunicationService} from "../communication/communication.service";
import {Subscription} from "rxjs";

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

    loginForm: FormGroup;
    private returnUrl = '/';
    private returnUrlSubscription: Subscription;

    constructor(
        private formBuilder: FormBuilder,
        private route: ActivatedRoute,
        private router: Router,
        private authenticationService: AuthenticationService,
        private communicationService: CommunicationService
    ) {
        if (this.authenticationService.currentUserValue) {
            this.router.navigate([this.returnUrl]);
        }
        this.returnUrlSubscription = this.communicationService.returnUrl$.subscribe(returnUrl => {
            this.returnUrl = returnUrl;
        });
    }

    get form() {
        return this.loginForm.controls;
    }

    ngOnInit() {
        this.loginForm = this.formBuilder.group({
            username: ['', Validators.required],
            password: ['', Validators.required]
        });

        // get return url from route parameters or default to '/'
        // this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    }

    ngOnDestroy(): void {
        this.returnUrlSubscription.unsubscribe();
    }

    onSubmitClick() {
        if (this.loginForm.valid) {
            this.authenticationService.login(this.form.username.value, this.form.password.value).subscribe(() => {
                this.router.navigate([this.returnUrl]);
            });
        }
    }

}
