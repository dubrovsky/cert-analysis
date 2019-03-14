import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {UserComponent} from './user/user.component';
import {UserListComponent} from './user-list/user-list.component';
import {UserUpdateComponent} from './user-update/user-update.component';
import {UserRoutingModule} from "./user-routing.module";
import {TableModule} from "primeng/table";
import {ContextMenuModule} from 'primeng/contextmenu';
import {SharedModule} from "../../shared/shared.module";
import {MessageModule} from "primeng/message";
import {ReactiveFormsModule} from "@angular/forms";
import {DialogModule} from 'primeng/dialog';
import {ButtonModule} from 'primeng/button';

@NgModule({
    declarations: [UserComponent, UserListComponent, UserUpdateComponent],
    imports: [
        CommonModule,
        ButtonModule,
        TableModule,
        SharedModule,
        ContextMenuModule,
        MessageModule,
        ReactiveFormsModule,
        DialogModule,
        UserRoutingModule
    ]
})
export class UserModule {
}
