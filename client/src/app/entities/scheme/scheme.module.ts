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


@NgModule({
    declarations: [SchemeComponent, SchemeListComponent, SchemeUpdateComponent],
    imports: [
        CommonModule,
        FileModule,
        AccordionModule,
        MenuModule,
        ContextMenuModule,
        SharedModule
    ]
})
export class SchemeModule {
}
