export class CrlDetailsDTO {
    constructor(
        public thisUpdate?: string,
        public nextUpdate?: string,
        public crlNumber?: string,
        // public revokedCertificates: { serialNumber: string, revocationDate: string, revocationReason: string }[] = [],
        public revokedCertificates: RevokedCertificateDTO[] = [],
        public version?: number,
        public authKeyId?: string,
        public issuerPrincipalAttrs?: { [key: string]: string }
    ) {
    }
}

export interface RevokedCertificateDTO {
    serialNumber: string,
    revocationDate: string,
    revocationReason: string
}