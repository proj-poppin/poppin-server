package com.poppin.poppinserver.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
@Slf4j
public class HeaderUtil {
    public static Optional<String> refineHeader(HttpServletRequest request, String header, String prefix) {
        String rawToken = request.getHeader(header);
        log.info(rawToken + " " + prefix);
        if (!StringUtils.hasText(rawToken) || !rawToken.startsWith(prefix)) {
            return Optional.empty();
        }
        return Optional.of(rawToken.substring(prefix.length()));
    }
}
