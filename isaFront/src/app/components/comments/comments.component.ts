import { Component, Input, OnInit } from '@angular/core';
import { CommentService } from '../../services/comment.service';
import { AuthService } from '../../services/auth.service';
import { CommentResponse } from '../../models/comment.model';

@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.css']
})
export class CommentsComponent implements OnInit {

  @Input() videoId!: number;

  comments: CommentResponse[] = [];
  currentPage: number = 0;
  totalPages: number = 0;
  totalComments: number = 0;
  loading: boolean = false;

  newComment: string = '';
  posting: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private commentService: CommentService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadComments(0);
  }

  loadComments(page: number): void {
    this.loading = true;
    this.commentService.getComments(this.videoId, page).subscribe({
      next: (response) => {
        this.comments = response.comments;
        this.currentPage = response.currentPage;
        this.totalPages = response.totalPages;
        this.totalComments = response.totalComments;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Greška pri učitavanju komentara.';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (!this.newComment.trim()) {
      this.errorMessage = 'Komentar ne sme biti prazan.';
      return;
    }

    this.posting = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.commentService.createComment(this.videoId, this.newComment).subscribe({
      next: (response) => {
        this.successMessage = 'Komentar je uspešno postavljen!';
        this.newComment = '';
        this.posting = false;
        this.loadComments(0);
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        if (err.status === 429) {
          this.errorMessage = err.error?.message || 'Premašen je limit komentara po satu.';
        } else {
          this.errorMessage = err.error?.message || 'Greška pri slanju komentara.';
        }
        this.posting = false;
      }
    });
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.loadComments(this.currentPage - 1);
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.loadComments(this.currentPage + 1);
    }
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