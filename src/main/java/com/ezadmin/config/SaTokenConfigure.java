package com.ezadmin.config;

import cn.dev33.satoken.stp.StpInterface;
import com.ezadmin.common.auth.StpInterfaceImpl;
import com.ezadmin.common.component.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SaTokenConfigure {

    private final RedisCache redisCache;

    @Bean
    public StpInterface stpInterface() {
        return new StpInterfaceImpl(redisCache);
    }
}
