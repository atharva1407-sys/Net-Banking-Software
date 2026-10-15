package com.securebank.beneficiary.dto;

import java.time.LocalDateTime;

public class BeneficiaryResponse {
    private Long id;
    private String beneficiaryName;
    private String accountNumber;
    private String maskedAccountNumber;
    private String bankName;
    private String ifscCode;
    private String nickname;
    private LocalDateTime createdAt;

    public BeneficiaryResponse() {}

    public BeneficiaryResponse(Long id, String beneficiaryName, String accountNumber,
                               String bankName, String ifscCode, String nickname, LocalDateTime createdAt) {
        this.id = id;
        this.beneficiaryName = beneficiaryName;
        this.accountNumber = accountNumber;
        this.maskedAccountNumber = maskNumber(accountNumber);
        this.bankName = bankName;
        this.ifscCode = ifscCode;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }

    private static String maskNumber(String num) {
        if (num == null || num.length() < 4) return "••••";
        return "•••• •••• " + num.substring(num.length() - 4);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBeneficiaryName() { return beneficiaryName; }
    public void setBeneficiaryName(String beneficiaryName) { this.beneficiaryName = beneficiaryName; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getMaskedAccountNumber() { return maskedAccountNumber; }
    public void setMaskedAccountNumber(String maskedAccountNumber) { this.maskedAccountNumber = maskedAccountNumber; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
