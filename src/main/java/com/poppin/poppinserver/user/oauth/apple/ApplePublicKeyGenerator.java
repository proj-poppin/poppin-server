package com.poppin.poppinserver.user.oauth.apple;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class ApplePublicKeyGenerator {
    private static final String ALG_HEADER_KEY = "alg";
    private static final String KID_HEADER_KEY = "kid";
    private static final int POSITIVE_SIGN_NUM = 1;

    public PublicKey generatePublicKey(Map<String, String> tokenHeaders, ApplePublicKeys applePublicKeys) {
        List<ApplePublicKey> publicKeys = applePublicKeys.keys();
        ApplePublicKey publicKey = publicKeys.stream()
                .filter(key -> key.alg().equals(tokenHeaders.get(ALG_HEADER_KEY)))
                .filter(key -> key.kid().equals(tokenHeaders.get(KID_HEADER_KEY)))
                .findFirst()
                .orElseThrow(() -> new CommonException(ErrorCode.INVALID_APPLE_PUBLIC_KEY_ERROR));

        return generatePublicKeyWithApplePublicKey(publicKey);
    }

    private PublicKey generatePublicKeyWithApplePublicKey(ApplePublicKey applePublicKey) {
        byte[] n = Base64.getUrlDecoder().decode(applePublicKey.n());
        byte[] e = Base64.getUrlDecoder().decode(applePublicKey.e());
        // 애플은 다 RSA 방식으로만 키를 생성
        RSAPublicKeySpec publicKeySpec =
                new RSAPublicKeySpec(
                        new BigInteger(POSITIVE_SIGN_NUM, n), new BigInteger(POSITIVE_SIGN_NUM, e)
                );
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(applePublicKey.kty());
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new CommonException(ErrorCode.INVALID_APPLE_PUBLIC_KEY_ERROR);
        }
    }
}
