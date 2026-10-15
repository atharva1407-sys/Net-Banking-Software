import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login-signup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login-signup.component.html',
  styleUrl: './login-signup.component.scss'
})
export class LoginSignupComponent implements OnInit {
  mode: 'login' | 'signup' = 'login';
  showPassword = false;
  showConfirmPassword = false;
  notice = '';
  noticeType: 'success' | 'error' = 'success';
  isLoading = false;

  login = {
    customerId: '',
    password: '',
    remember: false
  };

  signup = {
    fullName: '',
    email: '',
    mobile: '',
    customerId: '',
    password: '',
    confirmPassword: '',
    agree: false
  };

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  get passwordStrength(): { label: string; percent: number; colorClass: string } {
    const p = this.signup.password || '';
    if (!p) return { label: '', percent: 0, colorClass: '' };
    let score = 0;
    if (p.length >= 8) score++;
    if (/[A-Z]/.test(p)) score++;
    if (/[0-9]/.test(p)) score++;
    if (/[^A-Za-z0-9]/.test(p)) score++;

    if (score <= 1) return { label: 'Weak', percent: 25, colorClass: 'weak' };
    if (score === 2) return { label: 'Fair', percent: 50, colorClass: 'fair' };
    if (score === 3) return { label: 'Good', percent: 75, colorClass: 'good' };
    return { label: 'Strong', percent: 100, colorClass: 'strong' };
  }

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
      return;
    }

    this.route.url.subscribe(segments => {
      if (segments.some(s => s.path === 'signup')) {
        this.mode = 'signup';
      } else {
        this.mode = 'login';
      }
    });
  }

  setMode(mode: 'login' | 'signup'): void {
    this.mode = mode;
    this.notice = '';
  }

  submitLogin(form: NgForm): void {
    this.notice = '';
    if (form.invalid) {
      this.setNotice('Please enter your Customer ID and password.', 'error');
      return;
    }

    this.isLoading = true;
    this.authService.login({
      customerId: this.login.customerId.trim(),
      password: this.login.password
    }).subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res.success) {
          this.setNotice('Login successful! Redirecting to dashboard...', 'success');
          setTimeout(() => {
            this.router.navigate(['/dashboard']);
          }, 300);
        } else {
          this.setNotice(res.message || 'Login failed.', 'error');
        }
      },
      error: (err) => {
        this.isLoading = false;
        const msg = err.error?.message || 'Invalid Customer ID or password.';
        this.setNotice(msg, 'error');
      }
    });
  }

  submitSignup(form: NgForm): void {
    this.notice = '';
    if (form.invalid) {
      this.setNotice('Please complete all required fields and accept the terms.', 'error');
      return;
    }
    if (this.signup.password !== this.signup.confirmPassword) {
      this.setNotice('Passwords do not match.', 'error');
      return;
    }
    if (this.signup.password.length < 8) {
      this.setNotice('Use a password with at least 8 characters.', 'error');
      return;
    }

    this.isLoading = true;
    this.authService.register({
      fullName: this.signup.fullName.trim(),
      email: this.signup.email.trim(),
      mobile: this.signup.mobile.trim(),
      customerId: this.signup.customerId.trim(),
      password: this.signup.password
    }).subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res.success) {
          this.setNotice('Account created successfully! Redirecting...', 'success');
          setTimeout(() => {
            this.router.navigate(['/dashboard']);
          }, 300);
        } else {
          this.setNotice(res.message || 'Registration failed.', 'error');
        }
      },
      error: (err) => {
        this.isLoading = false;
        const msg = err.error?.message || 'Registration failed. Please check your details.';
        this.setNotice(msg, 'error');
      }
    });
  }

  setNotice(message: string, type: 'success' | 'error'): void {
    this.notice = message;
    this.noticeType = type;
    this.cdr.markForCheck();
  }
}
