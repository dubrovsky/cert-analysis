import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FileComponent} from './file/file.component';
import {FileListComponent} from './file-list/file-list.component';
import {FileUpdateComponent} from './file-update/file-update.component';
import {TableModule} from "primeng/table";
import {RouterModule} from "@angular/router";
import {DialogModule} from 'primeng/dialog';
import {FileUploadModule} from 'primeng/fileupload';
import {InputTextareaModule} from 'primeng/inputtextarea';
import {ReactiveFormsModule} from "@angular/forms";
import {ContextMenuModule} from 'primeng/contextmenu';
import {SharedModule} from "../../shared/shared.module";
import {CheckboxModule} from 'primeng/checkbox';
import {MessageModule} from 'primeng/message';
import {TooltipModule} from 'primeng/tooltip';
import {FieldsetModule} from 'primeng/fieldset';
import {CertificateDetailsComponent} from "./certificate-details/certificate-details.component";
import { CrlDetailsComponent } from './crl-details/crl-details.component';
import { CertificateCrlViewComponent } from './certificate-crl-view/certificate-crl-view.component';
import {DataViewModule, MenuModule} from "primeng";

@NgModule({
    declarations: [FileComponent, FileListComponent, FileUpdateComponent, CertificateDetailsComponent, CrlDetailsComponent, CertificateCrlViewComponent],
    imports: [
        CommonModule,
        RouterModule,
        TableModule,
        DialogModule,
        FileUploadModule,
        InputTextareaModule,
        ReactiveFormsModule,
        ContextMenuModule,
        CheckboxModule,
        MessageModule,
        TooltipModule,
        FieldsetModule,
        SharedModule,
        DataViewModule,
        MenuModule
    ],
    exports: [FileListComponent, FileUpdateComponent]
})
export class FileModule {
}
