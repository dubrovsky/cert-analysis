import {FileType} from "../../../shared/model/file-type.enum";
import {CertificateState} from "./certificate-state.enum";
import {CerCrl} from "./cer-crl.enum";

export class CertificateDTO {

    constructor(
        public id?: number,
        public uniqueId?: string,
        public fileId?: number,
        public schemeId?: number,
        public fio?: string,
        public position?: string,
        public begin?: string,
        public end?: string,
        public comment?: string,
        public state?: CertificateState,
        public stateDescr?: string,
        public serialNumber?: string,
        public name?: string,
        public type?: FileType,
        public cerCrl?: CerCrl,
        public issuerPrincipal?: string,
    ) {
    }
}
