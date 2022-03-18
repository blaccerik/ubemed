import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {catchError, map, Observable, throwError as observableThrowError} from "rxjs";
import {Product} from "../model/Product";
import {FormGroup} from "@angular/forms";
import {Bid} from "../model/Bid";
import {BidResponse} from "../model/BidResponse";

@Injectable({
  providedIn: 'root'
})
export class StoreService {
  private apiUrl = '/api/store';

  constructor(private http: HttpClient) { }

  public getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl).pipe()
  }

  public getImg(id: number): any {
    return this.http.get(this.apiUrl + "/" + id + "/image").pipe(catchError(this.handleError))
  }

  public getBids(id: number): Observable<BidResponse[]> {
    return this.http.get<BidResponse[]>(this.apiUrl + "/" + id + "/bids").pipe(catchError(this.handleError))
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

}
