package com.poppin.poppinserver.user.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

// 현재는 필요 없음
@Getter
@NoArgsConstructor
@RedisHash(value = "refreshToken")
public class RefreshToken {
    @Id
    private Long userId;

    private String refreshToken;

    @TimeToLive
    private Long expiration;

    @Builder(access = AccessLevel.PRIVATE)
    public RefreshToken(Long userId, String refreshToken, Long expiration) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

    public static RefreshToken create(Long userId, String refreshToken, Long expiration) {
        return RefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .expiration(expiration)
                .build();
    }
}
