import {Injectable} from '@angular/core';
import {ActivatedRoute, ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {SchemeService} from "./scheme.service";
import {EMPTY, Observable, of} from "rxjs";
import {mergeMap, take} from "rxjs/operators";
import {SchemeDTO} from "./scheme-dto.model";

@Injectable({
    providedIn: 'root'
})
export class SchemeResolverService implements Resolve<SchemeDTO> {

    constructor(
        private schemeService: SchemeService,
        private router: Router,
        private route: ActivatedRoute
    ) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<SchemeDTO> | Promise<SchemeDTO> | SchemeDTO {
        const id = route.paramMap.get('id') ? +route.paramMap.get('id') : null;

        if (id) {
            return this.schemeService.find(id).pipe(
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

        return of(new SchemeDTO());
    }
}
