package com.poppin.poppinserver.admin.service;

import com.poppin.poppinserver.core.constant.Constants;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.util.HeaderUtil;
import com.poppin.poppinserver.core.util.JwtUtil;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.domain.type.EUserRole;
import com.poppin.poppinserver.user.dto.auth.response.JwtTokenDto;
import com.poppin.poppinserver.user.repository.UserCommandRepository;
import com.poppin.poppinserver.user.usecase.UserCommandUseCase;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import jakarta.validation.constraints.NotNull;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminAuthService {
    private final UserQueryUseCase userQueryUseCase;
    private final UserCommandUseCase userCommandUseCase;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final UserCommandRepository userCommandRepository;

    public JwtTokenDto authSignIn(String authorizationHeader) {
        String encoded = HeaderUtil.refineHeader(authorizationHeader, Constants.BASIC_PREFIX);
        String[] decoded = new String(Base64.getDecoder().decode(encoded)).split(":");
        String email = decoded[0];
        String password = decoded[1];

        User user = userQueryUseCase.findUserByEmail(email);

        if (!user.getRole().equals(EUserRole.ADMIN)) {
            throw new CommonException(ErrorCode.ACCESS_DENIED_ERROR);
        }

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new CommonException(ErrorCode.INVALID_LOGIN);
        }

        JwtTokenDto jwtTokenDto = jwtUtil.generateToken(user.getId(), user.getRole());
        // user.updateRefreshToken(jwtTokenDto.refreshToken());
        // userCommandRepository.save(user);
        userCommandRepository.updateRefreshToken(user.getId(), jwtTokenDto.refreshToken());

        return jwtTokenDto;
    }

    @Transactional
    public JwtTokenDto refresh(@NotNull String refreshToken) {
        String token = refineToken(refreshToken);
        Long userId = jwtUtil.getUserIdFromToken(token);

        User user = userQueryUseCase.findUserById(userId);

        if (!user.getRefreshToken().equals(token)) {
            throw new CommonException(ErrorCode.INVALID_TOKEN_ERROR);
        }
        JwtTokenDto jwtTokenDto = jwtUtil.generateToken(userId, user.getRole());
        userCommandRepository.updateRefreshToken(user.getId(), jwtTokenDto.refreshToken());
        return jwtTokenDto;
    }

    private String refineToken(String accessToken) {
        if (accessToken.startsWith(Constants.BEARER_PREFIX)) {
            return accessToken.substring(Constants.BEARER_PREFIX.length());
        } else {
            return accessToken;
        }
    }
}
