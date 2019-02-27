import {FileType} from "../../../shared/model/file-type.enum";
import {CertificateState} from "./certificate-state.enum";

export class CertificateDTO {

    constructor(
        public id?: number,
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
        public issuerPrincipal?: string,
    ) {
    }
}
