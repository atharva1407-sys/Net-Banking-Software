package com.securebank.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Customer ID / User ID is required")
    private String customerId;

    @NotBlank(message = "Password is required")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String customerId, String password) {
        this.customerId = customerId;
        this.password = password;
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
