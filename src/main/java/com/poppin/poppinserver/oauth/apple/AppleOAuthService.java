package com.poppin.poppinserver.oauth.apple;

import com.poppin.poppinserver.oauth.OAuth2UserInfo;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.PublicKey;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppleOAuthService {
    private final RestTemplate restTemplate;
    private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";

    private final AppleJwtParser appleJwtParser;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;

    public OAuth2UserInfo getAppleUserInfo(String idToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(idToken);
        ResponseEntity<ApplePublicKeys> applePublicKeys = restTemplate.getForEntity(APPLE_PUBLIC_KEYS_URL, ApplePublicKeys.class);
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, applePublicKeys.getBody());
        Claims claims = appleJwtParser.parseClaims(idToken, publicKey);
        return OAuth2UserInfo.of(
                claims.get("sub", String.class),
                claims.get("email", String.class)
        );
    }
}
