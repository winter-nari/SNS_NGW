package com.winternari.sns_project.domain.user.controller;

import com.winternari.sns_project.domain.user.dto.UserResponseDto;
import com.winternari.sns_project.domain.user.dto.UserUpdateRequest;
import com.winternari.sns_project.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponseDto userDto = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/{id}/profile-image")
    public ResponseEntity<UserResponseDto> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        UserResponseDto updatedUser = userService.updateProfileImage(id, file);
        return ResponseEntity.ok(updatedUser);
    }

    // 인증된 사용자만 프로필 이미지 조회 가능
    @GetMapping("/{id}/profile-image")
    public ResponseEntity<Resource> getProfileImage(@PathVariable Long id) throws IOException {
        UserResponseDto user = userService.getUserById(id);

        if (user.getProfileImage() == null) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = Paths.get(System.getProperty("user.dir") + user.getProfileImage());
        Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
