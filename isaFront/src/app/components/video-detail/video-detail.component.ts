import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { VideoService } from '../../services/video.service';
import { VideoResponse } from '../../models/video.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-video-detail',
  templateUrl: './video-detail.component.html',
  styleUrls: ['./video-detail.component.css']
})
export class VideoDetailComponent implements OnInit {

  video: VideoResponse | null = null;
  loading: boolean = true;
  errorMessage: string = '';
  apiBaseUrl: string = 'http://localhost:8080';

    constructor(
    private route: ActivatedRoute,
    private router: Router,
    private videoService: VideoService,
    public authService: AuthService
  ) {}
  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam) {
      this.errorMessage = 'Video ID nedostaje.';
      this.loading = false;
      return;
    }

    const id = +idParam;
    this.videoService.getVideoById(id).subscribe({
      next: (video) => {
        this.video = video;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Video nije pronađen.';
        this.loading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/home']);
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