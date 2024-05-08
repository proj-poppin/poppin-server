package com.poppin.poppinserver.util;

import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class HeaderUtil {
    public static String refineHeader(String header, String prefix) {
        if (!StringUtils.hasText(header) || !header.startsWith(prefix)) {
            throw new CommonException(ErrorCode.INVALID_HEADER);
        }
        return header.substring(prefix.length());
    }
}
