import {Crl} from "./crl.model";

export class CrlRevoked {

    constructor(
        public id?: number,
        public revocationDate?: string,
        public serialNumber?: string,
        public reasonCode?: number,
        public crl: Crl = new Crl()
    ) {
    }
}
