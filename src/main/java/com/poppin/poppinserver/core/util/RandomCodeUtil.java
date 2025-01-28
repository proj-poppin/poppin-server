package com.poppin.poppinserver.core.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomCodeUtil {
    /**
     * 6자리 랜덤 난수를 생성하여 반환합니다.
     *
     * @return 생성된 6자리 난수 문자열
     */
    public static String generateVerificationCode() {
        // 100000 (6자리 최소값)부터 999999 (6자리 최대값) 사이의 랜덤 수를 생성
        // ThreadLocalRandom을 사용하여 멀티스레드 환경에서 안전하게 랜덤 수를 생성
        int verificationCode = ThreadLocalRandom.current().nextInt(100000, 1000000);
        // 생성된 랜덤 수를 문자열로 변환하여 반환
        return String.valueOf(verificationCode);
    }
}
