import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {SchemeComponent} from "./entities/scheme/scheme/scheme.component";
import {SchemeListComponent} from "./entities/scheme/scheme-list/scheme-list.component";
import {FileComponent} from "./entities/file/file/file.component";
import {FileUpdateComponent} from "./entities/file/file-update/file-update.component";
import {FileResolverService} from "./entities/file/shared/file-resolver.service";
import {FileFormType} from "./entities/file/shared/file-form-type.enum";
import {AuthenticationGuard} from "./shared/authentication/authentication.guard";
import {LoginComponent} from "./shared/login/login.component";

const routes: Routes = [
    {
        path: '',
        redirectTo: 'certificates',
        pathMatch: 'full',
        canActivate: [AuthenticationGuard]
    },
    {
        path: 'certificates',
        component: SchemeComponent,
        canActivate: [AuthenticationGuard],
        children: [{
            path: '',
            component: SchemeListComponent
        },{
            path: 'scheme/:schemeId/file',
            component: FileComponent,
            outlet: 'file',
            children: [{
                path: ':id/edit',
                component: FileUpdateComponent,
                resolve: {
                    fileDTO: FileResolverService
                },
                data: {
                    formType: FileFormType.UPDATE
                }
            },{
                path: ':id/replace',
                component: FileUpdateComponent,
                resolve: {
                    fileDTO: FileResolverService
                },
                data: {
                    formType: FileFormType.REPLACE
                }
            },{
                path: 'new',
                component: FileUpdateComponent,
                resolve: {
                    fileDTO: FileResolverService
                },
                data: {
                    formType: FileFormType.CREATE
                }
            }]
        }]
    },
    {
        path: 'login',
        component: LoginComponent
    },
    {path: '**', redirectTo: ''}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
