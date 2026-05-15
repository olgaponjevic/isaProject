import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { VideoService } from '../../services/video.service';
import { VideoResponse } from '../../models/video.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  videos: VideoResponse[] = [];
  loading: boolean = true;
  errorMessage: string = '';
  apiBaseUrl: string = 'http://localhost:8080';

  constructor(
    public authService: AuthService,
    private videoService: VideoService
  ) {}

  ngOnInit(): void {
    this.loadVideos();
  }

  loadVideos(): void {
    this.loading = true;
    this.videoService.getAllVideos().subscribe({
      next: (videos) => {
        this.videos = videos;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Greška pri učitavanju videa.';
        this.loading = false;
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString('sr-RS', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}