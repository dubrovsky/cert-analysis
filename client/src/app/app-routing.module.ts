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
import {SchemeUpdateComponent} from "./entities/scheme/scheme-update/scheme-update.component";
import {SchemeResolverService} from "./entities/scheme/shared/scheme-resolver.service";
import {Role} from "./shared/authentication/role-enum";
import {SchemeList00Component} from "./entities/scheme/scheme-list00/scheme-list00.component";

const routes: Routes = [
    {
        path: '',
        redirectTo: 'certificates',
        pathMatch: 'full'/*,
        canActivate: [AuthenticationGuard]*/
    },
    {
        path: 'certificates',
        component: SchemeComponent,
        canActivate: [AuthenticationGuard],
        children: [{
            path: '',
            component: SchemeList00Component,
            // component: SchemeListComponent,
            canActivateChild: [AuthenticationGuard]
        }, {
            path: 'scheme/new',
            component: SchemeUpdateComponent,
            outlet: 'scheme',
            resolve: {
                scheme: SchemeResolverService
            }
        }, {
            path: 'scheme/:id/edit',
            component: SchemeUpdateComponent,
            outlet: 'scheme',
            canActivateChild: [AuthenticationGuard],
            resolve: {
                scheme: SchemeResolverService
            }
        }, {
            path: 'scheme/:schemeId/file',
            component: FileComponent,
            outlet: 'file',
            canActivateChild: [AuthenticationGuard],
            children: [{
                path: ':id/edit',
                component: FileUpdateComponent,
                resolve: {
                    fileDTO: FileResolverService
                },
                data: {
                    formType: FileFormType.UPDATE
                }
            }, {
                path: ':id/replace',
                component: FileUpdateComponent,
                resolve: {
                    fileDTO: FileResolverService
                },
                data: {
                    formType: FileFormType.REPLACE
                }
            }, {
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
        path: 'admin',
        loadChildren: () => import('./entities/user/user.module').then(m => m.UserModule),
        data: {
            authorities: [Role.ADMIN]
        },
        canLoad: [AuthenticationGuard]
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
