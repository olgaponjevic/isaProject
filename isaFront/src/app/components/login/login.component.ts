import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  loginForm: FormGroup;
  errorMessage: string = '';
  loading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.router.navigate(['/home']);
      },
      error: (err) => {
        if (err.status === 429) {
          this.errorMessage = 'Previše neuspešnih pokušaja prijave. Pokušaj ponovo za minut.';
        } else if (err.status === 401 || err.status === 403) {
          this.errorMessage = 'Pogrešan email ili lozinka, ili nalog nije aktiviran.';
        } else {
          this.errorMessage = 'Greška pri prijavi.';
        }
        this.loading = false;
      }
    });
  }
}