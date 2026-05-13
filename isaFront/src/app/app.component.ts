import { Component, OnInit } from '@angular/core';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  title = 'isaFront';
  message = '';

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.testBackend().subscribe({
      next: (response) => {
        this.message = response;
        console.log('Odgovor sa backend-a:', response);
      },
      error: (error) => {
        console.error('Greška pri povezivanju sa backendom:', error);
      }
    });
  }
}