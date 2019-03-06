import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "../../shared/authentication/authentication.service";
import {Router} from "@angular/router";
import {FileService} from "../../entities/file/shared/file.service";
import {AlertService} from "../../shared/alert/alert.service";
import {BrowserStorageService} from "../../shared/browser-storage/browser-storage.service";
import {CommunicationService} from "../../shared/communication/communication.service";
import {finalize} from "rxjs/operators";

@Component({
    selector: 'app-toolbar',
    templateUrl: './toolbar.component.html',
    styleUrls: ['./toolbar.component.css']
})
export class ToolbarComponent implements OnInit {

    constructor(
        private router: Router,
        private authenticationService: AuthenticationService,
        private fileService: FileService,
        private alertService: AlertService,
        private browserStorageService: BrowserStorageService,
        private communicationService: CommunicationService
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

    onUpdateCrlsClick = (event) => {
        this.communicationService.startLoading();
        this.fileService.updateCrls().pipe(
            finalize(() => {
                this.communicationService.stopLoading();
            })
        ).subscribe(result => {
            this.alertService.success(`Обновлено - ${result}`);
            if (result > 0) {
                this.reloadOpenTabs();
            }
        });
    };

    private reloadOpenTabs() {
        const openedTabs = this.browserStorageService.get('openedTabs');
        let tabs = [];
        if (openedTabs) {
            tabs = JSON.parse(openedTabs);
            tabs.forEach(schemeId => {
                this.communicationService.reloadFileList(schemeId);
            })
        }
    }
}
