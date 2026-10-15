package com.securebank.transfer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferResponse {
    private String transferReference;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal amount;
    private String status;
    private String description;
    private BigDecimal updatedSourceBalance;
    private LocalDateTime timestamp;

    public TransferResponse() {}

    public TransferResponse(String transferReference, String sourceAccountNumber, String destinationAccountNumber,
                            BigDecimal amount, String status, String description,
                            BigDecimal updatedSourceBalance, LocalDateTime timestamp) {
        this.transferReference = transferReference;
        this.sourceAccountNumber = sourceAccountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
        this.amount = amount;
        this.status = status;
        this.description = description;
        this.updatedSourceBalance = updatedSourceBalance;
        this.timestamp = timestamp;
    }

    public String getTransferReference() { return transferReference; }
    public void setTransferReference(String transferReference) { this.transferReference = transferReference; }

    public String getSourceAccountNumber() { return sourceAccountNumber; }
    public void setSourceAccountNumber(String sourceAccountNumber) { this.sourceAccountNumber = sourceAccountNumber; }

    public String getDestinationAccountNumber() { return destinationAccountNumber; }
    public void setDestinationAccountNumber(String destinationAccountNumber) { this.destinationAccountNumber = destinationAccountNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getUpdatedSourceBalance() { return updatedSourceBalance; }
    public void setUpdatedSourceBalance(BigDecimal updatedSourceBalance) { this.updatedSourceBalance = updatedSourceBalance; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
