import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, TransferRequest, TransferResponse } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class TransferService {
  private readonly baseUrl = 'http://localhost:8080/api/v1/transfers';

  constructor(private http: HttpClient) {}

  executeTransfer(payload: TransferRequest): Observable<ApiResponse<TransferResponse>> {
    return this.http.post<ApiResponse<TransferResponse>>(this.baseUrl, payload);
  }
}
