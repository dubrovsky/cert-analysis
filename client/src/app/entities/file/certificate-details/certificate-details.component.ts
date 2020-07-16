import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {ActivatedRoute} from "@angular/router";
import {CertificateDetailsDTO} from "./certificate-details-dto.model";

@Component({
    selector: 'app-certificate-details',
    templateUrl: './certificate-details.component.html',
    styleUrls: ['./certificate-details.component.css']
})
export class CertificateDetailsComponent implements OnInit, OnDestroy {

    certificateDetailsDTO: CertificateDetailsDTO;
    private routeSubscription: Subscription;

    constructor(private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.routeSubscription = this.route.data.subscribe((data: { certificateDetailsDTO: CertificateDetailsDTO }) => {
            this.certificateDetailsDTO = data.certificateDetailsDTO;
        });
    }

    ngOnDestroy(): void {
        this.routeSubscription.unsubscribe();
    }

}
