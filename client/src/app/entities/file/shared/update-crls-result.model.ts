export class UpdateCrlsResult {
    constructor(
        public schemeName: string,
        public allCrls: number,
        public updatedCrls: number,
        public exceptions: string[]
    ) {
    }
}