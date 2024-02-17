package com.poppin.poppinserver.security.handler;

import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.security.AbstractFailureResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.minidev.json.JSONValue;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class DefaultSignInFailureHandler extends AbstractFailureResponse implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        setFailureResponse(response, ErrorCode.FAILURE_LOGIN);
    }
}
