package com.poppin.poppinserver.core.security.filter;

import com.poppin.poppinserver.core.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (SecurityException e) {
            e.printStackTrace();
            log.error("FilterException throw SecurityException Exception : {}", e.getMessage());
            request.setAttribute("exception", ErrorCode.ACCESS_DENIED_ERROR);

            filterChain.doFilter(request, response);
        } catch (MalformedJwtException e) {
            e.printStackTrace();
            log.error("FilterException throw MalformedJwtException Exception : {}", e.getMessage());
            request.setAttribute("exception", ErrorCode.TOKEN_MALFORMED_ERROR);

            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            log.error("FilterException throw IllegalArgumentException Exception : {}", e.getMessage());
            request.setAttribute("exception", ErrorCode.TOKEN_TYPE_ERROR);

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            log.error("FilterException throw ExpiredJwtException Exception : {}", e.getMessage());
            request.setAttribute("exception", ErrorCode.EXPIRED_TOKEN_ERROR);

            filterChain.doFilter(request, response);
        } catch (UnsupportedJwtException e) {
            e.printStackTrace();
            log.error("FilterException throw UnsupportedJwtException Exception : {}", e.getMessage());
            request.setAttribute("exception", ErrorCode.TOKEN_UNSUPPORTED_ERROR);

            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            e.printStackTrace();
            log.error("FilterException throw JwtException Exception : {}", e.getMessage());
            request.setAttribute("exception", ErrorCode.TOKEN_UNKNOWN_ERROR);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("FilterException throw Exception Exception : {}", e.getMessage());
            request.setAttribute("exception", ErrorCode.NOT_FOUND_USER);

            filterChain.doFilter(request, response);
        }
    }
}
