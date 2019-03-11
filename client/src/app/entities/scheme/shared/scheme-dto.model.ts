import {CrlUrlDTO} from "./crl-url-dto.model";

export class SchemeDTO {

    constructor(
        public id?: number,
        public name?: string,
        public comment?: string,
        public type?: string,
        public crlUrls: CrlUrlDTO[] = []
    ) {
    }
}
