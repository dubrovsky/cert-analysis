export class CurrentUserDTO {

    constructor(
        public id?: number,
        public login?: string,
        public firstname?: string,
        public lastname?: string,
        public surname?: string,
        public email?: string,
        public phone?: string,
        public enabled?: boolean,
        public authorities?: string[]
    ) {
    }
}