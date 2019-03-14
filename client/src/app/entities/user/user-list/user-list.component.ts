import {Component, OnDestroy, OnInit} from '@angular/core';
import {ConfirmationService, MenuItem} from "primeng/api";
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../shared/user.service";
import {UserDTO} from "../shared/user-dto.model";
import {CommunicationService} from "../../../shared/communication/communication.service";
import {Subscription} from "rxjs";

@Component({
    selector: 'app-user-list',
    templateUrl: './user-list.component.html',
    styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit, OnDestroy {

    contextMenuItems: MenuItem[];
    loading: boolean;
    users: UserDTO[];
    selectedUser: UserDTO;
    private communicationSubscription: Subscription;

    constructor(
        private userService: UserService,
        private confirmationService: ConfirmationService,
        private communicationService: CommunicationService,
        private router: Router,
        private route: ActivatedRoute
    ) {

        this.contextMenuItems = [
            {label: 'Редактировать', icon: 'pi pi-pencil', command: this.onEditUserClick},
            {label: 'Обновить', icon: 'pi pi-refresh', command: this.onReloadUsersClick},
            {label: 'Удалить', icon: 'pi pi-times', command: this.onDeleteUserClick}
        ];

        this.communicationSubscription = this.communicationService.reloadUserList$.subscribe(() => this.loadUsers());
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
        this.router.navigate([{outlets: {user: [this.selectedUser.id, 'edit']}}], {relativeTo: this.route});
    };

    private onDeleteUserClick = (event) => {
        this.confirmationService.confirm({
            message: 'Вы действительно хотите удалить этого пользователя?',
            accept: () => {
                this.userService.delete(this.selectedUser.id).subscribe(() => {
                    this.loadUsers();
                });
            }
        });
    };

    ngOnDestroy(): void {
        this.communicationSubscription.unsubscribe();
    }

    private onReloadUsersClick = (event) => {   // preserves the context(this)
        this.loadUsers();
    };
}
