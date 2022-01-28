import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {catchError, map, Observable, of, throwError as observableThrowError} from 'rxjs';
import {Post} from "../model/Post";
import {Vote} from "../model/Vote";

@Injectable({
  providedIn: 'root'
})
export class ForumService {
  private apiUrl = '/api/forum';

  constructor(private http: HttpClient) {}

  public getAll(): Observable<Post[]> {
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
    // const headers = new Headers();
    // headers.append('Content-Type', 'application/json');
    // headers.append("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdHJpbmciLCJleHAiOjE2NDI4ODcxMDIsImlhdCI6MTY0Mjg2OTEwMn0.4_jJ1sjpH9BBrYimNs0SvXkj4z7faN5f4sQ5r8Pf91g9RVZCX2o_5jxbsWPqXrFJJk4ksZkKpsUK6PKSUcCCVg")
    // { 'headers': headers }
    return this.http.post<Post>(this.apiUrl + "/new", data).pipe(catchError(this.handleError));
  }

  put(vote: Vote, id: number) {
    return this.http.put<Vote>(`${this.apiUrl}/${id}`, vote).pipe(map(data => data), catchError(this.handleError))
  }

  private handleError(res: HttpErrorResponse | any) {
    console.error(res.error || res.body.error);
    return observableThrowError(res.error || 'Server error');
  }
}
