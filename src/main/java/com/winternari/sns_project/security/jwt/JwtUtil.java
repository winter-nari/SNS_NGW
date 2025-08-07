package com.winternari.sns_project.security.jwt;


import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "bbdc029fad878d39ced7b315ae896d3b4970d2012cf690755cddca4bba21d631ec6e1e7351871e73066f60e495973f10fdcf432eda4e81cc11c2ce159952ceec";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // 토큰 생성 (email 을 subject 로 넣음)
    public String generateToken(String email) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(email)              // 토큰 주제
                .setIssuedAt(new Date(now))     // 발행 시간
                .setExpiration(new Date(now + 1000 * 60 * 60)) // 발행 시간 1시간 후
                .signWith(key, SignatureAlgorithm.HS256)    // 서명에 사용할 키와 알고리즘
                .compact();
    }

    // 토큰에서 email 추출
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }


    public String generateRefreshToken(String email) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 1000 * 60 * 60 *24 * 7))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
