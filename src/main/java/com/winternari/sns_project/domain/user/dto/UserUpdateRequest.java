package com.winternari.sns_project.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    // 수정할 username, 선택적 필드로 빈 문자열 들어올 수 있으니 @NotBlank는 생략 가능
    private String username;

    private String profileImage;
}
