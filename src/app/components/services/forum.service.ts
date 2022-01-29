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
