import {Component, OnInit} from '@angular/core';
import {ConfirmationService, MenuItem} from "primeng/api";
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../shared/user.service";
import {UserDTO} from "../shared/user-dto.model";

@Component({
    selector: 'app-user-list',
    templateUrl: './user-list.component.html',
    styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {

    contextMenuItems: MenuItem[];
    loading: boolean;
    users: UserDTO[];
    selectedUser: UserDTO;

    constructor(
        private userService: UserService,
        private confirmationService: ConfirmationService,
        private router: Router,
        private route: ActivatedRoute
    ) {

        this.contextMenuItems = [
            {label: 'Редактировать', icon: 'pi pi-pencil', command: this.onEditUserClick},
            {label: 'Удалить', icon: 'pi pi-times', command: this.onDeleteUserClick}
        ];
    }

    ngOnInit() {
        this.loadUsers();
    }

    loadUsers() {
        this.loading = true;
        this.userService.findAll().subscribe(
            users => {
                this.users = users;
                this.loading = false;
            }
        );
    }

    private onEditUserClick = (event) => {
        this.router.navigate([]);
    };

    private onDeleteUserClick = (event) => {
        this.router.navigate([]);
    };

}
