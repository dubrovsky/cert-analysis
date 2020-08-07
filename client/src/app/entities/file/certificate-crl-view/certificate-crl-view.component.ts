import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Subscription} from "rxjs";
import {CerCrl} from "../shared/cer-crl.enum";

@Component({
    selector: 'app-certificate-crl-view',
    templateUrl: './certificate-crl-view.component.html',
    styleUrls: ['./certificate-crl-view.component.css']
})
export class CertificateCrlViewComponent implements OnInit, OnDestroy {

    displayView: boolean = false;
    type: CerCrl;
    private routeSubscription: Subscription;
    cerCrl = CerCrl;

    constructor(
        private router: Router,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit(): void {
        this.routeSubscription = this.route.data.subscribe((data: { type: CerCrl }) => {
            this.type = data.type;
        });

        this.displayView = true;
    }

    onCancelClick() {
        this.displayView = false;
        this.router.navigate([{outlets: {file: null}}], {relativeTo: this.route.parent.parent});
    }

    ngOnDestroy(): void {
        this.routeSubscription.unsubscribe();
    }
}