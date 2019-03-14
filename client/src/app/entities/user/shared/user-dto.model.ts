export class UserDTO {

    constructor(
        public id?: number,
        public login?: string,
        public password?: string,
        public firstname?: string,
        public lastname?: string,
        public surname?: string,
        public email?: string,
        public phone?: string,
        public enabled?: boolean,
        public roleId?: number,
        public notificationGroupIds: number[] = []

        // public roles?: RoleDTO[],
        // public notificationGroups?: NotificationGroupDTO[]
    ) {
    }
}