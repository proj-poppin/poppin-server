package com.poppin.poppinserver.security.handler;

import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.security.AbstractFailureResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler extends AbstractFailureResponse implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        setFailureResponse(response, ErrorCode.ACCESS_DENIED_ERROR);
    }
}
