package com.poppin.poppinserver.core.security;

import com.poppin.poppinserver.core.dto.ExceptionDto;
import com.poppin.poppinserver.core.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import net.minidev.json.JSONValue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFailureResponse {
    protected void setFailureResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("data", null);
        result.put("error", new ExceptionDto(errorCode));

        response.getWriter().write(JSONValue.toJSONString(result));
    }
}
