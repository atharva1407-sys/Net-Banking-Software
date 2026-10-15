import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LoginSignupComponent } from './features/auth/login-signup/login-signup.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { AccountsComponent } from './features/accounts/accounts.component';
import { TransfersComponent } from './features/transfers/transfers.component';
import { TransactionsComponent } from './features/transactions/transactions.component';
import { StatementsComponent } from './features/statements/statements.component';
import { BeneficiariesComponent } from './features/beneficiaries/beneficiaries.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginSignupComponent },
  { path: 'signup', component: LoginSignupComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'accounts', component: AccountsComponent, canActivate: [authGuard] },
  { path: 'transfers', component: TransfersComponent, canActivate: [authGuard] },
  { path: 'transactions', component: TransactionsComponent, canActivate: [authGuard] },
  { path: 'statements', component: StatementsComponent, canActivate: [authGuard] },
  { path: 'beneficiaries', component: BeneficiariesComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: 'login' }
];
