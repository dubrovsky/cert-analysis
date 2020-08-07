import {Injectable} from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router, CanActivateChild, CanLoad, Route, UrlSegment} from '@angular/router';
import {Observable} from 'rxjs';
import {AuthenticationService} from "./authentication.service";
import {Role} from "./role-enum";
import {CommunicationService} from "../communication/communication.service";

@Injectable({
    providedIn: 'root'
})
export class AuthenticationGuard implements CanActivate, CanActivateChild, CanLoad {

    constructor(
        private router: Router,
        private authenticationService: AuthenticationService,
        private communicationService: CommunicationService
    ) {
    }

    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

        return this.checkLogin(route.data.authorities, state.url);
    }

    canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
        return this.canActivate(childRoute, state);
    }

    canLoad(route: Route, segments: UrlSegment[]): Observable<boolean> | Promise<boolean> | boolean {
        let url = `/${route.path}`;
        return this.checkLogin([Role.ADMIN], url);
    }

    checkLogin(authorities: string[], url: string): boolean {
        const currentUser = this.authenticationService.currentUserValue;
        this.communicationService.setReturnUrl(url || '/');
        if (currentUser) {
            // check if route is restricted by role
            const hasAnyAuthority = this.authenticationService.hasAnyAuthority(authorities);
            if (!hasAnyAuthority) {
                // role not authorised so redirect to home page
                this.router.navigate(['/']);
                return false;
            }

            // authorised so return true
            return true;
        }

        // not logged in so redirect to login page with the return url
        this.router.navigate(['/login']/*, {queryParams: {returnUrl: url}}*/);
        return false;
    }

}
