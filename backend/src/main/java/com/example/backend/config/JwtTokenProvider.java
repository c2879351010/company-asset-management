package com.example.backend.config;

import com.example.backend.entity.Users;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret:8V7r5bHkSlKLT8}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}") // 24時間
    private int jwtExpirationInMs;


/*    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }*/

    private SecretKey getSigningKey() {
        try {
            byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);

            // 使用SHA-256哈希确保固定32字节长度
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashedKey = digest.digest(keyBytes);

            return Keys.hmacShaKeyFor(hashedKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String generateToken(Users user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(user.getUserId())
                .claim("userId", user.getUserId())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

/*    // 创建JWT
    public static String createJWTBuilder(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());
        return builder.compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        // 使用HS256算法进行签名
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        // 生成密钥
        SecretKey secretKey = getSigningKey();
        // 获取当前时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        // 如果未指定有效期，则使用默认的14天
        if (ttlMillis == null) {
            ttlMillis = JwtTokenProvider.jwtExpirationInMs;
        }
        // 计算过期时间
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        // 构建JWT
        return Jwts.builder()
                .setId(uuid)  // 设置JWT的唯一标识
                .setSubject(subject)  // 设置JWT的主题
                .setIssuer("sg")  // 设置JWT的签发者
                .setIssuedAt(now)  // 设置JWT的签发时间
                .signWith(signatureAlgorithm, secretKey)  // 使用密钥进行签名
                .setExpiration(expDate);  // 设置JWT的过期时间
    }*/
}