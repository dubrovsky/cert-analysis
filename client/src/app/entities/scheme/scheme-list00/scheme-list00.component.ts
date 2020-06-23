import {AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ConfirmationService, MenuItem} from "primeng/api";
import {Scheme} from "../../../shared/model/scheme.model";
import {filter, finalize, take} from "rxjs/operators";
import {SchemeService} from "../shared/scheme.service";
import {CommunicationService} from "../../../shared/communication/communication.service";
import {ActivatedRoute, ActivationEnd, Router, UrlSegmentGroup} from "@angular/router";
import {BrowserStorageService} from "../../../shared/browser-storage/browser-storage.service";
import {FileListComponent} from "../../file/file-list/file-list.component";
import {Subscription} from "rxjs";
import {Menu, Panel} from "primeng";
import {AccordionTab} from "primeng/accordion";

@Component({
    selector: 'app-scheme-list00',
    templateUrl: './scheme-list00.component.html',
    styleUrls: ['./scheme-list00.component.css']
})
export class SchemeList00Component implements OnInit, OnDestroy, AfterViewInit {

    menuItems: MenuItem[];
    schemes: Scheme[];
    schemeId: number;
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
                items: [{
                    label: 'Редактировать',
                    icon: 'pi pi-pencil',
                    command: this.onEditSchemeClick
                }, {
                    label: 'Удалить',
                    icon: 'pi pi-minus',
                    command: this.onDeleteSchemeClick
                }]
            }
        ];
    }

    onMenuToggle(event: MouseEvent, menu: Menu, schemaId: number) {
        this.schemeId = schemaId;
        menu.toggle(event);
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
        this.router.navigate([{outlets: {file: ['scheme', this.schemeId, 'file', 'new']}}], {relativeTo: this.route.parent});
    }

    private onReloadFilesClick = (event) => {
        this.reloadFileList(this.schemeId);
    }

    private onEditSchemeClick = (event) => {
        this.router.navigate([{outlets: {scheme: ['scheme', this.schemeId, 'edit']}}], {relativeTo: this.route.parent});
    }

    private onDeleteSchemeClick = (event) => {
        this.confirmationService.confirm({
            message: 'Вы действительно хотите удалить эту систему?',
            accept: () => {
                this.schemeService.delete(this.schemeId).subscribe(() => {
                    this.loadSchemes();
                });
            }
        });
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

    private openClosePanelByUrl(fileLists: QueryList<FileListComponent>) {    // url in browser window
        fileLists.forEach(fileList => {
            fileList.panel.collapsed = fileList.schemeId != this.schemeId;
        })
    }

    private openClosePanelsByStorage(fileLists: QueryList<FileListComponent>) {    // opened tabs are saved in local storage
        const openedTabs = this.browserStorageService.get('openedTabs');
        let savedSchemeIds = [];
        if (openedTabs) {
            savedSchemeIds = JSON.parse(openedTabs);
        }

        fileLists.forEach(fileList => {
            fileList.panel.collapsed = savedSchemeIds.indexOf(fileList.schemeId) == -1;
        });
    }

    trackBySchemeId(index: number, scheme: Scheme): number {
        return scheme.id;
    }
}
