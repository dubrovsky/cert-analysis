import {Injectable} from '@angular/core';
import {Subject} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class CommunicationService {

    private reloadFileListSource = new Subject<number>();
    reloadFileList$ = this.reloadFileListSource.asObservable();

    constructor() {
    }

    reloadFileList(schemeId: number) {
        this.reloadFileListSource.next(schemeId);
    }
}
