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
  private coins: number;
  private name: string;
  private apiUrl = '/api/users';

  constructor(private http: HttpClient) {
    const token = localStorage.getItem("auth")
    this.coins = -1;
    this.name = '';
    if (!!token && !this.tokenExpired(token)) {
      this.loggedIn.next(true);
    } else {
      this.loggedIn.next(false);
    }
  }

  private checkToken() {
    const token = localStorage.getItem("auth")
    if (!!token && !this.tokenExpired(token)) {
      this.name = this.tokenName(token);
      this.loggedIn.next(true);
    } else {
      this.loggedIn.next(false);
    }
  }

  private checkCoins() {
    if (this.coins === -1) {
      this.http.get<number>(this.apiUrl + "/coins").pipe(catchError(this.handleError)).subscribe(
        result => {
          this.coins = result;
        }, error => {
          this.coins = -1;
          localStorage.removeItem("auth")
        }
      )
    }
  }

  isLoggedIn() {
    this.checkToken();
    return this.loggedIn.getValue();
  }

  getName() {
    return this.name;
  }

  getCoins() {
    if (this.loggedIn.getValue()) {
      this.checkCoins();
    }
    return this.coins;
  }

  updateCoins() {
    this.http.get<number>(this.apiUrl + "/coins").pipe(catchError(this.handleError)).subscribe(
      result => {
        this.coins = result;
      }
    )
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
    location.reload();
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
    return this.http.post<Post>(this.apiUrl + "/create", data).pipe(
      tap((response: any) => response),
      catchError(this.handleError)
    );
  }
}
