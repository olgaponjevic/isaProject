import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CommentPageResponse, CommentResponse } from '../models/comment.model';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  private apiUrl = 'http://localhost:8080/api/videos';

  constructor(private http: HttpClient) { }

  getComments(videoId: number, page: number): Observable<CommentPageResponse> {
    return this.http.get<CommentPageResponse>(`${this.apiUrl}/${videoId}/comments?page=${page}`);
  }

  createComment(videoId: number, content: string): Observable<CommentResponse> {
    return this.http.post<CommentResponse>(`${this.apiUrl}/${videoId}/comments`, { content });
  }
}