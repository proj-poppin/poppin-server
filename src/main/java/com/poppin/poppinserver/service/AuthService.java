package com.poppin.poppinserver.service;

import com.poppin.poppinserver.constant.Constant;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.auth.request.*;
import com.poppin.poppinserver.dto.auth.response.AccessTokenDto;
import com.poppin.poppinserver.dto.auth.response.EmailResponseDto;
import com.poppin.poppinserver.dto.auth.response.JwtTokenDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.oauth.OAuth2UserInfo;
import com.poppin.poppinserver.oauth.apple.AppleOAuthService;
import com.poppin.poppinserver.repository.UserRepository;
import com.poppin.poppinserver.type.ELoginProvider;
import com.poppin.poppinserver.type.EUserRole;
import com.poppin.poppinserver.util.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final OAuth2Util oAuth2Util;
    private final AppleOAuthService appleOAuthService;
    private final MailService mailService;

    public JwtTokenDto authSignUp(AuthSignUpDto authSignUpDto) {
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
        User newUser = userRepository.save(User.toUserEntity(authSignUpDto, bCryptPasswordEncoder.encode(authSignUpDto.password()), ELoginProvider.DEFAULT));

        // 회원 가입 후 바로 로그인 상태로 변경
        JwtTokenDto jwtToken = jwtUtil.generateToken(newUser.getId(), EUserRole.USER);
        userRepository.updateRefreshTokenAndLoginStatus(newUser.getId(), jwtToken.refreshToken(), true);
        return jwtToken;
    }

    public Object authSocialLogin(String token, String provider) {
        String accessToken = refineToken(token);
        String loginProvider = provider.toUpperCase();
        log.info("loginProvider : " + loginProvider);
        OAuth2UserInfo oAuth2UserInfoDto = getOAuth2UserInfo(loginProvider, accessToken);
        return processUserLogin(oAuth2UserInfoDto, ELoginProvider.valueOf(loginProvider));
    }

    @Transactional
    public JwtTokenDto socialRegister(String accessToken, SocialRegisterRequestDto socialRegisterRequestDto) {  // 소셜 로그인 후 회원 등록 및 토큰 발급
        String token = refineToken(accessToken);    // poppin access token

        Long userId = jwtUtil.getUserIdFromToken(token);    // 토큰으로부터 id 추출

        // 소셜 회원가입 시, id와 provider로 유저 정보를 찾음
        User user = userRepository.findByIdAndELoginProvider(userId, socialRegisterRequestDto.provider())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        // 닉네임과 생년월일을 등록 -> 소셜 회원가입 완료
        user.register(socialRegisterRequestDto.nickname(), socialRegisterRequestDto.birthDate());

        final JwtTokenDto jwtTokenDto = jwtUtil.generateToken(user.getId(), user.getRole());
        user.updateRefreshToken(jwtTokenDto.refreshToken());

        return jwtTokenDto;
    }

    private OAuth2UserInfo getOAuth2UserInfo(String provider, String accessToken){
        if (provider.equals(ELoginProvider.KAKAO.toString())){
            return oAuth2Util.getKakaoUserInfo(accessToken);
        } else if (provider.equals(ELoginProvider.NAVER.toString())) {
            return oAuth2Util.getNaverUserInfo(accessToken);
        } else if (provider.equals(ELoginProvider.GOOGLE.toString())) {
            return oAuth2Util.getGoogleUserInfo(accessToken);
        } else if (provider.equals(ELoginProvider.APPLE.toString())) {
            return appleOAuthService.getAppleUserInfo(accessToken);
        }
        else {
            throw new CommonException(ErrorCode.INVALID_OAUTH2_PROVIDER);
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

    private Object processUserLogin(OAuth2UserInfo oAuth2UserInfo, ELoginProvider provider) {
        Optional<User> user = userRepository.findByEmailAndRole(oAuth2UserInfo.email(), EUserRole.USER);
        // 회원 탈퇴 여부 확인
        if (user.isPresent() && user.get().getIsDeleted()) {
            throw new CommonException(ErrorCode.DELETED_USER_ERROR);
        }
        // USER 권한 + 이메일 정보가 DB에 존재 -> 팝핀 토큰 발급 및 로그인 상태 변경
        if (user.isPresent() && user.get().getProvider().equals(provider)) {
            JwtTokenDto jwtTokenDto = jwtUtil.generateToken(user.get().getId(), EUserRole.USER);
            userRepository.updateRefreshTokenAndLoginStatus(user.get().getId(), jwtTokenDto.refreshToken(), true);
            return jwtTokenDto;
        } else {
            // 비밀번호 랜덤 생성 후 암호화해서 DB에 저장
            User newUser = userRepository.findByEmail(oAuth2UserInfo.email())
                    .orElseGet(() -> userRepository.save(
                            User.toGuestEntity(oAuth2UserInfo,
                                    bCryptPasswordEncoder.encode(PasswordUtil.generateRandomPassword()),
                                    provider))
                    );
            // 유저에게 GUEST 권한 주기
            JwtTokenDto jwtTokenDto = jwtUtil.generateToken(newUser.getId(), EUserRole.GUEST);
            String accessToken = jwtTokenDto.accessToken();
            userRepository.updateRefreshTokenAndLoginStatus(newUser.getId(), jwtTokenDto.refreshToken(), true);
            return new AccessTokenDto(accessToken);
        }
    }

    @Transactional
    public void resetPassword(Long userId, PasswordUpdateDto passwordRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        if (!passwordRequestDto.password().equals(passwordRequestDto.passwordConfirm())) {
            throw new CommonException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        // 기존 쓰던 비밀번호로 설정해도 무방
        user.updatePassword(bCryptPasswordEncoder.encode(passwordRequestDto.password()));
    }

    public EmailResponseDto sendPasswordResetVerificationEmail(EmailRequestDto emailRequestDto) {
        if (!userRepository.findByEmail(emailRequestDto.email()).isPresent()) {
            throw new CommonException(ErrorCode.NOT_FOUND_USER);
        }
        String authCode = RandomCodeUtil.generateVerificationCode();
        mailService.sendEmail(emailRequestDto.email(), "[Poppin] 이메일 인증코드", authCode);
        return EmailResponseDto.builder()
                .authCode(authCode)
                .build();
    }

    public EmailResponseDto sendSignUpEmail(EmailRequestDto emailRequestDto) {
        if (userRepository.findByEmail(emailRequestDto.email()).isPresent()) {
            throw new CommonException(ErrorCode.DUPLICATED_SERIAL_ID);
        }
        String authCode = RandomCodeUtil.generateVerificationCode();
        mailService.sendEmail(emailRequestDto.email(), "[Poppin] 이메일 인증코드", authCode);
        return EmailResponseDto.builder()
                .authCode(authCode)
                .build();
    }

    public Boolean verifyPassword(Long userId, PasswordVerificationDto passwordVerificationDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        if (!bCryptPasswordEncoder.matches(passwordVerificationDto.password(), user.getPassword())) {
            throw new CommonException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        return Boolean.TRUE;
    }

    @Transactional
    public JwtTokenDto refresh(String refreshToken) {
        String token = refineToken(refreshToken);
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        if (!user.getRefreshToken().equals(token)) {
            throw new CommonException(ErrorCode.INVALID_TOKEN_ERROR);
        }
        JwtTokenDto jwtToken = jwtUtil.generateToken(userId, user.getRole());
        user.updateRefreshToken(jwtToken.refreshToken());
        return jwtToken;
    }

    public JwtTokenDto authSignIn(String authorizationHeader) {
        String encoded = HeaderUtil.refineHeader(authorizationHeader, Constant.BASIC_PREFIX);
        String[] decoded = new String(Base64.getDecoder().decode(encoded)).split(":");
        String email = decoded[0];
        String password = decoded[1];
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        if (email != user.getEmail() && !bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new CommonException(ErrorCode.INVALID_LOGIN);
        }
        JwtTokenDto jwtTokenDto = jwtUtil.generateToken(user.getId(), user.getRole());
        userRepository.updateRefreshTokenAndLoginStatus(user.getId(), jwtTokenDto.refreshToken(), true);
        return jwtTokenDto;
    }
}
