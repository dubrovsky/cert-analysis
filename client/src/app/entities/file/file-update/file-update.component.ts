import {AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {FileUpload} from "primeng/fileupload";
import {FileService} from "../shared/file.service";
import {ActivatedRoute, Router} from "@angular/router";
import {CommunicationService} from "../../../shared/communication/communication.service";
import {NotificationGroupService} from "../../notification-group/shared/notification-group.service";
import {NotificationGroup} from "../../../shared/model/notification-group.model";
import {FileDTO} from "../shared/file-dto.model";
import {FileFormType} from "../shared/file-form-type.enum";
import {Subscription} from "rxjs";

@Component({
    selector: 'app-file-update',
    templateUrl: './file-update.component.html',
    styleUrls: ['./file-update.component.css']
})
export class FileUpdateComponent implements OnInit, AfterViewInit, OnDestroy {

    displayFileForm: boolean = false;
    filesForm: FormGroup;
    @ViewChild(FileUpload) fileUpload: FileUpload;
    schemeId: number;
    notificationGroups: NotificationGroup[];
    fileFormType: FileFormType;
    formType = FileFormType;
    private routeSubscription: Subscription;

    // @Output() reloadFileListEvent = new EventEmitter();

    constructor(
        private fb: FormBuilder,
        private fileService: FileService,
        private router: Router,
        private route: ActivatedRoute,
        private communicationService: CommunicationService,
        private notificationGroupService: NotificationGroupService,
        private changeDetectorRef: ChangeDetectorRef
    ) {
        this.filesForm = this.fb.group({
            id: [''],
            comment: ['', Validators.maxLength(128)],
            schemeId: [''],
            notificationGroupIds: [[]]
        });
    }

    ngOnInit() {
        this.schemeId = +this.route.parent.snapshot.paramMap.get('schemeId');
        this.routeSubscription = this.route.data.subscribe((data: { fileDTO: FileDTO, formType: FileFormType }) => {
            this.fileFormType = data.formType;
            this.filesForm.patchValue(data.fileDTO);
        });

        this.notificationGroupService.findAll().subscribe(notificationGroups => {
            this.notificationGroups = notificationGroups;
            this.displayFileForm = true;
        });
    }

    onSubmitClick() {
        const fileDTO: FileDTO = this.filesForm.value;

        switch (this.fileFormType) {
            case this.formType.CREATE:
                this.create(fileDTO);
                break;
            case this.formType.UPDATE:
                this.update(fileDTO);
                break;
            case this.formType.REPLACE:
                this.replace(fileDTO);
                break;
        }
    }

    private create(fileDTO: FileDTO) {

        const formData = new FormData();
        for (let i = 0; i < this.fileUpload.files.length; i++) {
            formData.append('uploadFile', this.fileUpload.files[i], this.fileUpload.files[i].name);
        }

        formData.append('file', new Blob([JSON.stringify(fileDTO)], {type: "application/json"}));

        this.fileService.create(formData).subscribe(
            () => {
                this.communicationService.reloadFileList(this.schemeId);
                this.onCancelClick();
            },
            () => {
                this.onCancelClick();
            }
        );
    }

    private replace(fileDTO: FileDTO) {

        const formData = new FormData();
        formData.append('uploadFile', this.fileUpload.files[0], this.fileUpload.files[0].name);

        formData.append('file', new Blob([JSON.stringify(fileDTO)], {type: "application/json"}));

        this.fileService.replace(formData).subscribe(
            () => {
                this.communicationService.reloadFileList(this.schemeId);
                this.onCancelClick();
            },
            () => {
                this.onCancelClick();
            }
        );
    }

    private update(fileDTO: FileDTO) {
        this.fileService.update(fileDTO).subscribe(
            () => {
                this.communicationService.reloadFileList(this.schemeId);
                this.onCancelClick();
            },
            () => {
                this.onCancelClick();
            }
        )
    }

    onError() {
        if (this.fileUpload) {
            this.fileUpload.clear();
        }

        // this.filesForm.reset();
    }

    onCancelClick() {
        if (this.fileUpload) {
            this.fileUpload.clear();
        }

        this.filesForm.reset();
        this.displayFileForm = false;
        this.router.navigate([{outlets: {file: null}}], {relativeTo: this.route.parent.parent});
    }

    isDisabled() {
        return this.filesForm.invalid || (this.fileUpload && !this.fileUpload.hasFiles());
    }

    getAccepts(): string {
       return this.formType.CREATE === this.fileFormType ? '.cer,.crt,.crl,.p7b' :
           this.formType.REPLACE === this.fileFormType ? '.cer,.crt' : '';
    }

    ngAfterViewInit(): void {
        this.changeDetectorRef.detectChanges();
        // this.displayFileForm = true;
    }

    ngOnDestroy(): void {
        this.routeSubscription.unsubscribe();
    }    
}
