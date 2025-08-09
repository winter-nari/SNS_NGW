package com.winternari.sns_project.domain.user.service;

import com.winternari.sns_project.domain.user.dto.SignUpRequest;
import com.winternari.sns_project.domain.user.dto.UserResponseDto;
import com.winternari.sns_project.domain.user.dto.UserUpdateRequest;
import com.winternari.sns_project.domain.user.entity.User;
import com.winternari.sns_project.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public UserResponseDto signup(SignUpRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
                });

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .build();

        User savedUser = userRepository.save(user);
        return UserResponseDto.fromEntity(savedUser);
    }

    // 이메일로 사용자 조회
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 사용자 정보 업데이트
    public UserResponseDto updateUser(Long id, UserUpdateRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. id: " + id));

        if (request.getUsername() != null) {
            existingUser.setUsername(request.getUsername());
        }
        if (request.getProfileImage() != null) {
            existingUser.setProfileImage(request.getProfileImage());
        }

        User updatedUser = userRepository.save(existingUser);
        return UserResponseDto.fromEntity(updatedUser);
    }

    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow( () -> new RuntimeException("해당 이메일의 사용자를 찾을 수 없습니다. " + email));
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getProfileImage()
        );

    }


    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/profile_images/";

    public UserResponseDto updateProfileImage(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new RuntimeException("잘못된 파일입니다.");
        }

        // 업로드 폴더 생성
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (Files.notExists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 파일 이름 중복 방지용 UUID + 원래 확장자
        String ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String newFilename = java.util.UUID.randomUUID().toString() + ext;

        // 파일 저장
        Path filePath = uploadPath.resolve(newFilename);
        file.transferTo(filePath.toFile());

        // User 엔티티에 경로 저장 (ex : 상대경로 or 절대경로)
        user.setProfileImage("/uploads/profile_images/" + newFilename);
        userRepository.save(user);

        return UserResponseDto.fromEntity(user);
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. id: " + id));
        return UserResponseDto.fromEntity(user);
    }

}
