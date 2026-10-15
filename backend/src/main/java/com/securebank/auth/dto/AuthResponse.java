package com.securebank.auth.dto;

public class AuthResponse {

    private String token;
    private String customerId; // username
    private String fullName;
    private String email;
    private String mobile;
    private String role;
    private Long internalCustomerId;

    public AuthResponse() {}

    public AuthResponse(String token, String customerId, String fullName, String email, String mobile, String role, Long internalCustomerId) {
        this.token = token;
        this.customerId = customerId;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.role = role;
        this.internalCustomerId = internalCustomerId;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getInternalCustomerId() { return internalCustomerId; }
    public void setInternalCustomerId(Long internalCustomerId) { this.internalCustomerId = internalCustomerId; }
}
