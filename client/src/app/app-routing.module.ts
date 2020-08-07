import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {SchemeComponent} from "./entities/scheme/scheme/scheme.component";
import {FileComponent} from "./entities/file/file/file.component";
import {FileUpdateComponent} from "./entities/file/file-update/file-update.component";
import {FileResolverService} from "./entities/file/shared/file-resolver.service";
import {FileFormType} from "./entities/file/shared/file-form-type.enum";
import {AuthenticationGuard} from "./shared/authentication/authentication.guard";
import {LoginComponent} from "./shared/login/login.component";
import {SchemeUpdateComponent} from "./entities/scheme/scheme-update/scheme-update.component";
import {SchemeResolverService} from "./entities/scheme/shared/scheme-resolver.service";
import {Role} from "./shared/authentication/role-enum";
import {SchemeListComponent} from "./entities/scheme/scheme-list/scheme-list.component";
import {CertificateDetailsResolverService} from "./entities/file/certificate-details/certificate-details-resolver.service";
import {CrlDetailsResolverService} from "./entities/file/crl-details/crl-details-resolver.service";
import {CertificateCrlViewComponent} from "./entities/file/certificate-crl-view/certificate-crl-view.component";
import {CerCrl} from "./entities/file/shared/cer-crl.enum";
import {CertificateDetailsComponent} from "./entities/file/certificate-details/certificate-details.component";
import {CrlDetailsComponent} from "./entities/file/crl-details/crl-details.component";

const routes: Routes = [
    {
        path: '',
        redirectTo: 'certificates/schemes',
        pathMatch: 'full'
    },
    {
        path: 'certificates',
        redirectTo: 'certificates/schemes',
        pathMatch: 'full'
    },
    {
        path: 'certificates',
        component: SchemeComponent,
        canActivate: [AuthenticationGuard],
        children: [
            {
                path: 'schemes',
                canActivateChild: [AuthenticationGuard],
                children: [
                    {
                        path: '',
                        component: SchemeListComponent
                    }, {
                        path: 'new',
                        component: SchemeUpdateComponent,
                        outlet: 'scheme',
                        resolve: {
                            scheme: SchemeResolverService
                        }
                    }, {
                        path: ':id/edit',
                        component: SchemeUpdateComponent,
                        outlet: 'scheme',
                        resolve: {
                            scheme: SchemeResolverService
                        }
                    }, {
                        path: ':schemeId',
                        component: FileComponent,
                        outlet: 'file',
                        canActivateChild: [AuthenticationGuard],
                        children: [
                            {
                                path: 'certificate/:id/view',
                                component: CertificateCrlViewComponent,
                                resolve: {
                                    certificateDetailsDTO: CertificateDetailsResolverService
                                },
                                data: {
                                    type: CerCrl.CER
                                }
                            }, {
                                path: 'crl/:id/view',
                                component: CertificateCrlViewComponent,
                                resolve: {
                                    crlDetailsDTO: CrlDetailsResolverService
                                },
                                data: {
                                    type: CerCrl.CRL
                                }
                            }, {
                                path: 'file/:id/edit',
                                component: FileUpdateComponent,
                                resolve: {
                                    fileDTO: FileResolverService
                                },
                                data: {
                                    formType: FileFormType.UPDATE
                                }
                            }, {
                                path: 'file/:id/replace',
                                component: FileUpdateComponent,
                                resolve: {
                                    fileDTO: FileResolverService
                                },
                                data: {
                                    formType: FileFormType.REPLACE
                                }
                            }, {
                                path: 'file/new',
                                component: FileUpdateComponent,
                                resolve: {
                                    fileDTO: FileResolverService
                                },
                                data: {
                                    formType: FileFormType.CREATE
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    },
    {
        path: 'certificate/:id/view',
        component: CertificateDetailsComponent,
        canActivate: [AuthenticationGuard],
        resolve: {
            certificateDetailsDTO: CertificateDetailsResolverService
        }
    },
    {
        path: 'crl/:id/view',
        component: CrlDetailsComponent,
        canActivate: [AuthenticationGuard],
        resolve: {
            crlDetailsDTO: CrlDetailsResolverService
        }
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
    imports: [RouterModule.forRoot(routes, {useHash: true}/*, {enableTracing: true}*/)], /// ! for refresh
    exports: [RouterModule]
})
export class AppRoutingModule {
}
