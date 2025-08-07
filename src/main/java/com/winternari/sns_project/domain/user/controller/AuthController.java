package com.winternari.sns_project.domain.user.controller;

import com.winternari.sns_project.domain.user.dto.JwtResponse;
import com.winternari.sns_project.domain.user.dto.LoginRequest;
import com.winternari.sns_project.domain.user.dto.RefreshTokenRequest;
import com.winternari.sns_project.domain.user.dto.SignUpRequest;
import com.winternari.sns_project.domain.user.entity.User;
import com.winternari.sns_project.domain.user.service.CustomUserDetailsService;
import com.winternari.sns_project.domain.user.service.UserService;
import com.winternari.sns_project.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // 인증 성공 시 UserDetails 가져오기
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // JWT 토큰 생성
            String accessToken = jwtUtil.generateToken(userDetails.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

            // 토큰 응답하기
            return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패 : 잘못된 이메일 또는 비밀번호");

        }

    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest request) {
        User savedUser = userService.signup(request);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String email = jwtUtil.extractEmail(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtUtil.isTokenValid(refreshToken, userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 리프레시 토큰");
        }

        String newAccessToken = jwtUtil.generateToken(email);
        String newRefreshToken = jwtUtil.generateRefreshToken(email);
        return ResponseEntity.ok(new JwtResponse(newAccessToken, newRefreshToken));
    }


}
