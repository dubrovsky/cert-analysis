import {File} from "./file.model";
import {Scheme} from "./scheme.model";

export class Crl {

    constructor(
        public id?: number,
        public thisUpdate?: string,
        public nextUpdate?: string,
        public crlNumber?: string,
        public issuerPrincipal?: string,
        public authKeyIdentifier?: string,
        public active?: boolean,
        public file: File = new File(),
        public scheme: Scheme = new Scheme()
    ) {
    }
}
