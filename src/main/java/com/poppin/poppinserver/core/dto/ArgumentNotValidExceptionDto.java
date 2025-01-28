package com.poppin.poppinserver.core.dto;

import com.poppin.poppinserver.core.exception.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ArgumentNotValidExceptionDto extends ExceptionDto {
    private final Map<String, String> errorFields;

    public ArgumentNotValidExceptionDto(final MethodArgumentNotValidException methodArgumentNotValidException) {
        super(ErrorCode.INVALID_PARAMETER);

        this.errorFields = new HashMap<>();
        methodArgumentNotValidException.getBindingResult()
                .getAllErrors().forEach(e -> this.errorFields.put(((FieldError) e).getField(), e.getDefaultMessage()));
    }

    public ArgumentNotValidExceptionDto(final ConstraintViolationException constraintViolationException) {
        super(ErrorCode.INVALID_PARAMETER);

        this.errorFields = new HashMap<>();

        for (ConstraintViolation<?> constraintViolation : constraintViolationException.getConstraintViolations()) {
            errorFields.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
        }
    }
}