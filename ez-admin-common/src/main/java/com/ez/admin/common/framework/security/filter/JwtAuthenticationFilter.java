package com.ez.admin.common.framework.security.filter;

import com.ez.admin.framework.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 认证过滤器
 * <p>
 * 从请求头中提取 JWT Token 并验证，将认证信息设置到 SecurityContext 中
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // 从请求头中提取 JWT Token
            String token = extractTokenFromRequest(request);

            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                // 从 Token 中获取用户ID
                String userId = jwtTokenProvider.getUserIdFromToken(token);

                // 从 Token 中获取用户角色（如果有）
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) jwtTokenProvider.getClaim(token, "roles");
                List<SimpleGrantedAuthority> authorities = null;
                if (roles != null && !roles.isEmpty()) {
                    authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                }

                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 将认证信息设置到 SecurityContext 中
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT 认证成功，用户ID: {}", userId);
            }
        } catch (Exception e) {
            log.error("JWT 认证失败", e);
            // 不抛出异常，继续执行过滤器链
            // 如果 Token 无效，后续的安全拦截器会处理
        }

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取 JWT Token
     *
     * @param request HTTP 请求
     * @return JWT Token，如果不存在则返回 null
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
