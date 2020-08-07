export class CertificateDetailsDTO {
    constructor(
        public notBefore?: string,
        public notAfter?: string,
        public serialNumber?: string,
        public sha1?: string,
        public sha256?: string,
        public ca?: boolean,
        public subjectKeyId?: string,
        public version?: number,
        public authKeyId?: string,
        public issuerPrincipalAttrs?: {[key: string]: string},
        public subjectPrincipalAttrs?: {[key: string]: string},
        public keyUsages: string[] = [],
        public extendedKeyUsages: string[] = [],
        public crlDistributionPointFullNames: string[] = [],
        public crlDistributionPointCRLIssuers: string[] = [],
        public authorityInfoAccesses?: {[key: string]: string}
    ) {
    }
}
