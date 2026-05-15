import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { VideoService } from '../../services/video.service';
import { AuthService } from '../../services/auth.service';
import { VideoResponse } from '../../models/video.model';
import { UserProfileResponse } from '../../models/user.model';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  profile: UserProfileResponse | null = null;
  videos: VideoResponse[] = [];
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
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (!idParam) {
        this.errorMessage = 'ID korisnika nedostaje.';
        this.loading = false;
        return;
      }
      this.loadProfile(+idParam);
    });
  }

  loadProfile(userId: number): void {
    this.loading = true;
    this.profile = null;
    this.videos = [];

    this.videoService.getUserProfile(userId).subscribe({
      next: (profile) => {
        this.profile = profile;
        this.loadUserVideos(userId);
      },
      error: (err) => {
        this.errorMessage = 'Korisnik nije pronađen.';
        this.loading = false;
      }
    });
  }

  loadUserVideos(userId: number): void {
    this.videoService.getUserVideos(userId).subscribe({
      next: (videos) => {
        this.videos = videos;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
      }
    });
  }

  goHome(): void {
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