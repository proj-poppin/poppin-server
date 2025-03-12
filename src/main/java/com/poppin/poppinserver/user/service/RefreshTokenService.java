package com.poppin.poppinserver.user.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-token-validity-in-milli-seconds}")
    private Long refreshTokenValidityInMilliseconds;

    // refresh token 저장
    public void saveRefreshToken(Long userId, String refreshToken) {
        redisTemplate.opsForValue()
                .set(String.valueOf(userId), refreshToken, Duration.ofMillis(refreshTokenValidityInMilliseconds));
    }

    // refresh token 조회
    public String getRefreshTokenByUserId(Long userId) {
        return redisTemplate.opsForValue().get(String.valueOf(userId));
    }

    // refresh token 삭제
    public void deleteRefreshTokenByUserId(Long userId) {
        redisTemplate.delete(String.valueOf(userId));
    }
}
