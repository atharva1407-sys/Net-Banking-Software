import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, BankAccount } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private readonly baseUrl = 'http://localhost:8080/api/v1/accounts';

  constructor(private http: HttpClient) {}

  getAccounts(): Observable<ApiResponse<BankAccount[]>> {
    return this.http.get<ApiResponse<BankAccount[]>>(this.baseUrl);
  }

  getAccountById(id: number): Observable<ApiResponse<BankAccount>> {
    return this.http.get<ApiResponse<BankAccount>>(`${this.baseUrl}/${id}`);
  }

  createAccount(type: 'SAVINGS' | 'CURRENT'): Observable<ApiResponse<BankAccount>> {
    return this.http.post<ApiResponse<BankAccount>>(`${this.baseUrl}?type=${type}`, {});
  }
}
