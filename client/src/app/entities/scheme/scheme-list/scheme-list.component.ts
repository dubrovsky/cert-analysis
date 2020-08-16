import {AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ConfirmationService, MenuItem} from "primeng/api";
import {Scheme} from "../../../shared/model/scheme.model";
import {finalize} from "rxjs/operators";
import {SchemeService} from "../shared/scheme.service";
import {CommunicationService} from "../../../shared/communication/communication.service";
import {ActivatedRoute, Router} from "@angular/router";
import {BrowserStorageService} from "../../../shared/browser-storage/browser-storage.service";
import {FileListComponent} from "../../file/file-list/file-list.component";
import {Subscription} from "rxjs";
import {Menu} from "primeng";

@Component({
    selector: 'app-scheme-list',
    templateUrl: './scheme-list.component.html',
    styleUrls: ['./scheme-list.component.css']
})
export class SchemeListComponent implements OnInit, OnDestroy, AfterViewInit {

    menuItems: MenuItem[];
    schemes: Scheme[];
    schemeId: number;
    schemeName: string;
    @ViewChildren(FileListComponent) fileLists: QueryList<FileListComponent>;
    private reloadFileListSubscription: Subscription;
    private reloadSchemeListSubscription: Subscription;
    // private routeSubscription: Subscription;
    private panelsSubscription: Subscription;

    constructor(
        private schemeService: SchemeService,
        private router: Router,
        private route: ActivatedRoute,
        private communicationService: CommunicationService,
        private browserStorageService: BrowserStorageService,
        private confirmationService: ConfirmationService,
        private changeDetectorRef: ChangeDetectorRef
    ) {
    }

    ngOnInit(): void {
        this.initMenuItems();

        this.reloadFileListSubscription = this.communicationService.reloadFileList$.subscribe(schemeId => this.reloadFileList(schemeId));
        this.reloadSchemeListSubscription = this.communicationService.reloadSchemeList$.subscribe(() => this.loadSchemes());
        /*this.routeSubscription = this.router.events.pipe(
            filter(event => event instanceof ActivationEnd),
            take(1)
        ).subscribe((event: ActivationEnd) => {
            let fileSegment = (event.snapshot['_urlSegment'] as UrlSegmentGroup).children['file'];
            if (fileSegment) {
                fileSegment = (fileSegment as UrlSegmentGroup);
                for (let i = 0; i < fileSegment.segments.length; i++) {
                    if (fileSegment.segments[i].path === 'scheme') {
                        this.schemeId = +fileSegment.segments[i + 1].path;
                        break;
                    }
                }
            }
        });*/

        this.loadSchemes();
    }

    ngAfterViewInit(): void {
        this.panelsSubscription = this.fileLists.changes.subscribe((fileLists: QueryList<FileListComponent>) => {
            this.openClosePanels(fileLists);
        });
    }

    private openClosePanels(fileLists: QueryList<FileListComponent>): void {
        /*if (this.schemeId) {
            this.openClosePanelByUrl(fileLists);
        }*/
        if (this.schemes) {
            this.openClosePanelsByStorage(fileLists);
        }
        this.changeDetectorRef.detectChanges(); // !!!
    }

    ngOnDestroy(): void {
        this.reloadFileListSubscription.unsubscribe();
        this.reloadSchemeListSubscription.unsubscribe();
        // this.routeSubscription.unsubscribe();
        this.panelsSubscription.unsubscribe();
    }

    private initMenuItems() {
        this.menuItems = [
            {
                label: 'Сертификаты',
                icon: 'pi pi-file',
                items: [{
                    label: 'Добавить',
                    icon: 'pi pi-plus',
                    command: this.onAddFileClick
                }, {
                    label: 'Обновить',
                    icon: 'pi pi-refresh',
                    command: this.onReloadFilesClick
                }]
            },
            {
                label: 'Система',
                icon: 'pi pi-th-large',
                id: 'scheme',
                items: [{
                    label: 'Редактировать',
                    icon: 'pi pi-pencil',
                    command: this.onEditSchemeClick
                }, {
                    label: 'Удалить',
                    icon: 'pi pi-minus',
                    command: this.onDeleteSchemeClick
                }, {
                    label: 'Вверх',
                    id: 'up',
                    icon: 'pi pi-arrow-up',
                    command: this.onMoveSchemeUp
                }, {
                    label: 'Вниз',
                    id: 'down',
                    icon: 'pi pi-arrow-down',
                    command: this.onMoveSchemeDown
                }]
            }
        ];
    }

