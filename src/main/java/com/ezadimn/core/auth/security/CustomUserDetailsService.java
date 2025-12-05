package com.ezadimn.core.auth.security;

import com.ezadimn.modules.system.entity.User;
import com.ezadimn.modules.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 自定义用户详情服务
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public @NullMarked UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库查找用户，找不到则抛出异常
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
        // 返回的 UserDetails 对象包含了用户的密码和权限信息，Spring Security 会用它进行认证
        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setUser(user);
        return customUserDetails;
    }
}
