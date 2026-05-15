import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { VideoService } from '../../services/video.service';

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css']
})
export class UploadComponent {

  uploadForm: FormGroup;
  thumbnailFile: File | null = null;
  videoFile: File | null = null;
  errorMessage: string = '';
  successMessage: string = '';
  loading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private videoService: VideoService,
    private router: Router
  ) {
    this.uploadForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      tagsInput: [''],
      latitude: [null],
      longitude: [null]
    });
  }

  onThumbnailSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.thumbnailFile = input.files[0];
    }
  }

  onVideoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.videoFile = input.files[0];
    }
  }

  onSubmit(): void {
    if (this.uploadForm.invalid) {
      this.errorMessage = 'Popuni sva obavezna polja.';
      return;
    }
    if (!this.thumbnailFile) {
      this.errorMessage = 'Izaberi thumbnail sliku.';
      return;
    }
    if (!this.videoFile) {
      this.errorMessage = 'Izaberi video fajl.';
      return;
    }

    const maxSize = 200 * 1024 * 1024;
    if (this.videoFile.size > maxSize) {
      this.errorMessage = 'Video je veći od 200MB.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const tagsInput = this.uploadForm.value.tagsInput || '';
    const tags = tagsInput.split(',')
      .map((t: string) => t.trim())
      .filter((t: string) => t.length > 0);

    this.videoService.createVideo(
      this.uploadForm.value.title,
      this.uploadForm.value.description,
      tags,
      this.uploadForm.value.latitude,
      this.uploadForm.value.longitude,
      this.thumbnailFile,
      this.videoFile
    ).subscribe({
      next: (response) => {
        this.successMessage = 'Video uspešno postavljen!';
        this.loading = false;
        setTimeout(() => this.router.navigate(['/home']), 2000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Greška pri postavljanju videa.';
        this.loading = false;
      }
    });
  }
}