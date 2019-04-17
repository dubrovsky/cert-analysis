import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {NotificationGroup} from "../../../shared/model/notification-group.model";
import {ActivatedRoute, Router} from "@angular/router";
import {CommunicationService} from "../../../shared/communication/communication.service";
import {NotificationGroupService} from "../../notification-group/shared/notification-group.service";
import {UserService} from "../shared/user.service";
import {forkJoin, Observable, Subscription} from "rxjs";
import {UserDTO} from "../shared/user-dto.model";
import {Role} from "../../../shared/model/role.model";
import {UserListComponent} from "../user-list/user-list.component";

@Component({
    selector: 'app-user-update',
    templateUrl: './user-update.component.html',
    styleUrls: ['./user-update.component.css']
})
export class UserUpdateComponent implements OnInit, OnDestroy {

    displayUserForm: boolean = false;
    userForm: FormGroup;
    notificationGroups: NotificationGroup[];
    roles: Role[];
    private routeSubscription: Subscription;
    private userListComponentSubscription: Subscription;
    private userListComponent: UserListComponent;

    constructor(
        private fb: FormBuilder,
        private userService: UserService,
        private router: Router,
        private route: ActivatedRoute,
        private communicationService: CommunicationService,
        private notificationGroupService: NotificationGroupService
    ) {

        this.userForm = this.fb.group({
            id: [''],
            login: ['',  Validators.compose([Validators.required, Validators.maxLength(24)])],
            // password: ['', Validators.compose([Validators.required, Validators.maxLength(24)])],
            firstname: ['', Validators.compose([Validators.required, Validators.maxLength(24)])],
            lastname: ['', Validators.compose([Validators.required, Validators.maxLength(24)])],
            surname: ['', Validators.maxLength(24)],
            email: ['', Validators.compose([Validators.required, Validators.maxLength(24), Validators.email])],
            phone: ['', Validators.maxLength(24)],
            enabled: [''],
            roleId: ['', Validators.required],
            notificationGroupIds: [[]]
        });

        this.userListComponentSubscription = this.communicationService.userListComponent$.subscribe(userListComponent => this.userListComponent = userListComponent);
    }

    ngOnInit() {
        this.routeSubscription = this.route.data.subscribe((data: { userDTO: UserDTO }) => {
            if(!data.userDTO.id) {
                this.userForm.addControl('password', new FormControl('', Validators.compose([Validators.required, Validators.maxLength(24)])));
            }
            this.userForm.patchValue(data.userDTO);
        });

        forkJoin(
            this.notificationGroupService.findAll(),
            this.userService.findAllRoles()
        ).subscribe(data => {
            const [notificationGroups, roles] = data;
            this.notificationGroups = notificationGroups;
            this.roles = roles;
            this.displayUserForm = true;
        });
    }

    onSubmitClick() {
        const user: UserDTO = this.userForm.value;
        if (user.id) {
            this.subscribeToSaveResponse(this.userService.update(user));
        } else {
            this.subscribeToSaveResponse(this.userService.create(user));
        }
    }

    private subscribeToSaveResponse(userObservable: Observable<UserDTO>) {
        userObservable.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
    }

    private onSaveSuccess() {
        this.communicationService.reloadUserList();
        this.onCancelClick();
    }

    private onSaveError() {
        this.onCancelClick();
    }

    onCancelClick() {
        this.userForm.reset();
        this.displayUserForm = false;
        if(this.userListComponent && this.userListComponent.selectedUser) {
            this.userListComponent.selectedUser = null;
        }
        this.router.navigate([{outlets: {user: null}}], {relativeTo: this.route.parent});
    }

    ngOnDestroy(): void {
        this.routeSubscription.unsubscribe();
        this.userListComponentSubscription.unsubscribe();
    }

}
