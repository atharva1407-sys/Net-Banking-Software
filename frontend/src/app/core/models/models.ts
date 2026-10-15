export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface CustomerProfile {
  id: number;
  fullName: string;
  email: string;
  mobile: string;
  status: string;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  customerId: string;
  fullName: string;
  email: string;
  mobile: string;
  role: string;
  internalCustomerId: number;
}

export interface BankAccount {
  id: number;
  accountNumber: string;
  maskedAccountNumber: string;
  accountType: 'SAVINGS' | 'CURRENT';
  currency: string;
  balance: number;
  status: 'ACTIVE' | 'FROZEN' | 'CLOSED';
  createdAt: string;
}

export interface Beneficiary {
  id: number;
  beneficiaryName: string;
  accountNumber: string;
  maskedAccountNumber: string;
  bankName: string;
  ifscCode: string;
  nickname?: string;
  createdAt: string;
}

export interface TransferRequest {
  sourceAccountId: number;
  destinationAccountNumber: string;
  amount: number;
  description?: string;
  idempotencyKey?: string;
}

export interface TransferResponse {
  transferReference: string;
  sourceAccountNumber: string;
  destinationAccountNumber: string;
  amount: number;
  status: string;
  description: string;
  updatedSourceBalance: number;
  timestamp: string;
}

export interface TransactionEntry {
  id: number;
  transactionReference: string;
  accountNumber: string;
  direction: 'DEBIT' | 'CREDIT';
  amount: number;
  balanceAfter: number;
  description: string;
  status: string;
  createdAt: string;
}

export interface StatementResponse {
  accountNumber: string;
  accountType: string;
  startDate: string;
  endDate: string;
  openingBalance: number;
  closingBalance: number;
  totalDebits: number;
  totalCredits: number;
  transactions: TransactionEntry[];
}

export interface NotificationItem {
  id: number;
  title: string;
  message: string;
  read: boolean;
  type: string;
  createdAt: string;
}
