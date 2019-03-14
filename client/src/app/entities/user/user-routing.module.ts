import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {UserComponent} from "./user/user.component";
import {UserListComponent} from "./user-list/user-list.component";
import {UserUpdateComponent} from "./user-update/user-update.component";
import {UserResolverService} from "./shared/user-resolver.service";

const routes: Routes = [{
    path: 'user',
    component: UserComponent,
    children: [{
        path: '',
        component: UserListComponent
    }, {
        path: ':id/edit',
        component: UserUpdateComponent,
        outlet: 'user',
        resolve: {
            userDTO: UserResolverService
        }
    }, {
        path: 'new',
        component: UserUpdateComponent,
        outlet: 'user',
        resolve: {
            userDTO: UserResolverService
        }
    }]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class UserRoutingModule {
}
