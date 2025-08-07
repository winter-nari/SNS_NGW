package com.winternari.sns_project.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SignUpRequest {
    private String email;
    private String password;
    private String username;
}
