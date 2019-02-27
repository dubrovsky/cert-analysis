export class FileDTO {

    constructor(
        public id?: number,
        public comment?: string,
        public schemeId?: number,
        public notificationGroupIds: number[] = []
    ) {
    }
}
