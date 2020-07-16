import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Subscription} from "rxjs";
import {CertificateViewDTO} from "./certificate-view-dto.model";

@Component({
    selector: 'app-file-view',
    templateUrl: './file-view.component.html',
    styleUrls: ['./file-view.component.css']
})
export class FileViewComponent implements OnInit, OnDestroy {

    displayFileView: boolean = false;
    certificateViewDTO: CertificateViewDTO;
    private routeSubscription: Subscription;

    constructor(
        private router: Router,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit(): void {
        this.routeSubscription = this.route.data.subscribe((data: { certificateViewDTO: CertificateViewDTO }) => {
            this.certificateViewDTO = data.certificateViewDTO;
        });
        this.displayFileView = true;
    }

    onCancelClick() {
        this.displayFileView = false;
        this.router.navigate([{outlets: {file: null}}], {relativeTo: this.route.parent.parent});
    }

    ngOnDestroy(): void {
        this.routeSubscription.unsubscribe();
    }

}
