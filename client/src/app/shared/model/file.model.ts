import {Scheme} from "./scheme.model";
import {FileType} from "./file-type.enum";

export class File {

    constructor(
        public id?: number,
        public size?: number,
        public name?: string,
        public comment?: string,
        public contentType?: string,
        public type?: FileType,
        public scheme: Scheme = new Scheme()
    ) {
    }
}
