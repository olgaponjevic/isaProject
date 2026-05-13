import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  testBackend(): Observable<string> {
    return this.http.get(`${this.apiUrl}/test`, { responseType: 'text' });
  }
}