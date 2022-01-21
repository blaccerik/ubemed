import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {catchError, map, Observable, of, throwError as observableThrowError} from 'rxjs';
import {Post} from "../model/Post";

@Injectable({
  providedIn: 'root'
})
export class ForumService {
  private apiUrl = '/api/forum';

  constructor(private http: HttpClient) {}

  public getAll(): Observable<Post[]> {
    console.log(this.apiUrl)
    console.log(this.http.get(this.apiUrl).pipe())
    return this.http
      .get<Post[]>(this.apiUrl)
      .pipe(map(data => data), catchError(this.handleError))
  }

  public get(id: string): Observable<Post> {
    return this.http
      .get<Post>(`${this.apiUrl}/${id}`)
      .pipe(map(data => data), catchError(this.handleError))
  }

  post(data: Post) {
    const headers = new Headers();
    headers.append('Content-Type', 'application/json');
    return this.http.post<Post>(this.apiUrl + "/new", data).pipe(catchError(this.handleError));
  }

  // put(data: ForumPost) {
  //   const headers = new Headers();
  //   headers.append('Content-Type', 'application/json');
  //   return this.http.put<ForumPost>(this.apiUrl, data).pipe(catchError(this.handleError));
  // }

  private handleError(res: HttpErrorResponse | any) {
    console.error(res.error || res.body.error);
    return observableThrowError(res.error || 'Server error');
  }
}
