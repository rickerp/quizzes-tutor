export class ClarificationStats {
  username!: string;
  name!: string;
  dashboardState!: string;
  totalClarificationRequests!: number;
  publicClarificationRequests!: number;
  percentageOfPublicClarifications!: number;

  constructor(jsonObj?: ClarificationStats) {
    if (jsonObj) {
      this.username = jsonObj.username;
      this.name = jsonObj.name;
      this.dashboardState = jsonObj.dashboardState;
      this.totalClarificationRequests = jsonObj.totalClarificationRequests;
      this.publicClarificationRequests = jsonObj.publicClarificationRequests;
      this.percentageOfPublicClarifications =
        jsonObj.percentageOfPublicClarifications;
    }
  }
}
