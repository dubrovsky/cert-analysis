import {Injectable} from '@angular/core';
import {BehaviorSubject, Subject} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class CommunicationService {

    private reloadFileListSource = new Subject<number>();
    reloadFileList$ = this.reloadFileListSource.asObservable();

    private reloadUserListSource = new Subject<void>();
    reloadUserList$ = this.reloadUserListSource.asObservable();

    private reloadSchemeListSource = new Subject<void>();
    reloadSchemeList$ = this.reloadSchemeListSource.asObservable();

    private loadingSource = new BehaviorSubject<boolean>(false);
    loading$ = this.loadingSource.asObservable();

    constructor() {
    }

    reloadFileList(schemeId: number) {
        this.reloadFileListSource.next(schemeId);
    }

    reloadUserList() {
        this.reloadUserListSource.next();
    }

    reloadSchemeList() {
        this.reloadSchemeListSource.next();
    }

    startLoading() {
        this.loadingSource.next(true);
    }

    stopLoading() {
        this.loadingSource.next(false);
    }
}
