package com.ezadmin.modules.system.repository;

import com.ezadmin.modules.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 用户数据访问接口
 * 继承JpaRepository，提供基本的CRUD操作
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询用户
     * Spring Data JPA会根据方法名自动生成SQL查询
     */
    Optional<User> findByUsername(String username);
}
