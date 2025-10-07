package com.finance.manager.dto;

import lombok.*;

@Getter @Setter
public class SignupRequest {
    private String fullName;
    private String email;
    private String password;
}

