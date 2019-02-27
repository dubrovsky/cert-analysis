import {File} from "./file.model";
import {Scheme} from "./scheme.model";

export class Certificate {

    constructor(
        public id?: number,
        public fio?: string,
        public position?: string,
        public address?: string,
        public serialNumber?: string,
        public commonName?: string,
        public subjectKeyIdentifier?: string,
        public issuerKeyIdentifier?: string,
        public issuerPrincipal?: string,
        public subjectPrincipal?: string,
        public notBefore?: string,
        public notAfter?: string,
        public file: File = new File(),
        public scheme: Scheme = new Scheme()
    ) {
    }
}
