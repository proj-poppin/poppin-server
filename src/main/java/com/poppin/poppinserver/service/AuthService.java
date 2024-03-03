package com.poppin.poppinserver.service;

import com.poppin.poppinserver.constant.Constant;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.auth.request.AuthSignUpDto;
import com.poppin.poppinserver.dto.auth.response.JwtTokenDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.oauth.OAuth2UserInfo;
import com.poppin.poppinserver.repository.UserRepository;
import com.poppin.poppinserver.type.ELoginProvider;
import com.poppin.poppinserver.type.EUserRole;
import com.poppin.poppinserver.util.JwtUtil;
import com.poppin.poppinserver.util.OAuth2Util;
import com.poppin.poppinserver.util.PasswordUtil;
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
    private final PasswordUtil passwordUtil;
    private final OAuth2Util oAuth2Util;

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
        String token = accessToken.substring(Constant.BEARER_PREFIX.length());
        log.info("token: " + token);

        OAuth2UserInfo oAuth2UserInfoDto = oAuth2Util.getKakaoUserInfo(token);
        JwtTokenDto jwtTokenDto = null;

        // USER 권한 + 이메일 정보가 DB에 존재 -> 팝핀 토큰 발급 및 로그인 상태 변경
        if (userRepository.findByEmailAndRole(oAuth2UserInfoDto.email(), EUserRole.USER).isPresent()) {
            jwtTokenDto = jwtUtil.generateToken(oAuth2UserInfoDto.email(), EUserRole.USER);
            userRepository.updateRefreshTokenAndLoginStatus(oAuth2UserInfoDto.email(), jwtTokenDto.refreshToken(), true);
        } else {
            // 비밀번호 랜덤 생성 후 암호화해서 DB에 저장
            userRepository.findByEmail(oAuth2UserInfoDto.email())
                    .orElseGet(() -> userRepository.save(
                            User.toGuestEntity(oAuth2UserInfoDto,
                                    bCryptPasswordEncoder.encode(passwordUtil.generateRandomPassword()),
                                    ELoginProvider.KAKAO))
                    );
            // 유저에게 GUEST 권한 주기
            jwtTokenDto = jwtUtil.generateToken(oAuth2UserInfoDto.email(), EUserRole.GUEST);
            // 유저에게 refreshToken 발급, 로그인 상태 변경
            userRepository.updateRefreshTokenAndLoginStatus(oAuth2UserInfoDto.email(), jwtTokenDto.refreshToken(), true);
        }

        return jwtTokenDto;
    }
}
