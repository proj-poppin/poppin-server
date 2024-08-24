package com.poppin.poppinserver.core.interceptor.post;

import com.poppin.poppinserver.core.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "com.poppin.poppinserver.controller")
@Slf4j
public class PoppinResponseInterceptor implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        log.info("supports returnType : {}", returnType);
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        log.info("beforeBodyWrite body : {}", body);
        if (returnType.getParameterType() == ResponseDto.class) {
            ResponseDto<?> responseDto = (ResponseDto<?>) body;
            response.setStatusCode(responseDto.httpStatus());
        }

        return body;
    }
}
