import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AlertComponent} from "./alert/alert.component";
import {MessageService} from "primeng/api";
import {MessagesModule} from 'primeng/messages';
import {MessageModule} from 'primeng/message';
import {DialogComponent} from './dialog/dialog.component';
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import {ConfirmationService} from 'primeng/api';
import {LoginComponent} from './login/login.component';
import {InputTextModule} from 'primeng/inputtext';
import {ReactiveFormsModule} from "@angular/forms";
import {PasswordModule} from 'primeng/password';
import {CardModule} from 'primeng/card';

@NgModule({
    declarations: [AlertComponent, DialogComponent, LoginComponent],
    imports: [
        CommonModule,
        MessagesModule,
        MessageModule,
        ConfirmDialogModule,
        ReactiveFormsModule,
        InputTextModule,
        PasswordModule,
        CardModule
    ],
    providers: [
        MessageService,
        ConfirmationService
    ],
    exports: [
        AlertComponent,
        DialogComponent
    ]
})
export class SharedModule {
}
