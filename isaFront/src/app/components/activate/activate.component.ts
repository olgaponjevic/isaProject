import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-activate',
  templateUrl: './activate.component.html',
  styleUrls: ['./activate.component.css']
})
export class ActivateComponent implements OnInit {

  loading: boolean = true;
  success: boolean = false;
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.loading = false;
      this.errorMessage = 'Nedostaje aktivacioni token.';
      return;
    }

    this.http.get(`http://localhost:8080/api/auth/activate?token=${token}`, { responseType: 'text' })
      .subscribe({
        next: () => {
          this.loading = false;
          this.success = true;
          setTimeout(() => this.router.navigate(['/login']), 4000);
        },
        error: (err) => {
          this.loading = false;
          this.errorMessage = err.error || 'Greška pri aktivaciji naloga.';
        }
      });
  }
}