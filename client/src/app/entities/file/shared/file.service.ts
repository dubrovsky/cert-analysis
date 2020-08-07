import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {APP_CONFIG_TOKEN, AppConfig} from "../../../app.config";
import {Observable} from "rxjs";
import {CertificateDTO} from "./certificate-dto.model";
import {FileDTO} from "./file-dto.model";
import {CertificateDetailsDTO} from "../certificate-details/certificate-details-dto.model";
import {CrlDetailsDTO} from "../crl-details/crl-details-dto.model";

@Injectable({
    providedIn: 'root'
})
export class FileService {

    apiUrl: string;

    constructor(protected http: HttpClient, @Inject(APP_CONFIG_TOKEN) public appConfig: AppConfig) {
        this.apiUrl = appConfig.apiUrl + '/file';
    }

    findAllBySchemeId(schemeId: number, sortField: string, sortOrder: number): Observable<CertificateDTO[]> {
        return this.http.get<CertificateDTO[]>(
            `${this.apiUrl}s/${schemeId}`,
            {params: {sortField: sortField, sortOrder: sortOrder.toString()}}
        );
    }

    findById(id: number): Observable<FileDTO> {
        return this.http.get<FileDTO>(`${this.apiUrl}/${id}`);
    }

    create(files: FormData): Observable<string> {
        return this.http.post(this.apiUrl, files, {responseType: 'text'});
    }

    replace(files: FormData): Observable<FileDTO> {
        return this.http.put<FileDTO>(`${this.apiUrl}/replace`, files);
    }

    update(file: FileDTO): Observable<FileDTO> {
        return this.http.put<FileDTO>(this.apiUrl, file);
    }

    delete(certificateId: number, fileId: number): Observable<any> {
        return this.http.delete<any>(`${this.apiUrl}/${certificateId}/${fileId}`);
    }

    download(fileId: number) {
        return this.http.get(`${this.apiUrl}/download/${fileId}`, {responseType: 'blob', observe: 'response'});
    }

    updateCrls(): Observable<string> {
        return this.http.get(`${this.apiUrl}/crls/update`, {responseType: 'text'});
    }

    getCertificateDetails(id: number): Observable<CertificateDetailsDTO> {
        return this.http.get<CertificateDetailsDTO>(`${this.appConfig.apiUrl}/certificate/${id}/view`);
    }

    getCrlDetails(id: number): Observable<CrlDetailsDTO> {
        return this.http.get<CrlDetailsDTO>(`${this.appConfig.apiUrl}/crl/${id}/view`);
    }
}
