import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "../../shared/authentication/authentication.service";
import {CurrentUserDTO} from "../../entities/user/shared/current-user-dto.model";

@Component({
    selector: 'app-main',
    templateUrl: './main.component.html',
    styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

    currentUser: CurrentUserDTO;

    constructor(private authenticationService: AuthenticationService) {
    }

    /*get isAdmin() {
        return this.currentUser && this.currentUser.authorities.indexOf('Role.Admin') != -1;
    }*/

    ngOnInit() {
        this.authenticationService.currentUser$.subscribe(user => {
            this.currentUser = user;
        });
    }

}
