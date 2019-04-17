import {Component, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {SchemeService} from "../shared/scheme.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Scheme} from "../../../shared/model/scheme.model";
import {Observable} from "rxjs";
import {SchemeDTO} from "../shared/scheme-dto.model";
import {CrlUrlDTO} from "../shared/crl-url-dto.model";
import {CommunicationService} from "../../../shared/communication/communication.service";

@Component({
    selector: 'app-scheme-update',
    templateUrl: './scheme-update.component.html',
    styleUrls: ['./scheme-update.component.css']
})
export class SchemeUpdateComponent implements OnInit {

    displaySchemeForm: boolean = false;
    schemeForm: FormGroup;

    constructor(
        private fb: FormBuilder,
        private schemeService: SchemeService,
        private router: Router,
        private communicationService: CommunicationService,
        private route: ActivatedRoute,
    ) {

        this.schemeForm = this.fb.group({
            id: [''],
            name: ['', Validators.compose([Validators.required, Validators.maxLength(48)])],
            comment: ['', Validators.maxLength(128)],
            type: ['', Validators.compose([Validators.required, Validators.maxLength(12)])],
            crlUrls: this.fb.array([])
        });
    }

    ngOnInit() {
        this.route.data.subscribe((data: { scheme: SchemeDTO }) => {
            this.schemeForm.patchValue(data.scheme);
            data.scheme.crlUrls.forEach(url => {
                this.onAddCrlUrl(url);
            });
            this.displaySchemeForm = true;
        });
    }

    get crlUrls() {
        return this.schemeForm.get('crlUrls') as FormArray;
    }

    onAddCrlUrl(crlUrl?: CrlUrlDTO) {
        // this.crlUrls.push(this.fb.control(value, Validators.compose([Validators.required, Validators.maxLength(128), Validators.pattern('(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?')])));
        if(!crlUrl) {
            crlUrl = new CrlUrlDTO();
        }
        this.crlUrls.push(
            this.fb.group({
                id: [crlUrl.id],
                url: [crlUrl.url, Validators.compose([Validators.required, Validators.maxLength(128), Validators.pattern('(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?')])]
            })
        );
    }

    onDeleteCrlUrl(index: number) {
        this.crlUrls.removeAt(index);
    }

    onSubmitClick() {
        const scheme: SchemeDTO = this.schemeForm.value;
        if (scheme.id) {
            this.subscribeToSaveResponse(this.schemeService.update(scheme));
        } else {
            this.subscribeToSaveResponse(this.schemeService.create(scheme));
        }
    }

    private subscribeToSaveResponse(schemeObservable: Observable<Scheme>) {
        schemeObservable.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
    }

    private onSaveSuccess() {
        this.onCancelClick();
        this.communicationService.reloadSchemeList();
        // this.router.navigate(['/']);
        // location.reload();
    }

    private onSaveError() {
        this.onCancelClick();
    }

    onCancelClick() {
        this.schemeForm.reset();
        this.displaySchemeForm = false;
        this.router.navigate([{outlets: {scheme: null}}], {relativeTo: this.route.parent});
    }

}
