import {BidResponse} from "./BidResponse";
import {FormGroup} from "@angular/forms";

export class Product {
  cats: number[];
  price: number;
  bid: number;
  seller: string;
  title: string;
  id: number;
  file: any;

  bids: BidResponse[];
  form: FormGroup;
}
