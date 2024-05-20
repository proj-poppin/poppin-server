package com.poppin.poppinserver.security.config;

import com.poppin.poppinserver.constant.Constant;
import com.poppin.poppinserver.security.filter.CustomLogoutFilter;
import com.poppin.poppinserver.security.filter.JwtAuthenticationFilter;
import com.poppin.poppinserver.security.filter.JwtExceptionFilter;
import com.poppin.poppinserver.security.handler.CustomSignOutProcessHandler;
import com.poppin.poppinserver.security.handler.CustomSignOutResultHandler;
import com.poppin.poppinserver.security.provider.JwtAuthenticationProvider;
import com.poppin.poppinserver.security.service.CustomUserDetailsService;
import com.poppin.poppinserver.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomSignOutProcessHandler customSignOutProcessHandler;
    private final CustomSignOutResultHandler customSignOutResultHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                .requestMatchers(Constant.NO_NEED_AUTH_URLS.toArray(new String[0])).permitAll()
                                .anyRequest().authenticated())
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(configurer ->
                        configurer
                                .logoutUrl("/api/v1/auth/sign-out")
                                .addLogoutHandler(customSignOutProcessHandler)
                                .logoutSuccessHandler(customSignOutResultHandler)
                                .deleteCookies(Constant.AUTHORIZATION_HEADER, Constant.REAUTHORIZATION))
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, new JwtAuthenticationProvider(customUserDetailsService, bCryptPasswordEncoder)), LogoutFilter.class)
                .addFilterBefore(new JwtExceptionFilter(), JwtAuthenticationFilter.class)
                .addFilterBefore(new CustomLogoutFilter(), LogoutFilter.class)
                .build();
    }
}
