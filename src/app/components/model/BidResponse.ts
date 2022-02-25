export class BidResponse {

  username: string;
  amount: number;

  constructor(username: string, amount: number) {
    this.username = username;
    this.amount = amount;
  }
}
