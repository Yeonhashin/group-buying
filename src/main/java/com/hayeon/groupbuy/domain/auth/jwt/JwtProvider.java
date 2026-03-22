package com.hayeon.groupbuy.domain.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private final String SECRET_KEY = "mysecretjwtkeyforgroupbuyingproject2026verylongkey";
    private final long EXPIRATION = 1000 * 60 * 30; // 30분
    private final SecretKey key = Keys.hmacShaKeyFor(
            SECRET_KEY.getBytes(StandardCharsets.UTF_8)
    );

    // JWT 생성
    public String createToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(System.currentTimeMillis() + EXPIRATION);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))   // sub
                .setIssuedAt(now)                     // iat
                .setExpiration(expiry)                // exp
                .signWith(key)                        // HS256 + SecretKey
                .compact();
    }

    // JWT에서 userId 추출
    public Long getUserId(String token) {

        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    // JWT 유효성 검증
    public boolean validateToken(String token) {
        try {

            Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token);

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}