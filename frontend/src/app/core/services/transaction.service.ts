import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, StatementResponse, TransactionEntry } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private readonly baseUrl = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient) {}

  getTransactions(accountId?: number, page: number = 0, size: number = 20): Observable<ApiResponse<{ content: TransactionEntry[]; totalElements: number }>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (accountId) {
      params = params.set('accountId', accountId);
    }
    return this.http.get<ApiResponse<{ content: TransactionEntry[]; totalElements: number }>>(`${this.baseUrl}/transactions`, { params });
  }

  getStatement(accountId: number, startDate?: string, endDate?: string): Observable<ApiResponse<StatementResponse>> {
    let params = new HttpParams().set('accountId', accountId);
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    return this.http.get<ApiResponse<StatementResponse>>(`${this.baseUrl}/statements`, { params });
  }

  downloadStatementCsv(accountId: number, startDate?: string, endDate?: string): Observable<Blob> {
    let params = new HttpParams().set('accountId', accountId);
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    return this.http.get(`${this.baseUrl}/statements/export`, {
      params,
      responseType: 'blob'
    });
  }
}
