package com.poppin.poppinserver.core.config;

import com.poppin.poppinserver.core.constant.Constant;
import com.poppin.poppinserver.core.interceptor.pre.UserIdArgumentResolver;
import com.poppin.poppinserver.core.interceptor.pre.UserIdInterceptor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final UserIdArgumentResolver userIdArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserIdInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns(Constant.NO_NEED_AUTH_URLS);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
        resolvers.add(userIdArgumentResolver);
    }

    // 향후 보안을 위해 삭제
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "https://www.bubble-poppin.com",
                        "http://3.36.136.115") // vite 로컬 주소, 운영 환경, 개발 환경
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
