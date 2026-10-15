import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { AccountService } from '../../core/services/account.service';
import { TransactionService } from '../../core/services/transaction.service';
import { BeneficiaryService } from '../../core/services/beneficiary.service';
import { BankAccount, TransactionEntry, Beneficiary } from '../../core/models/models';
import { HeaderComponent } from '../../shared/components/header/header.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  accounts: BankAccount[] = [];
  recentTransactions: TransactionEntry[] = [];
  beneficiaries: Beneficiary[] = [];
  isLoading = true;

  constructor(
    public authService: AuthService,
    private accountService: AccountService,
    private transactionService: TransactionService,
    private beneficiaryService: BeneficiaryService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.isLoading = true;
    this.accountService.getAccounts().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.accounts = res.data;
        }
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });

    this.transactionService.getTransactions(undefined, 0, 5).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.recentTransactions = res.data.content;
        }
        this.cdr.markForCheck();
      }
    });

    this.beneficiaryService.getBeneficiaries().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.beneficiaries = res.data;
        }
        this.cdr.markForCheck();
      }
    });
  }

  get totalBalance(): number {
    return this.accounts.reduce((sum, acc) => sum + (acc.balance || 0), 0);
  }

  get primaryAccount(): BankAccount | null {
    return this.accounts.length > 0 ? this.accounts[0] : null;
  }

  get totalCredits(): number {
    return this.recentTransactions
      .filter(t => t.direction === 'CREDIT')
      .reduce((sum, t) => sum + (t.amount || 0), 0);
  }

  get totalDebits(): number {
    return this.recentTransactions
      .filter(t => t.direction === 'DEBIT')
      .reduce((sum, t) => sum + (t.amount || 0), 0);
  }

  get savingsRatePercentage(): number {
    if (this.totalBalance <= 0) return 0;
    // Real goal calculation: ratio against target 100,000 INR
    const target = 100000;
    return Math.min(Math.round((this.totalBalance / target) * 100), 100);
  }

  get gaugeDashOffset(): number {
    const totalLength = 251.3;
    const pct = this.savingsRatePercentage / 100;
    return totalLength * (1 - pct);
  }

  get insightMessage(): string {
    return `Your transaction volume has increased by 12% this month.`;
  }

  barHeights: number[] = [30, 55, 38, 50, 70, 35, 48, 62, 85, 50, 65, 90, 55];

  categories = [
    { name: 'Shopping', amount: 8450, percent: 28, color: '#3B82F6' },
    { name: 'Food & Dining', amount: 5230, percent: 18, color: '#A855F7' },
    { name: 'Transport', amount: 3120, percent: 10, color: '#F472B6' },
    { name: 'Entertainment', amount: 2890, percent: 9, color: '#FB923C' },
    { name: 'Others', amount: 10450, percent: 35, color: '#38BDF8' }
  ];

  getMerchantIcon(tx: TransactionEntry): string {
    const d = (tx.description || '').toLowerCase();
    if (d.includes('salary')) return '💼';
    if (d.includes('netflix')) return '🎬';
    if (d.includes('playstation')) return '🎮';
    if (d.includes('apple')) return '🍎';
    if (d.includes('airbnb')) return '🏠';
    if (d.includes('consult')) return '👔';
    return tx.direction === 'CREDIT' ? '📥' : '📤';
  }
}
