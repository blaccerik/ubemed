import { Injectable } from '@angular/core';
import {ForumService} from "./forum.service";
import {Post} from "../model/Post";
import {BehaviorSubject, catchError, map, skipWhile, take, tap, throwError as observableThrowError} from "rxjs";
import {Login} from "../model/Login";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private loggedIn = new BehaviorSubject<boolean>(false);
  private apiUrl = '/api/users';

  constructor(private http: HttpClient) {
    const token = localStorage.getItem("auth")
    if (!!token && !this.tokenExpired(token)) {
      this.loggedIn.next(true);
    } else {
      this.loggedIn.next(false);
    }
  }

  private checkToken() {
    const token = localStorage.getItem("auth")
    if (!!token && !this.tokenExpired(token)) {
      this.loggedIn.next(true);
    } else {
      this.loggedIn.next(false);
    }
  }

  isLoggedIn() {
    this.checkToken()
    return this.loggedIn.getValue();
  }

  private handleError(res: HttpErrorResponse | any) {
    console.error(res.error || res.body.error);
    return observableThrowError(res.error || 'Server error');
  }

  private tokenExpired(token: string) {
    const expiry = (JSON.parse(atob(token.split('.')[1]))).exp;
    return (Math.floor((new Date).getTime() / 1000)) >= expiry;
  }

  tokenName(token: string) {
    return (JSON.parse(atob(token.split('.')[1]))).sub;
  }

  getToken() {
    const a = localStorage.getItem("auth");
    if (a) {
      return a;
    }
    return "";
  }

  logout() {
    localStorage.removeItem("auth");
    this.loggedIn.next(false);
  }

  login(data: Login) {
    return this.http.post<Post>(this.apiUrl + "/login", data).pipe(
      tap((response: any) => {
        this.loggedIn.next(true);
        localStorage.setItem("auth", response.token);
        }
      ),
      catchError(this.handleError)
    );
  }

  register(data: Login) {
    console.log(data)
    return this.http.post<Post>(this.apiUrl + "/create", data).pipe(
      tap((response: any) => response),
      catchError(this.handleError)
    );
  }

  // post(data: Login) {
  //   // const headers = new Headers();
  //   // headers.append('Content-Type', 'application/json');
  //   // headers.append("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdHJpbmciLCJleHAiOjE2NDI4ODcxMDIsImlhdCI6MTY0Mjg2OTEwMn0.4_jJ1sjpH9BBrYimNs0SvXkj4z7faN5f4sQ5r8Pf91g9RVZCX2o_5jxbsWPqXrFJJk4ksZkKpsUK6PKSUcCCVg")
  //   // { 'headers': headers }
  //   return this.http.post<Post>(this.apiUrl + "/login", data).pipe(
  //     tap((response: any) => {
  //       this.loggedIn.next(true);
  //       localStorage.setItem("auth", response.token);
  //     }),
  //     catchError(this.handleError)
  //   );
  // }
}
