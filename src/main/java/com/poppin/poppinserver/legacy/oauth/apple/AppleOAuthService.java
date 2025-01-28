package com.poppin.poppinserver.legacy.oauth.apple;

import com.poppin.poppinserver.user.dto.auth.response.OAuth2UserInfo;
import io.jsonwebtoken.Claims;
import java.security.PublicKey;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AppleOAuthService {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";

    private final AppleJwtParser appleJwtParser;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;

    public OAuth2UserInfo getAppleUserInfo(String idToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(idToken);
        ResponseEntity<ApplePublicKeys> applePublicKeys = restTemplate.getForEntity(APPLE_PUBLIC_KEYS_URL,
                ApplePublicKeys.class);
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, applePublicKeys.getBody());
        Claims claims = appleJwtParser.parseClaims(idToken, publicKey);
        return OAuth2UserInfo.of(
                claims.get("sub", String.class),
                claims.get("email", String.class)
        );
    }
}
