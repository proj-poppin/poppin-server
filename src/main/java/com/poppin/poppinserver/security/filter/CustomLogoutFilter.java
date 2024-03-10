package com.poppin.poppinserver.security.filter;

import com.poppin.poppinserver.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.minidev.json.JSONValue;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomLogoutFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Check if it's a logout request
        if ("/api/v1/auth/sign-out".equals(httpRequest.getRequestURI()) && "POST".equalsIgnoreCase(httpRequest.getMethod())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // If the user is not authenticated, return a failure response
            if (authentication == null || !authentication.isAuthenticated()) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.setContentType("application/json;charset=UTF-8");

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", false);
                responseData.put("data", null);
                responseData.put("error", ErrorCode.FAILURE_LOGOUT.getMessage());

                httpResponse.getWriter().write(JSONValue.toJSONString(responseData));
                return; // Do not proceed with the filter chain
            }
        }
        filterChain.doFilter(request, response);
    }
}
