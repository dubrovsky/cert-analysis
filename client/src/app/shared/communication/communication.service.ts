import {Injectable} from '@angular/core';
import {BehaviorSubject, Subject} from "rxjs";
import {FileListComponent} from "../../entities/file/file-list/file-list.component";
import {UserListComponent} from "../../entities/user/user-list/user-list.component";

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

    private fileListComponentSource = new BehaviorSubject<FileListComponent>(null);
    fileListComponent$ = this.fileListComponentSource.asObservable();

    private userListComponentSource = new BehaviorSubject<UserListComponent>(null);
    userListComponent$ = this.userListComponentSource.asObservable();

    private returnUrlSource = new BehaviorSubject<string>('/');
    returnUrl$ = this.returnUrlSource.asObservable();

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

    setFileListComponent(fileListComponent: FileListComponent ) {
       this.fileListComponentSource.next(fileListComponent);
    }

    setUserListComponent(userListComponent: UserListComponent ) {
        this.userListComponentSource.next(userListComponent);
    }

    setReturnUrl(returnUrl: string) {
        this.returnUrlSource.next(returnUrl);
    }

}
