import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../core/services/account.service';
import { BankAccount } from '../../core/models/models';
import { HeaderComponent } from '../../shared/components/header/header.component';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, HeaderComponent],
  templateUrl: './accounts.component.html',
  styleUrl: './accounts.component.scss'
})
export class AccountsComponent implements OnInit {
  accounts: BankAccount[] = [];
  isLoading = true;
  showCreateModal = false;
  newAccountType: 'SAVINGS' | 'CURRENT' = 'SAVINGS';
  notice = '';

  constructor(
    private accountService: AccountService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
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
  }

  copiedAcc = '';

  copyAccountNumber(accNum: string): void {
    if (!accNum) return;
    navigator.clipboard.writeText(accNum);
    this.copiedAcc = accNum;
    this.cdr.markForCheck();
    setTimeout(() => {
      this.copiedAcc = '';
      this.cdr.markForCheck();
    }, 2000);
  }

  openAccount(): void {
    this.accountService.createAccount(this.newAccountType).subscribe({
      next: (res) => {
        if (res.success) {
          this.showCreateModal = false;
          this.notice = 'New ' + this.newAccountType + ' account provisioned successfully!';
          this.loadAccounts();
          setTimeout(() => this.notice = '', 4000);
        }
      }
    });
  }
}