    onMenuToggle(event: any, schemeId: number, schemeName: string, sort: number) {
        this.schemeId = schemeId;
        this.schemeName = schemeName;
        this.updateMenuItemsVisability(event.menu, sort);
        event.menu.toggle(event.event);
    }

    private updateMenuItemsVisability(menu: Menu, sort: number) {
        const schemeMenuItems = menu.model.find(item => item.id == 'scheme').items;
        const upMenuItem = schemeMenuItems.find(item => item.id == 'up');
        const downMenuItem = schemeMenuItems.find(item => item.id == 'down');
        upMenuItem.visible = true;
        downMenuItem.visible = true;

        if (this.schemes[0].sort == sort) { // first
            upMenuItem.visible = false;
        } else if (this.schemes[this.schemes.length - 1].sort == sort) { // last
            downMenuItem.visible = false;
        }
    }

    private loadSchemes() {
        this.communicationService.startLoading();
        this.schemeService.findAll().pipe(
            finalize(() => {
                this.communicationService.stopLoading();
            })
        ).subscribe(schemes => {
            this.schemes = schemes;
        });
    }

    private reloadFileList(schemeId: number) {
        this.fileLists.forEach(fileListComponent => {
            if (fileListComponent.schemeId === schemeId) {
                fileListComponent.loadFiles();
                return;
            }
        })
    }

    private onAddFileClick = (event) => {
        this.router.navigate([{outlets: {file: [this.schemeId, 'file', 'new']}}], {relativeTo: this.route.parent});
    }

    private onReloadFilesClick = (event) => {
        this.reloadFileList(this.schemeId);
    }

    private onEditSchemeClick = (event) => {
        this.router.navigate([{outlets: {scheme: [this.schemeId, 'edit']}}], {relativeTo: this.route.parent});
    }

    private onDeleteSchemeClick = (event) => {
        this.confirmationService.confirm({
            message: 'Вы действительно хотите удалить систему "' + this.schemeName + '"?',
            accept: () => {
                this.schemeService.delete(this.schemeId).subscribe(() => {
                    this.loadSchemes();
                });
            }
        });
    }

    private onMoveSchemeUp = (event) => {
        this.moveSchemeUpDown('UP');
    }

    private onMoveSchemeDown = (event) => {
        this.moveSchemeUpDown('DOWN');
    }

    onCollapsedChange(collapsed: boolean, schemeId: number) {
        if (collapsed) {
            this.panelClosed(schemeId);
        } else {
            this.panelOpened(schemeId);
        }
    }

    panelOpened(schemeId: number) {
        const openedTabs = this.browserStorageService.get('openedTabs');
        let savedSchemeIds = [];
        if (openedTabs) {
            savedSchemeIds = JSON.parse(openedTabs);
        }
        if (savedSchemeIds.indexOf(schemeId) == -1) {
            savedSchemeIds.push(schemeId);
        }
        this.browserStorageService.set('openedTabs', JSON.stringify(savedSchemeIds));
    }

    panelClosed(schemeId: number) {
        const openedTabs = this.browserStorageService.get('openedTabs');
        let savedSchemeIds = [];
        if (openedTabs) {
            savedSchemeIds = JSON.parse(openedTabs);
        }
        savedSchemeIds.splice(savedSchemeIds.indexOf(schemeId), 1);
        this.browserStorageService.set('openedTabs', JSON.stringify(savedSchemeIds));
    }

    /*private openClosePanelByUrl(fileLists: QueryList<FileListComponent>) {    // url in browser window
        fileLists.forEach(fileList => {
            fileList.panel.collapsed = fileList.schemeId != this.schemeId;
        })
    }*/

    private openClosePanelsByStorage(fileLists: QueryList<FileListComponent>) {    // opened tabs are saved in local storage
        const openedTabs = this.browserStorageService.get('openedTabs');
        let savedSchemeIds = [];
        if (openedTabs) {
            savedSchemeIds = JSON.parse(openedTabs);
        }

        /*fileLists.forEach(fileList => {
            fileList.panel.collapsed = savedSchemeIds.indexOf(fileList.schemeId) == -1;
        });*/
        fileLists.forEach(fileList => {
            fileList.collapsed = savedSchemeIds.indexOf(fileList.schemeId) == -1;
        });
    }

    trackBySchemeId(index: number, scheme: Scheme): number {
        return scheme.id;
    }

    private moveSchemeUpDown(direction: string) {
        this.communicationService.startLoading();
        this.schemeService.moveUpDown(this.schemeId, direction).pipe(
            finalize(() => {
                this.communicationService.stopLoading();
            })
        ).subscribe(schemes => {
            this.schemes = schemes;
        });
    }
}
