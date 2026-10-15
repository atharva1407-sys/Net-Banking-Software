import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../core/services/account.service';
import { BeneficiaryService } from '../../core/services/beneficiary.service';
import { TransferService } from '../../core/services/transfer.service';
import { BankAccount, Beneficiary, TransferResponse } from '../../core/models/models';
import { HeaderComponent } from '../../shared/components/header/header.component';

@Component({
  selector: 'app-transfers',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, HeaderComponent],
  templateUrl: './transfers.component.html',
  styleUrl: './transfers.component.scss'
})
export class TransfersComponent implements OnInit {
  accounts: BankAccount[] = [];
  beneficiaries: Beneficiary[] = [];

  selectedSourceId: number | null = null;
  selectedBeneficiaryId: string = 'MANUAL';
  destinationAccountNumber = '';
  amount: number | null = null;
  description = '';

  isReviewMode = false;
  isSubmitting = false;
  transferResult: TransferResponse | null = null;
  errorMessage = '';

  constructor(
    private accountService: AccountService,
    private beneficiaryService: BeneficiaryService,
    private transferService: TransferService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.accountService.getAccounts().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.accounts = res.data;
          this.route.queryParams.subscribe(params => {
            if (params['sourceId']) {
              this.selectedSourceId = Number(params['sourceId']);
            } else if (this.accounts.length > 0) {
              this.selectedSourceId = this.accounts[0].id;
            }

            if (params['destAccount']) {
              this.destinationAccountNumber = params['destAccount'];
            }
          });
          this.cdr.markForCheck();
        }
      }
    });

    this.beneficiaryService.getBeneficiaries().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.beneficiaries = res.data;
          this.cdr.markForCheck();
        }
      }
    });
  }

  get selectedAccount(): BankAccount | undefined {
    return this.accounts.find(a => a.id === this.selectedSourceId);
  }

  onBeneficiarySelect(): void {
    if (this.selectedBeneficiaryId === 'MANUAL') {
      this.destinationAccountNumber = '';
    } else {
      const b = this.beneficiaries.find(item => item.id === Number(this.selectedBeneficiaryId));
      if (b) {
        this.destinationAccountNumber = b.accountNumber;
      }
    }
  }

  proceedToReview(): void {
    this.errorMessage = '';
    if (!this.selectedSourceId) {
      this.errorMessage = 'Please select a source account.';
      return;
    }
    if (!this.destinationAccountNumber || this.destinationAccountNumber.trim().length < 6) {
      this.errorMessage = 'Please enter a valid destination account number.';
      return;
    }
    if (!this.amount || this.amount <= 0) {
      this.errorMessage = 'Transfer amount must be greater than zero.';
      return;
    }
    if (this.selectedAccount && this.amount > this.selectedAccount.balance) {
      this.errorMessage = `Insufficient funds. Available balance is ₹${this.selectedAccount.balance}.`;
      return;
    }

    this.isReviewMode = true;
  }

  confirmTransfer(): void {
    if (!this.selectedSourceId || !this.amount) return;

    this.isSubmitting = true;
    this.errorMessage = '';

    this.transferService.executeTransfer({
      sourceAccountId: this.selectedSourceId,
      destinationAccountNumber: this.destinationAccountNumber.trim(),
      amount: this.amount,
      description: this.description,
      idempotencyKey: 'IDEMP-' + Date.now() + '-' + Math.random().toString(36).substring(2, 7)
    }).subscribe({
      next: (res) => {
        this.isSubmitting = false;
        if (res.success && res.data) {
          this.transferResult = res.data;
          this.isReviewMode = false;
        } else {
          this.errorMessage = res.message || 'Transfer failed.';
        }
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err.error?.message || 'Transfer request failed. Please verify your details.';
      }
    });
  }

  resetForm(): void {
    this.transferResult = null;
    this.isReviewMode = false;
    this.amount = null;
    this.description = '';
    this.destinationAccountNumber = '';
    this.selectedBeneficiaryId = 'MANUAL';
    this.loadData();
  }

  get amountInWords(): string {
    if (!this.amount || this.amount <= 0) return '';
    const num = Math.floor(this.amount);
    return this.numberToIndianWords(num) + ' Rupees Only';
  }

  private numberToIndianWords(num: number): string {
    if (num === 0) return 'Zero';
    const a = ['', 'One ', 'Two ', 'Three ', 'Four ', 'Five ', 'Six ', 'Seven ', 'Eight ', 'Nine ', 'Ten ', 'Eleven ', 'Twelve ', 'Thirteen ', 'Fourteen ', 'Fifteen ', 'Sixteen ', 'Seventeen ', 'Eighteen ', 'Nineteen '];
    const b = ['', '', 'Twenty', 'Thirty', 'Forty', 'Fifty', 'Sixty', 'Seventy', 'Eighty', 'Ninety'];

    function inWords(n: number): string {
      let str = '';
      if (n > 19) {
        str += b[Math.floor(n / 10)] + (n % 10 ? ' ' + a[n % 10] : ' ');
      } else {
        str += a[n];
      }
      return str;
    }

    let result = '';
    const crore = Math.floor(num / 10000000);
    num %= 10000000;
    const lakh = Math.floor(num / 100000);
    num %= 100000;
    const thousand = Math.floor(num / 1000);
    num %= 1000;
    const hundred = Math.floor(num / 100);
    const remaining = num % 100;

    if (crore > 0) result += inWords(crore) + 'Crore ';
    if (lakh > 0) result += inWords(lakh) + 'Lakh ';
    if (thousand > 0) result += inWords(thousand) + 'Thousand ';
    if (hundred > 0) result += inWords(hundred) + 'Hundred ';
    if (remaining > 0) {
      if (result !== '') result += 'and ';
      result += inWords(remaining);
    }
    return result.trim();
  }

  downloadReceipt(): void {
    if (!this.transferResult) return;
    const receiptText = `SECUREBANK ELECTRONIC FUND TRANSFER RECEIPT
================================================
Reference ID:       ${this.transferResult.transferReference}
Timestamp:          ${new Date(this.transferResult.timestamp).toLocaleString()}
Debited Account:    ${this.transferResult.sourceAccountNumber}
Credited Account:   ${this.transferResult.destinationAccountNumber}
Amount Sent:        INR ${this.transferResult.amount.toFixed(2)}
Settlement Status:  SETTLED / COMPLETED
Remaining Balance:  INR ${this.transferResult.updatedSourceBalance.toFixed(2)}
Security Check:     256-bit Encrypted Ledger Verified
================================================
Thank you for using SecureBank NetBanking.`;

    const blob = new Blob([receiptText], { type: 'text/plain;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `receipt_${this.transferResult.transferReference}.txt`;
    a.click();
    URL.revokeObjectURL(url);
  }
}
