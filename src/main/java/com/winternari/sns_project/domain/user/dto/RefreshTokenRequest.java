package com.winternari.sns_project.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {

    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;
}
