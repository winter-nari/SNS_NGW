package com.winternari.sns_project.domain.user.controller;

import com.winternari.sns_project.domain.user.dto.UserResponseDto;
import com.winternari.sns_project.domain.user.dto.UserUpdateRequest;
import com.winternari.sns_project.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequest updateRequest) {
        UserResponseDto updatedUser = userService.updateUser(id, updateRequest);
        return ResponseEntity.ok(updatedUser);
    }
}
