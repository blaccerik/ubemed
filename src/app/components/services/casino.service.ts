import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {catchError, map, Observable} from "rxjs";
import {Post} from "../model/Post";
import {WheelData} from "../model/WheelData";

@Injectable({
  providedIn: 'root'
})
export class CasinoService {
  private apiUrl = '/api/casino';

  constructor(private http: HttpClient) { }

  public getData(): Observable<WheelData> {
    return this.http
        .get<WheelData>(this.apiUrl + "/data")
        .pipe()
  }
}
