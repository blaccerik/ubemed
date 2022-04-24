import {BidResponse} from "./BidResponse";
import {FormGroup} from "@angular/forms";

export class Product {
  cats: string[];
  price: number;
  bid: number;
  seller: string;
  title: string;
  id: number;
  file: any;

  bids: BidResponse[];
  form: FormGroup;
}
