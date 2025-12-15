package com.ezadmin.modules.system.service;

import com.ezadmin.modules.system.entity.User;
import com.ezadmin.modules.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 *
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-05 16:10:23
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;




    /**
     * 初始化用户
     */
    public void initUser() {
        User user = new User();
        user.setUsername("admin");
//        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);
    }

}
