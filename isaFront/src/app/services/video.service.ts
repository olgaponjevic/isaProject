import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { VideoResponse } from '../models/video.model';
import { UserProfileResponse } from '../models/user.model';


@Injectable({
  providedIn: 'root'
})
export class VideoService {

  private apiUrl = 'http://localhost:8080/api/videos';

  constructor(private http: HttpClient) { }

  getAllVideos(): Observable<VideoResponse[]> {
    return this.http.get<VideoResponse[]>(this.apiUrl);
  }

  getVideoById(id: number): Observable<VideoResponse> {
    return this.http.get<VideoResponse>(`${this.apiUrl}/${id}`);
  }

    getUserProfile(userId: number): Observable<UserProfileResponse> {
    return this.http.get<UserProfileResponse>(`http://localhost:8080/api/users/${userId}`);
  }

  getUserVideos(userId: number): Observable<VideoResponse[]> {
    return this.http.get<VideoResponse[]>(`http://localhost:8080/api/users/${userId}/videos`);
  }

  createVideo(
    title: string,
    description: string,
    tags: string[],
    latitude: number | null,
    longitude: number | null,
    thumbnail: File,
    video: File
  ): Observable<VideoResponse> {
    const formData = new FormData();
    formData.append('title', title);
    formData.append('description', description);

    tags.forEach(tag => formData.append('tags', tag));

    if (latitude !== null) {
      formData.append('latitude', latitude.toString());
    }
    if (longitude !== null) {
      formData.append('longitude', longitude.toString());
    }

    formData.append('thumbnail', thumbnail);
    formData.append('video', video);

    return this.http.post<VideoResponse>(this.apiUrl, formData);
  }
  
}