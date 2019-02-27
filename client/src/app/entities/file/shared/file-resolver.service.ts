import {Injectable} from '@angular/core';
import {ActivatedRoute, ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {FileDTO} from "./file-dto.model";
import {EMPTY, Observable, of} from "rxjs";
import {FileService} from "./file.service";
import {mergeMap, take} from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class FileResolverService implements Resolve<FileDTO>{

    constructor(
        private fileService: FileService,
        private router: Router,
        private route: ActivatedRoute
    ) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<FileDTO> | Promise<FileDTO> | FileDTO {
        const id = route.paramMap.get('id') ? +route.paramMap.get('id') : null;

        if (id) {
            return this.fileService.findById(id).pipe(
                take(1),
                mergeMap(fileDTO => {
                    if (fileDTO) {
                        return of(fileDTO);
                    } else { // id not found
                        this.router.navigate(['./'], { relativeTo: this.route });
                        return EMPTY;
                    }
                })
            );
        }

        return of(new FileDTO(null, null, +route.parent.paramMap.get('schemeId')));
    }
}
