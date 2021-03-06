import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SchemeComponent} from './scheme/scheme.component';
import {SchemeUpdateComponent} from './scheme-update/scheme-update.component';
import {AccordionModule} from 'primeng/accordion';
import {MenuModule} from 'primeng/menu';
import {ContextMenuModule} from 'primeng/contextmenu';
import {FileModule} from "../file/file.module";
import {SharedModule} from "../../shared/shared.module";
import {DialogModule} from 'primeng/dialog';
import {ReactiveFormsModule} from "@angular/forms";
import {InputTextareaModule} from 'primeng/inputtextarea';
import {InputTextModule} from 'primeng/inputtext';
import {ButtonModule} from 'primeng/button';
import {RadioButtonModule} from 'primeng/radiobutton';
import {SchemeListComponent} from './scheme-list/scheme-list.component';
import {CheckboxModule, FieldsetModule, PanelModule, SplitButtonModule} from "primeng";


@NgModule({
  declarations: [SchemeComponent, SchemeUpdateComponent, SchemeListComponent],
    imports: [
        CommonModule,
        FileModule,
        AccordionModule,
        MenuModule,
        ContextMenuModule,
        DialogModule,
        ReactiveFormsModule,
        InputTextareaModule,
        InputTextModule,
        ButtonModule,
        RadioButtonModule,
        SharedModule,
        PanelModule,
        SplitButtonModule,
        CheckboxModule,
        FieldsetModule
    ]
})
export class SchemeModule {
}
