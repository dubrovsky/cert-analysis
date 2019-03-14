import {RoleDTO} from "./role-dto.model";
import {NotificationGroupDTO} from "./notification-group-dto.model";

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
        public roles?: RoleDTO[],
        public notificationGroups?: NotificationGroupDTO[]
    ) {
    }
}