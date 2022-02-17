import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {catchError, map, Observable, throwError as observableThrowError} from "rxjs";
import {Product} from "../model/Product";
import {FormGroup} from "@angular/forms";

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
    return this.http.get(this.apiUrl + "/" + id).pipe(catchError(this.handleError))
  }

  upload(form: FormData) {
    return this.http.post<boolean>(this.apiUrl + "/add", form).pipe(catchError(this.handleError))
  }

  private handleError(res: HttpErrorResponse | any) {
    console.error(res.error || res.body.error);
    return observableThrowError(res.error || 'Server error');
  }

}
