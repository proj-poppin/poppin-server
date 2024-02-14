package com.poppin.poppinserver.util;

import com.poppin.poppinserver.constant.Constant;
import com.poppin.poppinserver.dto.auth.response.JwtTokenDto;
import com.poppin.poppinserver.type.EUserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil implements InitializingBean {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity-in-milli-seconds}")
    private Integer accessTokenExpirationPeriod;

    @Value("${jwt.refresh-token-validity-in-milli-seconds}")
    private Integer refreshTokenExpirationPeriod;

    private Key key;

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String email, EUserRole role, Integer expirationPeriod) {
        Claims claims = Jwts.claims();
        claims.put(Constant.USER_EMAIL_CLAIM_NAME, email);
        claims.put(Constant.USER_ROLE_CLAIM_NAME, role.toString());

        Date now = new Date();
        Date tokenValidity = new Date(now.getTime() + expirationPeriod);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(tokenValidity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String createToken(Authentication authentication, Integer expirationPeriod) {
        Claims claims = Jwts.claims();
        claims.put(Constant.USER_EMAIL_CLAIM_NAME, authentication.getPrincipal());
        claims.put(Constant.USER_ROLE_CLAIM_NAME, authentication.getCredentials());

        Date now = new Date();
        Date tokenValidity = new Date(now.getTime() + expirationPeriod);    // 토큰의 만료시간 설정

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(tokenValidity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public JwtTokenDto generateToken(String email, EUserRole role) {
        return new JwtTokenDto(createToken(email, role, accessTokenExpirationPeriod), createToken(email, role, refreshTokenExpirationPeriod));
    }

    public JwtTokenDto generateToken(Authentication authentication) {
        return new JwtTokenDto(createToken(authentication, accessTokenExpirationPeriod), createToken(authentication, refreshTokenExpirationPeriod));
    }

    public Claims validateAndGetClaimsFromToken(String token) throws JwtException {
        final JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        return jwtParser.parseClaimsJws(token).getBody();
    }
}
