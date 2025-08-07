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
}
