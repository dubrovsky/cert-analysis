import {Injectable} from '@angular/core';
import {ActivatedRoute, ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {UserDTO} from "./user-dto.model";
import {EMPTY, Observable, of} from "rxjs";
import {mergeMap, take} from "rxjs/operators";
import {UserService} from "./user.service";

@Injectable({
    providedIn: 'root'
})
export class UserResolverService implements Resolve<UserDTO>{

    constructor(
        private userService: UserService,
        private router: Router,
        private route: ActivatedRoute
    ) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<UserDTO> | Promise<UserDTO> | UserDTO {
        const id = route.paramMap.get('id') ? +route.paramMap.get('id') : null;

        if (id) {
            return this.userService.find(id).pipe(
                take(1),
                mergeMap(scheme => {
                    if (scheme) {
                        return of(scheme);
                    } else { // id not found
                        this.router.navigate(['./'], {relativeTo: this.route});
                        return EMPTY;
                    }
                })
            );
        }

        return of(new UserDTO());
    }
}
