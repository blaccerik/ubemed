import { Injectable } from '@angular/core';
import {Post} from "../model/Post";
import {
  BehaviorSubject,
  catchError,
  map, observable,
  Observable,
  skipWhile,
  take,
  tap,
  throwError as observableThrowError
} from "rxjs";
import {Login} from "../model/Login";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {UserData} from "../model/UserData";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private loggedIn: boolean;
  private coins: number;
  private name: string;
  private lastClaimDate: number
  private apiUrl = '/api/users';

  constructor(private http: HttpClient) {
    this.loggedIn = false;
    // const token = localStorage.getItem("auth")
    this.coins = -1;
    this.name = '';
    this.checkToken()
  }

  public getCoins() {
    return this.coins;
  }

  public getLastClaimDate() {
    return this.lastClaimDate;
  }

  public update() {
    this.http.get<UserData>(this.apiUrl + "/data").pipe(catchError(this.handleError)).subscribe(
      next => {
        this.coins = next.coins;
        this.lastClaimDate = next.lastClaimDate;
      }
    )
  }

  private checkToken() {
    const token = localStorage.getItem("auth")
    if (!!token && !this.tokenExpired(token)) {
      this.name = this.tokenName(token);
      this.loggedIn = true;
    } else {
      localStorage.removeItem("auth")
      this.loggedIn = false;
    }
  }

  isLoggedIn() {
    this.checkToken();
    return this.loggedIn;
  }

  public getName() {
    return this.name;
  }

  private handleError(res: HttpErrorResponse | any) {
    // console.error(res.error || res.body.error);
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

  claim() {
    return this.http.get(this.apiUrl + "/claim").pipe(catchError(this.handleError))
  }

  logout() {
    localStorage.removeItem("auth");
    this.loggedIn = false;
    this.coins = -1;
    this.lastClaimDate = -1;
    location.reload();
  }

  login(data: Login) {
    return this.http.post<Post>(this.apiUrl + "/login", data).pipe(
      tap((response: any) => {
        this.loggedIn = true;
        localStorage.setItem("auth", response.token);
        }
      ),
      catchError(this.handleError)
    );
  }

  register(data: Login) {
    return this.http.post<Post>(this.apiUrl + "/create", data).pipe(
      tap((response: any) => response),
      catchError(this.handleError)
    );
  }
}
