import {BidResponse} from "./BidResponse";

export class Product {
  cats: string[];
  price: number;
  bid: number;
  seller: string;
  title: string;
  id: number;
  file: any;
  bids: BidResponse[];
}
