import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "../../shared/authentication/authentication.service";
import {UserDTO} from "../../entities/user/shared/user-dto.model";

@Component({
    selector: 'app-main',
    templateUrl: './main.component.html',
    styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

    currentUser: UserDTO;

    constructor(private authenticationService: AuthenticationService) {
        this.authenticationService.currentUser$.subscribe(user => this.currentUser = user);
    }

    /*get isAdmin() {
        return this.currentUser && this.currentUser.authorities.indexOf('Role.Admin') != -1;
    }*/

    ngOnInit() {
    }

}
