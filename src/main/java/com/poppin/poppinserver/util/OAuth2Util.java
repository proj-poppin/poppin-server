package com.poppin.poppinserver.util;

import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.poppin.poppinserver.constant.Constant;
import com.poppin.poppinserver.oauth.OAuth2UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class OAuth2Util {
    private final RestTemplate restTemplate = new RestTemplate();

    public OAuth2UserInfo getKakaoUserInfo(String accessToken) {

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add(Constant.AUTHORIZATION_HEADER, Constant.BEARER_PREFIX + accessToken);
        httpHeaders.add(Constant.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<?> kakaoProfileRequest = new HttpEntity<>(httpHeaders);
        log.info("kakaoProfileRequest" + kakaoProfileRequest);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("Kakao API 요청에 실패했습니다.");
        }

        JsonElement element = JsonParser.parseString(response.getBody());

        return OAuth2UserInfo.of(
                element.getAsJsonObject().get("id").getAsString(),
                element.getAsJsonObject().getAsJsonObject("kakao_account").get("email").getAsString()
        );
    }

    public OAuth2UserInfo getNaverUserInfo(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(Constant.AUTHORIZATION_HEADER, Constant.BEARER_PREFIX + accessToken);
        httpHeaders.add(Constant.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<?> naverProfileRequest = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                naverProfileRequest,
                String.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("Naver API 요청에 실패했습니다.");
        }

        JsonElement element = JsonParser.parseString(response.getBody());
        return OAuth2UserInfo.of(
                element.getAsJsonObject().getAsJsonObject("response").get("id").getAsString(),
                element.getAsJsonObject().getAsJsonObject("response").get("email").getAsString()
        );
    }

    public OAuth2UserInfo getGoogleUserInfo(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(Constant.AUTHORIZATION_HEADER, Constant.BEARER_PREFIX + accessToken);
        httpHeaders.add(Constant.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<?> googleProfileRequest = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET,
                googleProfileRequest,
                String.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("Google API 요청에 실패했습니다.");
        }

        JsonElement element = JsonParser.parseString(response.getBody());
        return OAuth2UserInfo.of(
                element.getAsJsonObject().get("sub").getAsString(),
                element.getAsJsonObject().get("email").getAsString()
        );
    }

//    // Apple의 공개키 받아오기
//    public OAuth2UserInfo getAppleUserInfo(String accessToken) {
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add(Constant.AUTHORIZATION_HEADER, Constant.BEARER_PREFIX + accessToken);
//        httpHeaders.add(Constant.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");
//
//        HttpEntity<?> appleProfileRequest = new HttpEntity<>(httpHeaders);
//        ResponseEntity<String> response = restTemplate.exchange(
//                "https://appleid.apple.com/auth/keys",
//                HttpMethod.GET,
//                appleProfileRequest,
//                String.class
//        );
//        JsonElement element = JsonParser.parseString(response.getBody());
//        return OAuth2UserInfo.of(element.getAsJsonObject().get("email").getAsString());
//    }
}
