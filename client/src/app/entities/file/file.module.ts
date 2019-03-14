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

@NgModule({
    declarations: [FileComponent, FileListComponent, FileUpdateComponent],
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
        SharedModule
    ],
    exports: [FileListComponent, FileUpdateComponent]
})
export class FileModule {
}
