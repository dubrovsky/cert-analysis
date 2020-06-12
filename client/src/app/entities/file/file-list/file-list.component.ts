import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {FileService} from "../shared/file.service";
import {ConfirmationService, MenuItem} from "primeng/api";
import {CertificateDTO} from "../shared/certificate-dto.model";
import {ContextMenu} from "primeng/contextmenu";
import {ActivatedRoute, Router} from "@angular/router";
import {CertificateState} from "../shared/certificate-state.enum";
import {CommunicationService} from "../../../shared/communication/communication.service";

@Component({
  selector: 'app-file-list',
  templateUrl: './file-list.component.html',
  styleUrls: ['./file-list.component.css'],
})
export class FileListComponent implements OnInit {

  @Input() schemeId: number;
  // files: File[];
  @Input() certificates: CertificateDTO[];
  contextMenuItems: MenuItem[];
  selectedCertificate: CertificateDTO;
  loading: boolean;
  @Output() contextMenuEvent = new EventEmitter();
  @Output() selectedTableRowEvent = new EventEmitter();
  @ViewChild(ContextMenu) contextMenu: ContextMenu;
  certificateState = CertificateState;

  constructor(
    private fileService: FileService,
    private confirmationService: ConfirmationService,
    private communicationService: CommunicationService,
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
    // this.loadFiles();
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
    // this.contextMenuEvent.next(this.contextMenu);
    this.contextMenuEvent.next(this);
  }

  onRowSelect(event) {
    this.selectedTableRowEvent.next(this);
  }

  private onEditFileClick = (event) => {
    this.communicationService.setFileListComponent(this);
    this.router.navigate([{outlets: {file: ['scheme', this.schemeId, 'file', this.selectedCertificate.fileId, 'edit']}}], {relativeTo: this.route.parent});

  };

  private onReplaceFileClick = (event) => {
    this.communicationService.setFileListComponent(this);
    this.router.navigate([{outlets: {file: ['scheme', this.schemeId, 'file', this.selectedCertificate.fileId, 'replace']}}], {relativeTo: this.route.parent});
  };

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

}
