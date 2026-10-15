import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../core/services/account.service';
import { TransactionService } from '../../core/services/transaction.service';
import { BankAccount, StatementResponse } from '../../core/models/models';
import { HeaderComponent } from '../../shared/components/header/header.component';

@Component({
  selector: 'app-statements',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, HeaderComponent],
  templateUrl: './statements.component.html',
  styleUrl: './statements.component.scss'
})
export class StatementsComponent implements OnInit {
  accounts: BankAccount[] = [];
  selectedAccountId: number | null = null;
  startDate: string = '';
  endDate: string = '';

  statement: StatementResponse | null = null;
  isLoading = false;

  constructor(
    private accountService: AccountService,
    private transactionService: TransactionService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const today = new Date();
    const lastMonth = new Date();
    lastMonth.setDate(today.getDate() - 30);

    this.endDate = today.toISOString().split('T')[0];
    this.startDate = lastMonth.toISOString().split('T')[0];

    this.accountService.getAccounts().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.accounts = res.data;
          this.route.queryParams.subscribe(params => {
            if (params['accountId']) {
              this.selectedAccountId = Number(params['accountId']);
            } else if (this.accounts.length > 0) {
              this.selectedAccountId = this.accounts[0].id;
            }
            if (this.selectedAccountId) {
              this.generateStatement();
            }
            this.cdr.markForCheck();
          });
        }
      }
    });
  }

  generateStatement(): void {
    if (!this.selectedAccountId) return;
    this.isLoading = true;
    this.transactionService.getStatement(this.selectedAccountId, this.startDate, this.endDate).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.statement = res.data;
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

  downloadCsv(): void {
    if (!this.selectedAccountId) return;
    this.transactionService.downloadStatementCsv(this.selectedAccountId, this.startDate, this.endDate).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `SecureBank-Statement-${this.selectedAccountId}.csv`;
        a.click();
        window.URL.revokeObjectURL(url);
      }
    });
  }
}
