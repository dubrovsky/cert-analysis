import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {FileService} from "../shared/file.service";
import {ConfirmationService, MenuItem} from "primeng/api";
import {CertificateDTO} from "../shared/certificate-dto.model";
import {ContextMenu} from "primeng/contextmenu";
import {ActivatedRoute, Router} from "@angular/router";
import {CertificateState} from "../shared/certificate-state.enum";

@Component({
    selector: 'app-file-list',
    templateUrl: './file-list.component.html',
    styleUrls: ['./file-list.component.css'],
})
export class FileListComponent implements OnInit {

    @Input() schemeId: number;
    // files: File[];
    certificates: CertificateDTO[];
    contextMenuItems: MenuItem[];
    selectedCertificate: CertificateDTO;
    loading: boolean;
    @Output() contextMenuEvent = new EventEmitter();
    @ViewChild(ContextMenu) contextMenu: ContextMenu;
    certificateState = CertificateState;

    constructor(
        private fileService: FileService,
        private confirmationService: ConfirmationService,
        private router: Router,
        private route: ActivatedRoute
    ) {
        this.contextMenuItems = [
            // {label: 'Просмотреть', icon: 'pi pi-search'},
            {label: 'Редактировать', icon: 'pi pi-pencil', command: this.onEditFileClick},
            {label: 'Заменить', icon: 'pi pi-clone', command: this.onReplaceFileClick},
            {label: 'Удалить', icon: 'pi pi-times', command: this.onDeleteCertificateClick}
        ];
    }

    ngOnInit() {
        this.loadFiles();
    }

    loadFiles(schemeId?: number) {
        this.loading = true;
        this.fileService.findAllBySchemeId(schemeId ? schemeId : this.schemeId).subscribe(
            certificates => {
                this.certificates = certificates;
                this.loading = false;
            }
        );
    }

    private onDeleteCertificateClick = (event) => {
        this.confirmationService.confirm({
            message: 'Вы действительно хотите удалить этот сертификат?',
            accept: () => {
                this.fileService.delete(this.selectedCertificate.id, this.selectedCertificate.fileId).subscribe(() => {
                    this.loadFiles();
                });
            }
        });
    };

    onContextMenu(event) {
        this.contextMenuEvent.next(this.contextMenu);
    }

    private onEditFileClick = (event) => {
        this.router.navigate([{outlets: {file: ['scheme', this.schemeId, 'file', this.selectedCertificate.fileId, 'edit']}}], {relativeTo: this.route.parent});
    };

    private onReplaceFileClick = (event) => {
        this.router.navigate([{outlets: {file: ['scheme', this.schemeId, 'file', this.selectedCertificate.fileId, 'replace']}}], {relativeTo: this.route.parent});
    };

    getTableRowClass(certificateDTO: CertificateDTO) {
        switch (certificateDTO.state) {
            case this.certificateState.EXPIRED :
            case this.certificateState.NOT_START :
            case this.certificateState.REVOKED :
                return 'invalid';
            case this.certificateState.IN_7_DAYS_INACTIVE :
                return 'warning';
            default:
                return 'valid';
        }
    }
}
