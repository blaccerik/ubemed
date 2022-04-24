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
