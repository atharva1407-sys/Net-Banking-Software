import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BeneficiaryService } from '../../core/services/beneficiary.service';
import { Beneficiary } from '../../core/models/models';
import { HeaderComponent } from '../../shared/components/header/header.component';

@Component({
  selector: 'app-beneficiaries',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, HeaderComponent],
  templateUrl: './beneficiaries.component.html',
  styleUrl: './beneficiaries.component.scss'
})
export class BeneficiariesComponent implements OnInit {
  beneficiaries: Beneficiary[] = [];
  isLoading = true;
  showAddModal = false;

  newBeneficiary = {
    beneficiaryName: '',
    accountNumber: '',
    bankName: 'SecureBank',
    ifscCode: 'SECU0001001',
    nickname: ''
  };

  notice = '';
  errorMessage = '';

  constructor(
    private beneficiaryService: BeneficiaryService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadBeneficiaries();
  }

  loadBeneficiaries(): void {
    this.isLoading = true;
    this.beneficiaryService.getBeneficiaries().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.beneficiaries = res.data;
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

  addBeneficiary(): void {
    this.errorMessage = '';
    if (!this.newBeneficiary.beneficiaryName || !this.newBeneficiary.accountNumber) {
      this.errorMessage = 'Beneficiary Name and Account Number are required.';
      return;
    }

    this.beneficiaryService.addBeneficiary(this.newBeneficiary).subscribe({
      next: (res) => {
        if (res.success) {
          this.showAddModal = false;
          this.notice = 'Beneficiary added successfully!';
          this.newBeneficiary = {
            beneficiaryName: '',
            accountNumber: '',
            bankName: 'SecureBank',
            ifscCode: 'SECU0001001',
            nickname: ''
          };
          this.loadBeneficiaries();
          setTimeout(() => this.notice = '', 4000);
        }
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to add beneficiary.';
      }
    });
  }

  deleteBeneficiary(id: number): void {
    if (confirm('Are you sure you want to remove this beneficiary?')) {
      this.beneficiaryService.deleteBeneficiary(id).subscribe({
        next: () => {
          this.loadBeneficiaries();
        }
      });
    }
  }
}
