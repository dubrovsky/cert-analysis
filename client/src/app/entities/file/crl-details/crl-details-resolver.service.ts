import {Injectable} from '@angular/core';
import {ActivatedRoute, ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {CrlDetailsDTO} from "./crl-details-dto.model";
import {EMPTY, Observable, of} from "rxjs";
import {mergeMap, take} from "rxjs/operators";
import {CertificateDetailsDTO} from "../certificate-details/certificate-details-dto.model";
import {FileService} from "../shared/file.service";

@Injectable({
    providedIn: 'root'
})
export class CrlDetailsResolverService implements Resolve<CrlDetailsDTO> {

    constructor(
        private fileService: FileService,
        private router: Router,
        private route: ActivatedRoute
    ) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<CrlDetailsDTO> | Promise<CrlDetailsDTO> | CrlDetailsDTO {
        const id = route.paramMap.get('id') ? +route.paramMap.get('id') : null;

        if (id) {
            return this.fileService.getCrlDetails(id).pipe(
                take(1),
                mergeMap(crlDetailsDTO => {
                    if (crlDetailsDTO) {
                        return of(crlDetailsDTO);
                    } else { // id not found
                        return of(new CrlDetailsDTO());
                        // this.router.navigate(['./'], { relativeTo: this.route });
                        // return EMPTY;
                    }
                })
            );
        }

        return of(new CrlDetailsDTO());
    }
}
