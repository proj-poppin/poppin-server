package com.poppin.poppinserver.core.security.config;

import com.poppin.poppinserver.core.constant.Constants;
import com.poppin.poppinserver.core.security.filter.CustomLogoutFilter;
import com.poppin.poppinserver.core.security.filter.JwtAuthenticationFilter;
import com.poppin.poppinserver.core.security.filter.JwtExceptionFilter;
import com.poppin.poppinserver.core.security.handler.CustomSignOutProcessHandler;
import com.poppin.poppinserver.core.security.handler.CustomSignOutResultHandler;
import com.poppin.poppinserver.core.security.handler.JwtAccessDeniedHandler;
import com.poppin.poppinserver.core.security.handler.JwtAuthEntryPoint;
import com.poppin.poppinserver.core.security.manager.CustomAuthenticationManager;
import com.poppin.poppinserver.core.security.provider.JwtAuthenticationProvider;
import com.poppin.poppinserver.core.security.provider.UsernamePasswordAuthenticationProvider;
import com.poppin.poppinserver.core.util.JwtUtil;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;
    private final CustomSignOutProcessHandler customSignOutProcessHandler;
    private final CustomSignOutResultHandler customSignOutResultHandler;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public AuthenticationManager authenticationManager() {
        return new CustomAuthenticationManager(Arrays.asList(
                usernamePasswordAuthenticationProvider,
                jwtAuthenticationProvider
        ));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(Constants.NO_NEED_AUTH_URLS.toArray(new String[0])).permitAll()
                                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated())
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling((exceptionHandling) ->
                        exceptionHandling
                                .authenticationEntryPoint(jwtAuthEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .logout(configurer ->
                        configurer
                                .logoutUrl("/api/v1/auth/sign-out")
                                .addLogoutHandler(customSignOutProcessHandler)
                                .logoutSuccessHandler(customSignOutResultHandler)
                                .deleteCookies(Constants.AUTHORIZATION_HEADER, Constants.REAUTHORIZATION))
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, authenticationManager()), LogoutFilter.class)
                .addFilterAfter(new JwtExceptionFilter(), JwtAuthenticationFilter.class)
                .addFilterBefore(new CustomLogoutFilter(), LogoutFilter.class)
                .build();
    }
}
