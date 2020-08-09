import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthenticationService} from "../../shared/authentication/authentication.service";
import {ActivatedRoute, Event, NavigationEnd, NavigationStart, Router} from "@angular/router";
import {FileService} from "../../entities/file/shared/file.service";
import {BrowserStorageService} from "../../shared/browser-storage/browser-storage.service";
import {CommunicationService} from "../../shared/communication/communication.service";
import {finalize} from "rxjs/operators";
import {Subscription} from "rxjs";
import {Role} from "../../shared/authentication/role-enum";
import {MessageService} from "primeng";
import {AlertService} from "../../shared/alert/alert.service";

@Component({
    selector: 'app-toolbar',
    templateUrl: './toolbar.component.html',
    styleUrls: ['./toolbar.component.css']
})
export class ToolbarComponent implements OnInit, OnDestroy {

    private subscription: Subscription;
    private currentRoute = '/';

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private authenticationService: AuthenticationService,
        private fileService: FileService,
        // private alertService: AlertService,
        private alertService: AlertService,
        private browserStorageService: BrowserStorageService,
        private communicationService: CommunicationService
    ) {
    }

    ngOnInit() {
        this.subscription = this.router.events.subscribe((event: Event) => {
            if (event instanceof NavigationEnd) {
                this.currentRoute = event.url;
            }
        });
    }

    onLogoutClick(event) {
        this.authenticationService.logout().subscribe(() => this.router.navigate(['/login']));
    }

    get user() {
        return this.authenticationService.currentUserValue.login;
    }

    onUpdateCrlsClick = () => {
        this.communicationService.startLoading();
        this.fileService.updateCrls().pipe(
            finalize(() => {
                this.communicationService.stopLoading();
            })
        ).subscribe(result => {
            this.alertService.success(result, {
                callback: this.onCloseUpdatedCrlsToast,
                scope: this
            }, true, false, 'message', 'Обновление СОС завершено');
            // this.messageService.add({key: 'message', sticky: true, severity: 'info', summary: 'Обновление СОС завершено', detail: result});
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

    onAddScheme() {
        this.router.navigate(['schemes', {outlets: {scheme: ['new']}}], {relativeTo: this.route.firstChild});
    }

    onUsersClick() {
        this.router.navigate(['admin', 'user'], {relativeTo: this.route});
    }

    onAddUser() {
        this.router.navigate(['admin', 'user', {outlets: {user: ['new']}}], {relativeTo: this.route});
    }

    onCertificatesClick() {
        this.router.navigate(['/']);
    }

    isMainMenu() {
        return this.currentRoute === '/' || this.currentRoute.indexOf('certificates') !== -1;
    }

    isUserMenu() {
        return this.currentRoute.indexOf('user') !== -1;
    }

    isCerCrlViewMenu() {
        return this.currentRoute.search(/(certificate|crl)\/\d+\/view/) !== -1;
    }

    isAdmin() {
        return this.authenticationService.hasAuthority(Role.ADMIN);
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    onReloadUsersClick() {
        this.communicationService.reloadUserList();
    }

    onReloadSchemes() {
        this.communicationService.reloadSchemeList();
    }

    onCloseUpdatedCrlsToast() {
        this.onReloadSchemes();
    }
}
