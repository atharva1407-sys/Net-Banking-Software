import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../core/services/account.service';
import { TransactionService } from '../../core/services/transaction.service';
import { BankAccount, TransactionEntry } from '../../core/models/models';
import { HeaderComponent } from '../../shared/components/header/header.component';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, HeaderComponent],
  templateUrl: './transactions.component.html',
  styleUrl: './transactions.component.scss'
})
export class TransactionsComponent implements OnInit {
  accounts: BankAccount[] = [];
  transactions: TransactionEntry[] = [];
  isLoading = true;

  selectedAccountId: number | null = null;
  filterDirection: 'ALL' | 'DEBIT' | 'CREDIT' = 'ALL';
  searchTerm = '';

  currentPage = 0;
  pageSize = 20;
  totalElements = 0;

  constructor(
    private accountService: AccountService,
    private transactionService: TransactionService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.accountService.getAccounts().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.accounts = res.data;
          this.cdr.markForCheck();
        }
      }
    });

    this.loadTransactions();
  }

  loadTransactions(): void {
    this.isLoading = true;
    const accId = this.selectedAccountId ? Number(this.selectedAccountId) : undefined;
    this.transactionService.getTransactions(accId, this.currentPage, this.pageSize).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.transactions = res.data.content;
          this.totalElements = res.data.totalElements;
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

  get filteredTransactions(): TransactionEntry[] {
    return this.transactions.filter(t => {
      const matchDir = this.filterDirection === 'ALL' || t.direction === this.filterDirection;
      const term = this.searchTerm.toLowerCase().trim();
      const matchSearch = !term ||
        t.transactionReference.toLowerCase().includes(term) ||
        t.description.toLowerCase().includes(term) ||
        t.accountNumber.toLowerCase().includes(term);
      return matchDir && matchSearch;
    });
  }

  onAccountChange(): void {
    this.currentPage = 0;
    this.loadTransactions();
  }

  get totalPages(): number {
    return Math.ceil(this.totalElements / this.pageSize) || 1;
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadTransactions();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadTransactions();
    }
  }

  downloadCSV(): void {
    const data = this.filteredTransactions;
    if (!data || data.length === 0) return;

    const headers = ['Timestamp', 'Reference', 'Account Number', 'Description', 'Direction', 'Amount (INR)', 'Balance After (INR)', 'Status'];
    const rows = data.map(t => [
      `"${new Date(t.createdAt).toLocaleString()}"`,
      `"${t.transactionReference}"`,
      `"${t.accountNumber}"`,
      `"${(t.description || '').replace(/"/g, '""')}"`,
      `"${t.direction}"`,
      t.amount.toFixed(2),
      t.balanceAfter.toFixed(2),
      `"${t.status}"`
    ]);

    const csvContent = [headers.join(','), ...rows.map(r => r.join(','))].join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `transactions_export_${Date.now()}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  }
}
