import { Injectable } from '@angular/core';
import {HttpClient, HttpParams, HttpErrorResponse} from "@angular/common/http";
import {catchError, map, Observable, throwError as observableThrowError} from "rxjs";
import {Product} from "../model/Product";
import {FormGroup} from "@angular/forms";
import {Bid} from "../model/Bid";
import {BidResponse} from "../model/BidResponse";
import {ActivatedRoute} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class StoreService {
  private apiUrl = '/api/store';

  constructor(
    private http: HttpClient,
    private activatedRoute : ActivatedRoute
  ) { }

  public getAll(): Observable<Product[]> {
    // let data = {filter: "ee"};
    // let a = new HttpParams();
    let data = this.activatedRoute.snapshot.queryParams;
    return this.http.get<Product[]>(this.apiUrl, {params: data}).pipe()
  }

  private getImg(id: number): any {
    return this.http.get(this.apiUrl + "/" + id + "/image").pipe(catchError(this.handleError))
  }

  public sell(id: number, form: FormData): any {
    return this.http.put(this.apiUrl + "/" + id, form).pipe(catchError(this.handleError))
  }

  public makeBid(id: number, bid: Bid): any {
    return this.http.post(this.apiUrl + "/" + id, bid).pipe(catchError(this.handleError))
  }

  public upload(form: FormData) {
    return this.http.post<boolean>(this.apiUrl + "/add", form).pipe(catchError(this.handleError))
  }

  private handleError(res: HttpErrorResponse | any) {
    console.error(res.error || res.body.error);
    return observableThrowError(res.error || 'Server error');
  }

  public getImage(product: Product) {
    const value = localStorage.getItem("image-" + product.id)
    let file: any
    // save to localstorage
    if (!value) {
      this.getImg(product.id).subscribe(
        (next: any) => {
          file = next.file;
          localStorage.setItem("image-" + product.id, file);
          product.file = file
        }
      )
    } else {
      product.file = value;
    }
  }
}
