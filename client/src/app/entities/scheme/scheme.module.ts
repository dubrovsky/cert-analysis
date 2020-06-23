import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SchemeComponent} from './scheme/scheme.component';
import {SchemeListComponent} from './scheme-list/scheme-list.component';
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
import {SchemeList00Component} from './scheme-list00/scheme-list00.component';
import {PanelModule, SplitButtonModule} from "primeng";


@NgModule({
  declarations: [SchemeComponent, SchemeListComponent, SchemeUpdateComponent, SchemeList00Component],
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
    SplitButtonModule
  ]
})
export class SchemeModule {
}
