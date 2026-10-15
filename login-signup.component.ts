import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

@Component({
  selector: 'app-login-signup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login-signup.component.html',
  styleUrl: './login-signup.component.scss'
})
export class LoginSignupComponent {
  mode: 'login' | 'signup' = 'login';
  showPassword = false;
  showConfirmPassword = false;
  notice = '';
  noticeType: 'success' | 'error' = 'success';

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
    // Demo UI only. Replace with an Angular service call to Spring Boot.
    this.setNotice('Demo mode: connect this form to your Spring Boot login API.', 'success');
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
    // Demo UI only. Replace with an Angular service call to Spring Boot.
    this.setNotice('Demo mode: connect this form to your Spring Boot registration API.', 'success');
  }

  private setNotice(message: string, type: 'success' | 'error'): void {
    this.notice = message;
    this.noticeType = type;
  }
}
