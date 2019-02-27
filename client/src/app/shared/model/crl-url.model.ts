import {Scheme} from "./scheme.model";

export class CrlUrl {

    constructor(
        public id?: number,
        public url?: string,
        public scheme: Scheme = new Scheme()
    ) {
    }
}
