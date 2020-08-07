import {Injectable} from '@angular/core';
import {ActivatedRoute, ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {EMPTY, Observable, of} from "rxjs";
import {FileService} from "../shared/file.service";
import {mergeMap, take} from "rxjs/operators";
import {CertificateDetailsDTO} from "./certificate-details-dto.model";

@Injectable({
    providedIn: 'root'
})
export class CertificateDetailsResolverService implements Resolve<CertificateDetailsDTO> {

    constructor(
        private fileService: FileService,
        private router: Router,
        private route: ActivatedRoute) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<CertificateDetailsDTO> | Promise<CertificateDetailsDTO> | CertificateDetailsDTO {
        const id = route.paramMap.get('id') ? +route.paramMap.get('id') : null;

        if (id) {
            return this.fileService.getCertificateDetails(id).pipe(
                take(1),
                mergeMap(certificateDetailsDTO => {
                    if (certificateDetailsDTO) {
                        return of(certificateDetailsDTO);
                    } else { // id not found
                        of(new CertificateDetailsDTO());
                        /*this.router.navigate(['./'], { relativeTo: this.route });
                        return EMPTY;*/
                    }
                })
            );
        }

        return of(new CertificateDetailsDTO());
    }
}
