package com.poppin.poppinserver.interceptor.pre;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


@Component
@Slf4j
public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter = {}", parameter.getParameterType().equals(String.class) &&
                parameter.hasParameterAnnotation(UserId.class));
        return parameter.getParameterType().equals(String.class) && parameter.hasParameterAnnotation(UserId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        final Object userIdObj = webRequest.getAttribute("USER_ID", NativeWebRequest.SCOPE_REQUEST);
        if (userIdObj == null) {
            throw new CommonException(ErrorCode.ACCESS_DENIED_ERROR);
        }
        log.info("resolveArgument = {}", ((HttpServletRequest) webRequest).getAttribute("USER_ID").toString());
        return userIdObj.toString();
    }
}
