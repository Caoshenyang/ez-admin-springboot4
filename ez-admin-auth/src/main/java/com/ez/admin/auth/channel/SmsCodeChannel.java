package com.ez.admin.auth.channel;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ez.admin.auth.dto.LoginRequest;
import com.ez.admin.auth.enums.ChannelType;
import com.ez.admin.common.exception.EzBusinessException;
import com.ez.admin.domain.system.entity.User;
import com.ez.admin.domain.system.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 手机验证码登录渠道
 * <p>
 * 支持手机号+验证码登录
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmsCodeChannel implements AuthenticationChannel {

    private final IUserService userService;
    private final StringRedisTemplate redisTemplate;

    private static final String CREDENTIAL_PHONE = "phone";
    private static final String CREDENTIAL_SMS_CODE = "smsCode";
    private static final String SMS_CODE_KEY_PREFIX = "sms:code:";
    private static final int SMS_CODE_EXPIRE_MINUTES = 5; // 验证码有效期5分钟

    @Override
    public ChannelType getSupportedChannel() {
        return ChannelType.SMS_CODE;
    }

    @Override
    public boolean validateCredentials(LoginRequest request) {
        String phone = request.getCredential(CREDENTIAL_PHONE);
        String smsCode = request.getCredential(CREDENTIAL_SMS_CODE);
        return phone != null && !phone.isBlank() && smsCode != null && !smsCode.isBlank();
    }

    @Override
    public String authenticate(LoginRequest request) {
        String phone = request.getCredential(CREDENTIAL_PHONE);
        String smsCode = request.getCredential(CREDENTIAL_SMS_CODE);

        // 查询用户
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhoneNumber, phone));

        if (user == null) {
            log.warn("手机验证码登录失败：手机号未注册 - {}", phone);
            throw new EzBusinessException("手机号未注册");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            log.warn("手机验证码登录失败：用户已被禁用 - {}", phone);
            throw new EzBusinessException("账号已被禁用");
        }

        // 验证短信验证码
        String key = SMS_CODE_KEY_PREFIX + phone;
        String cachedCode = redisTemplate.opsForValue().get(key);

        if (cachedCode == null) {
            log.warn("手机验证码登录失败：验证码不存在或已过期 - {}", phone);
            throw new EzBusinessException("验证码不存在或已过期");
        }

        if (!cachedCode.equals(smsCode)) {
            log.warn("手机验证码登录失败：验证码错误 - {}", phone);
            throw new EzBusinessException("验证码错误");
        }

        // 验证成功后删除验证码（一次性使用）
        redisTemplate.delete(key);

        log.info("手机验证码登录成功 - phone: {}, userId: {}", phone, user.getUserId());
        return String.valueOf(user.getUserId());
    }

    /**
     * 保存短信验证码到 Redis
     * <p>
     * 此方法由发送短信的接口调用
     * </p>
     *
     * @param phone    手机号
     * @param smsCode  验证码
     */
    public void saveSmsCode(String phone, String smsCode) {
        String key = SMS_CODE_KEY_PREFIX + phone;
        redisTemplate.opsForValue().set(key, smsCode, SMS_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        log.info("短信验证码已保存 - phone: {}, code: {}", phone, smsCode);
    }
}
