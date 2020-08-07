import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {Subscription} from "rxjs";
import {CrlDetailsDTO, RevokedCertificateDTO} from "./crl-details-dto.model";

@Component({
    selector: 'app-crl-details',
    templateUrl: './crl-details.component.html',
    styleUrls: ['./crl-details.component.css']
})
export class CrlDetailsComponent implements OnInit, OnDestroy {

    crlDetailsDTO: CrlDetailsDTO;
    private routeSubscription: Subscription;

    constructor(private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.routeSubscription = this.route.data.subscribe((data: { crlDetailsDTO: CrlDetailsDTO }) => {
            this.crlDetailsDTO = data.crlDetailsDTO;
        });
    }

    ngOnDestroy(): void {
        this.routeSubscription.unsubscribe();
    }

    rowTrackBy(index: number, item: RevokedCertificateDTO) {
        return item.serialNumber;
    }

}
