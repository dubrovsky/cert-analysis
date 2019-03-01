import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "../../shared/authentication/authentication.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-toolbar',
    templateUrl: './toolbar.component.html',
    styleUrls: ['./toolbar.component.css']
})
export class ToolbarComponent implements OnInit {

    constructor(
        private router: Router,
        private authenticationService: AuthenticationService
    ) {
    }

    ngOnInit() {
    }

    onLogoutClick(event) {
        this.authenticationService.logout().subscribe(() => this.router.navigate(['/login']));
    }

    getUser() {
        return this.authenticationService.currentUserValue.name;
    }
}
