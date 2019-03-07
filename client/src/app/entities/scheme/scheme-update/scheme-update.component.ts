import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {SchemeService} from "../shared/scheme.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Scheme} from "../../../shared/model/scheme.model";
import {Observable} from "rxjs";

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
        private route: ActivatedRoute,
    ) {

        this.schemeForm = this.fb.group({
            id: [''],
            name: ['', Validators.compose([Validators.required, Validators.maxLength(48)])],
            comment: ['', Validators.maxLength(128)],
            type: ['', Validators.compose([Validators.required, Validators.maxLength(12)])]
        });
    }

    ngOnInit() {
        this.route.data.subscribe((data: { scheme: Scheme }) => {
            this.schemeForm.patchValue(data.scheme);
        });

        this.displaySchemeForm = true;
    }

    onSubmitClick() {
        const scheme: Scheme = this.schemeForm.value;
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
        location.reload();
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
