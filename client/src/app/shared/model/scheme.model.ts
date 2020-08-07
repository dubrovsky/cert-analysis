import {CrlUrl} from "./crl-url.model";
import {CertificateDTO} from "../../entities/file/shared/certificate-dto.model";

export class Scheme {

    constructor(
        public id?: number,
        public sort?: number,
        public name?: string,
        public comment?: string,
        public type?: string,
        public certificates?: CertificateDTO[]
    ) {
    }
}
