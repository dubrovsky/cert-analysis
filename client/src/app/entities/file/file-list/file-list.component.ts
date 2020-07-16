import {Component, Input, OnInit} from '@angular/core';
import {FileService} from "../shared/file.service";
import {ConfirmationService, MenuItem, SortEvent} from "primeng/api";
import {CertificateDTO} from "../shared/certificate-dto.model";
import {ActivatedRoute, Router} from "@angular/router";
import {CertificateState} from "../shared/certificate-state.enum";
import {CommunicationService} from "../../../shared/communication/communication.service";
import {Panel} from "primeng";
import {CerCrl} from "../shared/cer-crl.enum";

@Component({
    selector: 'app-file-list',
    templateUrl: './file-list.component.html',
    styleUrls: ['./file-list.component.css'],
})
export class FileListComponent implements OnInit {

    @Input() schemeId: number;
    @Input() certificates: CertificateDTO[];
    @Input() panel: Panel;
    contextMenuItems: MenuItem[];
    selectedCertificate: CertificateDTO;
    loading: boolean;
    // @Output() contextMenuEvent = new EventEmitter();
    // @Output() selectedTableRowEvent = new EventEmitter();
    // @ViewChild(ContextMenu) contextMenu: ContextMenu;
    private certificateState = CertificateState;
    private sortedField = 'name';
    private sortedOrder = 1;

    constructor(
        private fileService: FileService,
        private confirmationService: ConfirmationService,
        private communicationService: CommunicationService,
        private router: Router,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.initContextMenuItems();
    }

    initContextMenuItems() {
        this.contextMenuItems = [
            {label: 'Просмотреть', icon: 'pi pi-eye', command: this.onViewFileClick},
            {label: 'Редактировать', icon: 'pi pi-pencil', command: this.onEditFileClick},
            {label: 'Заменить', icon: 'pi pi-clone', command: this.onReplaceFileClick},
            {label: 'Удалить', icon: 'pi pi-times', command: this.onDeleteCertificateClick}
        ];
    }

    loadFiles() {
        this.loading = true;
        this.fileService.findAllBySchemeId(this.schemeId, this.sortedField, this.sortedOrder).subscribe(
            certificates => {
                this.certificates = certificates;
                this.loading = false;
            }
        );
    }

    sortCertificates(event: SortEvent) { // !!! [lazy]="true"
        this.sortedField = event.field;
        this.sortedOrder = event.order;
        this.loadFiles();
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
    }

    private onViewFileClick = (event) => {
        if (this.selectedCertificate.cerCrl == CerCrl.CER) {
            this.router.navigate([{outlets: {file: [this.schemeId, 'certificate', this.selectedCertificate.id, 'view']}}], {relativeTo: this.route.parent});
        } else if (this.selectedCertificate.cerCrl == CerCrl.CRL) {
            this.router.navigate([{outlets: {file: [this.schemeId, 'crl', this.selectedCertificate.id, 'view']}}], {relativeTo: this.route.parent});
        }
    }

    private onEditFileClick = (event) => {
        this.communicationService.setFileListComponent(this);
        this.router.navigate([{outlets: {file: [this.schemeId, 'file', this.selectedCertificate.fileId, 'edit']}}], {relativeTo: this.route.parent});
    }

    private onReplaceFileClick = (event) => {
        this.communicationService.setFileListComponent(this);
        this.router.navigate([{outlets: {file: [this.schemeId, 'file', this.selectedCertificate.fileId, 'replace']}}], {relativeTo: this.route.parent});
    }

    getTableRowClass(certificateDTO: CertificateDTO) {
        switch (certificateDTO.state) {
            case this.certificateState.EXPIRED :
            case this.certificateState.NOT_STARTED :
            case this.certificateState.REVOKED :
                return 'invalid';
            case this.certificateState.IN_7_DAYS_INACTIVE :
                return 'warning';
            default:
                return '';
        }
    }

    onDownloadFileClick(certificate) {
        this.fileService.download(certificate.fileId).subscribe(response => {
            const url = window.URL.createObjectURL(response.body);
            const a = document.createElement("a");
            document.body.appendChild(a);
            a.setAttribute('style', 'display: none');
            a.href = url;
            const contentDisposition = response.headers.get('Content-Disposition');
            a.download = decodeURIComponent(contentDisposition.split(';')[1].split('filename')[1].split('=')[1].trim());
            // start download
            a.click();
            window.URL.revokeObjectURL(url);
            a.remove();
        });
    }

    rowTrackBy(index: number, item: CertificateDTO) {
        return item.uniqueId;
    }
}
