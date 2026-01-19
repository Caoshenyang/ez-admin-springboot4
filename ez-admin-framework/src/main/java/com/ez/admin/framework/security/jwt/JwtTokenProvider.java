package com.ez.admin.framework.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT Token 提供者
 * <p>
 * 负责 JWT Token 的生成、解析、验证等核心操作
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Slf4j
@Component
public class JwtTokenProvider {

    /**
     * JWT 密钥（从配置文件读取）
     */
    @Value("${jwt.secret:ez-admin-secret-key-for-jwt-token-generation-please-change-in-production}")
    private String secret;

    /**
     * Access Token 默认有效期（秒），默认 15 分钟
     * -- GETTER --
     *  获取 Access Token 有效期（秒）
     *
     * @return 有效期（秒）

     */
    @Getter
    @Value("${jwt.access-token-expiration:900}")
    private Long accessTokenExpiration;

    /**
     * Refresh Token 默认有效期（秒），默认 7 天
     * -- GETTER --
     *  获取 Refresh Token 有效期（秒）
     *
     * @return 有效期（秒）

     */
    @Getter
    @Value("${jwt.refresh-token-expiration:604800}")
    private Long refreshTokenExpiration;

    /**
     * 生成 Access Token
     *
     * @param subject    主题（通常是用户ID）
     * @param claims     自定义载荷数据
     * @return JWT Token
     */
    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, accessTokenExpiration);
    }

    /**
     * 生成 Refresh Token
     *
     * @param subject    主题（通常是用户ID）
     * @param claims     自定义载荷数据
     * @return JWT Token
     */
    public String generateRefreshToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, refreshTokenExpiration);
    }

    /**
     * 生成自定义有效期的 Token
     *
     * @param subject           主题（通常是用户ID）
     * @param claims            自定义载荷数据
     * @param expirationSeconds 有效期（秒）
     * @return JWT Token
     */
    public String generateToken(String subject, Map<String, Object> claims, Long expirationSeconds) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationSeconds * 1000);

        JwtBuilder builder = Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey());

        if (claims != null && !claims.isEmpty()) {
            builder.claims(claims);
        }

        return builder.compact();
    }

    /**
     * 解析 Token
     *
     * @param token JWT Token
     * @return Claims 对象
     * @throws JwtException 解析失败时抛出
     */
    public Claims parseToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT Token
     * @return true: 有效，false: 无效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("JWT Token 已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.debug("不支持的 JWT Token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.debug("JWT Token 格式错误: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.debug("JWT Token 为空: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT Token 验证失败", e);
        }
        return false;
    }

    /**
     * 从 Token 中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 从 Token 中获取自定义声明
     *
     * @param token JWT Token
     * @param key   声明键
     * @return 声明值
     */
    public Object getClaim(String token, String key) {
        Claims claims = parseToken(token);
        return claims.get(key);
    }

    /**
     * 检查 Token 是否过期
     *
     * @param token JWT Token
     * @return true: 已过期，false: 未过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * 获取 Token 过期时间
     *
     * @param token JWT Token
     * @return 过期时间（Unix 时间戳，秒）
     */
    public Long getExpirationTime(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().getTime() / 1000;
    }

    /**
     * 获取签名密钥
     *
     * @return签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
