import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, Beneficiary } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class BeneficiaryService {
  private readonly baseUrl = 'http://localhost:8080/api/v1/beneficiaries';

  constructor(private http: HttpClient) {}

  getBeneficiaries(): Observable<ApiResponse<Beneficiary[]>> {
    return this.http.get<ApiResponse<Beneficiary[]>>(this.baseUrl);
  }

  addBeneficiary(payload: {
    beneficiaryName: string;
    accountNumber: string;
    bankName?: string;
    ifscCode?: string;
    nickname?: string;
  }): Observable<ApiResponse<Beneficiary>> {
    return this.http.post<ApiResponse<Beneficiary>>(this.baseUrl, payload);
  }

  deleteBeneficiary(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
