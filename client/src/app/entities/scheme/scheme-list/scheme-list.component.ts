import {AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {SchemeService} from "../shared/scheme.service";
import {Scheme} from "../../../shared/model/scheme.model";
import {ConfirmationService, MenuItem} from "primeng/api";
import {FileListComponent} from "../../file/file-list/file-list.component";
import {ContextMenu} from "primeng/contextmenu";
import {ActivatedRoute, ActivationEnd, Router, UrlSegmentGroup} from "@angular/router";
import {CommunicationService} from "../../../shared/communication/communication.service";
import {Subscription} from "rxjs";
import {filter, finalize, take} from "rxjs/operators";
import {AccordionTab} from "primeng/accordion";
import {BrowserStorageService} from "../../../shared/browser-storage/browser-storage.service";

@Component({
    selector: 'app-scheme-list',
    templateUrl: './scheme-list.component.html',
    styleUrls: ['./scheme-list.component.css']
})
export class SchemeListComponent implements OnInit, OnDestroy, AfterViewInit/*, AfterViewChecked*/ {

    schemes: Scheme[];
    menuItems: MenuItem[];
    schemeId: number;
    @ViewChildren(FileListComponent) fileListComponents: QueryList<FileListComponent>;
    @ViewChildren(AccordionTab) accordionTabs: QueryList<AccordionTab>;
    // private certificateContextMenu: ContextMenu;
    private fileListWithSelectedRow: FileListComponent;
    private schemeContextMenu: ContextMenu;
    private reloadFileListSubscription: Subscription;
    private reloadSchemeListSubscription: Subscription;
    private routeSubscription: Subscription;
    private accordionTabsSubscription: Subscription;

    constructor(
        private schemeService: SchemeService,
        private router: Router,
        private route: ActivatedRoute,
        private communicationService: CommunicationService,
        private browserStorageService: BrowserStorageService,
        private confirmationService: ConfirmationService,
        private changeDetectorRef: ChangeDetectorRef
    ) {
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

        this.reloadFileListSubscription = this.communicationService.reloadFileList$.subscribe(schemeId => this.reloadFileList(schemeId));
        this.reloadSchemeListSubscription = this.communicationService.reloadSchemeList$.subscribe(() => this.loadSchemes());
        this.routeSubscription = this.router.events.pipe(
            filter(evt => evt instanceof ActivationEnd),
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
        });
    }

    ngOnInit() {
        // this.browserStorageService.clear();
        this.loadSchemes();
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

    private onAddFileClick = (event) => {   // preserves the context(this)
        this.router.navigate([{outlets: {file: ['scheme', this.schemeId, 'file', 'new']}}], {relativeTo: this.route.parent});
    };

    private onReloadFilesClick = (event) => {   // preserves the context(this)
        this.reloadFileList(this.schemeId);
    };

    onSchemeContextMenuClick(schemeId: number, schemeContextMenu) {
        this.schemeId = schemeId;
        if (this.schemeContextMenu && this.schemeContextMenu != schemeContextMenu) {
            this.schemeContextMenu.hide();
        }
        this.schemeContextMenu = schemeContextMenu;
        if (this.fileListWithSelectedRow) {
            // this.fileListWithSelectedRow.contextMenu.hide();
            this.fileListWithSelectedRow.selectedCertificate = null;
        }
    }

    reloadFileList(schemeId: number) {
        this.fileListComponents.forEach(fileListComponent => {
            if (fileListComponent.schemeId === schemeId) {
                fileListComponent.loadFiles();
                return;
            }
        })
    }

    /*onCertificateContextMenuClick(fileListComponent) {
        if (this.fileListWithSelectedRow && this.fileListWithSelectedRow != fileListComponent) {
            this.fileListWithSelectedRow.contextMenu.hide();
            this.onSelectedTableRowEvent(fileListComponent);
        }
        this.fileListWithSelectedRow = fileListComponent;
        if (this.schemeContextMenu) {
            this.schemeContextMenu.hide();
        }
    }


    onSelectedTableRowEvent(fileListComponent) {
        if (this.fileListWithSelectedRow && this.fileListWithSelectedRow != fileListComponent) {
            this.fileListWithSelectedRow.selectedCertificate = null;
        }
        this.fileListWithSelectedRow = fileListComponent;
    }*/

    ngOnDestroy(): void {
        this.reloadFileListSubscription.unsubscribe();
        this.reloadSchemeListSubscription.unsubscribe();
        this.routeSubscription.unsubscribe();
        this.accordionTabsSubscription.unsubscribe();
    }

    ngAfterViewInit(): void {
        this.accordionTabsSubscription = this.accordionTabs.changes.subscribe((accordionTabs: QueryList<AccordionTab>) => {
            if (this.schemeId) {
                this.openTabByUrl(accordionTabs);
            }
            if (this.schemes) {
                this.openTabsByStorage(accordionTabs);
            }
            this.changeDetectorRef.detectChanges();
        });
    }

    private openTabByUrl(accordionTabs: QueryList<AccordionTab>) {    // url in browser window
        let index = undefined;
        if (this.schemes) {
            this.schemes.find((scheme, ind) => {
                if (scheme.id === this.schemeId) {
                    index = ind;
                    return true;
                }
            });

            if (index != undefined) {
                const tab = accordionTabs.toArray()[index];
                if (tab && !tab.selected) {
                    tab.toggle(event);
                }
            } else {
                this.schemeId = undefined;
            }
        }
    }

    private openTabsByStorage(accordionTabs: QueryList<AccordionTab>) {    // opened tabs are saved in local storage
        const openedTabs = this.browserStorageService.get('openedTabs');
        let tabs = [];
        if (openedTabs) {
            tabs = JSON.parse(openedTabs);
        }
        this.schemes.map((scheme, index) => {
            if (tabs.indexOf(scheme.id) != -1) {
                return index;
            }
        }).forEach(index => {
            const tab = accordionTabs.toArray()[index];
            if (tab && !tab.selected) {
                tab.toggle(event);
            }
        });
    }

    onTabOpen(event: any) {
        const openedTabs = this.browserStorageService.get('openedTabs');
        let tabs = [];
        if (openedTabs) {
            tabs = JSON.parse(openedTabs);
        }
        if (tabs.indexOf(this.schemes[event.index].id) == -1) {
            tabs.push(this.schemes[event.index].id);
        }
        this.browserStorageService.set('openedTabs', JSON.stringify(tabs));
    }

    onTabClose(event: any) {
        const openedTabs = this.browserStorageService.get('openedTabs');
        let tabs = [];
        if (openedTabs) {
            tabs = JSON.parse(openedTabs);
        }
        tabs.splice(tabs.indexOf(this.schemes[event.index].id), 1);
        this.browserStorageService.set('openedTabs', JSON.stringify(tabs));
    }

    private onEditSchemeClick = (event) => {   // preserves the context(this)
        this.router.navigate([{outlets: {scheme: ['scheme', this.schemeId, 'edit']}}], {relativeTo: this.route.parent});
    };

    private onDeleteSchemeClick = (event) => {   // preserves the context(this)
        this.confirmationService.confirm({
            message: 'Вы действительно хотите удалить эту систему?',
            accept: () => {
                this.schemeService.delete(this.schemeId).subscribe(() => {
                    // this.ngOnInit();
                    this.loadSchemes();
                    // this.router.navigate(['/']);
                });
            }
        });
    };
}
