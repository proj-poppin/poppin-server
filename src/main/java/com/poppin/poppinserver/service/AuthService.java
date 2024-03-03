package com.poppin.poppinserver.service;

import com.poppin.poppinserver.constant.Constant;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.auth.request.AuthSignUpDto;
import com.poppin.poppinserver.dto.auth.request.SocialRegisterRequestDto;
import com.poppin.poppinserver.dto.auth.response.JwtTokenDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.oauth.OAuth2UserInfo;
import com.poppin.poppinserver.oauth.apple.AppleOAuthService;
import com.poppin.poppinserver.repository.UserRepository;
import com.poppin.poppinserver.type.ELoginProvider;
import com.poppin.poppinserver.type.EUserRole;
import com.poppin.poppinserver.util.JwtUtil;
import com.poppin.poppinserver.util.OAuth2Util;
import com.poppin.poppinserver.util.PasswordUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final OAuth2Util oAuth2Util;
    private final AppleOAuthService appleOAuthService;

    public void authSignUp(AuthSignUpDto authSignUpDto) {
        // 유저 이메일 중복 확인
        userRepository.findByEmail(authSignUpDto.email())
                .ifPresent(user -> {
                    throw new CommonException(ErrorCode.DUPLICATED_SERIAL_ID);
                });
        // 비밀번호와 비밀번호 확인 일치 여부 검증
        if (!authSignUpDto.password().equals(authSignUpDto.passwordConfirm())) {
            throw new CommonException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        // 유저 닉네임 중복 확인
        userRepository.findByNickname(authSignUpDto.nickname())
                .ifPresent(user -> {
                    throw new CommonException(ErrorCode.DUPLICATED_NICKNAME);
                });
        // 유저 생성, 패스워드 암호화
        userRepository.save(User.toUserEntity(authSignUpDto, bCryptPasswordEncoder.encode(authSignUpDto.password()), ELoginProvider.DEFAULT));
    }

    public JwtTokenDto authKakaoLogin(String accessToken) {
        String token = refineToken(accessToken);
        log.info("token: " + token);
        OAuth2UserInfo oAuth2UserInfoDto = oAuth2Util.getKakaoUserInfo(token);
        return processUserLogin(oAuth2UserInfoDto, ELoginProvider.KAKAO);
    }

    public JwtTokenDto authNaverLogin(String accessToken) {
        String token = refineToken(accessToken);
        OAuth2UserInfo oAuth2UserInfoDto = oAuth2Util.getNaverUserInfo(token);
        return processUserLogin(oAuth2UserInfoDto, ELoginProvider.NAVER);
    }

    public JwtTokenDto authGoogleLogin(String accessToken) {
        String token = refineToken(accessToken);
        OAuth2UserInfo oAuth2UserInfoDto = oAuth2Util.getGoogleUserInfo(token);
        return processUserLogin(oAuth2UserInfoDto, ELoginProvider.GOOGLE);
    }

    public JwtTokenDto authAppleLogin(String idToken) {
        OAuth2UserInfo oAuth2UserInfoDto = appleOAuthService.getAppleUserInfo(idToken);
        return processUserLogin(oAuth2UserInfoDto, ELoginProvider.APPLE);
    }

    @Transactional
    public JwtTokenDto socialRegister(String accessToken, SocialRegisterRequestDto socialRegisterRequestDto) {  // 소셜 로그인 후 회원 등록 및 토큰 발급
        String token = refineToken(accessToken);

        OAuth2UserInfo oAuth2UserInfoDto = getOAuth2UserInfo(socialRegisterRequestDto, token);

        // 소셜 회원가입 시, 등록된 이메일과 provider로 유저 정보를 찾음
        User user = userRepository.findByEmailAndELoginProvider(oAuth2UserInfoDto.email(), socialRegisterRequestDto.provider())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        // 닉네임과 생년월일을 등록 -> 소셜 회원가입 완료
        user.register(socialRegisterRequestDto.nickname(), socialRegisterRequestDto.birthDate());

        final JwtTokenDto jwtTokenDto = jwtUtil.generateToken(user.getEmail(), user.getRole());
        user.updateRefreshToken(jwtTokenDto.refreshToken());

        return jwtTokenDto;
    }

    private OAuth2UserInfo getOAuth2UserInfo(SocialRegisterRequestDto request, String accessToken){
        if (request.provider().toString().equals(ELoginProvider.KAKAO.toString())){
            return oAuth2Util.getKakaoUserInfo(accessToken);
        } else if (request.provider().toString().equals(ELoginProvider.NAVER.toString())) {
            return oAuth2Util.getNaverUserInfo(accessToken);
        } else if (request.provider().toString().equals(ELoginProvider.GOOGLE.toString())) {
            return oAuth2Util.getGoogleUserInfo(accessToken);
        } else if (request.provider().toString().equals(ELoginProvider.APPLE.toString())) {
            return appleOAuthService.getAppleUserInfo(accessToken);
        }
        else {
            throw new CommonException(ErrorCode.NOT_FOUND_USER);
        }
    }

    private String refineToken(String accessToken) {
        if (accessToken.startsWith(Constant.BEARER_PREFIX)) {
            return accessToken.substring(Constant.BEARER_PREFIX.length());
        }
        else {
            return accessToken;
        }
    }

    private JwtTokenDto processUserLogin(OAuth2UserInfo oAuth2UserInfo, ELoginProvider provider) {
        JwtTokenDto jwtTokenDto;
        // USER 권한 + 이메일 정보가 DB에 존재 -> 팝핀 토큰 발급 및 로그인 상태 변경
        if (userRepository.findByEmailAndRole(oAuth2UserInfo.email(), EUserRole.USER).isPresent()) {
            jwtTokenDto = jwtUtil.generateToken(oAuth2UserInfo.email(), EUserRole.USER);
        } else {
            // 비밀번호 랜덤 생성 후 암호화해서 DB에 저장
            userRepository.findByEmail(oAuth2UserInfo.email())
                    .orElseGet(() -> userRepository.save(
                            User.toGuestEntity(oAuth2UserInfo,
                                    bCryptPasswordEncoder.encode(PasswordUtil.generateRandomPassword()),
                                    provider))
                    );
            // 유저에게 GUEST 권한 주기
            jwtTokenDto = jwtUtil.generateToken(oAuth2UserInfo.email(), EUserRole.GUEST);
        }
        // 유저에게 refreshToken 발급, 로그인 상태 변경
        userRepository.updateRefreshTokenAndLoginStatus(oAuth2UserInfo.email(), jwtTokenDto.refreshToken(), true);
        return jwtTokenDto;
    }
}
