package com.poppin.poppinserver.core.exception;


import com.poppin.poppinserver.core.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

// 에러 터지면 담당해줄 클래스 얘한테 넘겨서 에러처리 하면 됌
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 지원되지 않는 HTTP 메소드를 사용할 때 발생하는 예외
    @ExceptionHandler(value = {NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseDto<?> handleNoPageFoundException(Exception e) {
        log.error("handleNoPageFoundException() in GlobalExceptionHandler throw NoHandlerFoundException : {}", e.getMessage());
        return ResponseDto.fail(new CommonException(ErrorCode.NOT_FOUND_END_POINT));
    }

    // @Validated 어노테이션을 사용하여 검증을 수행할 때 발생하는 예외
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseDto<?> handleArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleArgumentNotValidException() in GlobalExceptionHandler throw MethodArgumentNotValidException : {}", e.getMessage());
        return ResponseDto.fail(e);
    }

    // 메소드의 인자 타입이 일치하지 않을 때 발생하는 예외
    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    public ResponseDto<?> handleArgumentNotValidException(MethodArgumentTypeMismatchException e) {
        log.error("handleArgumentNotValidException() in GlobalExceptionHandler throw MethodArgumentTypeMismatchException : {}", e.getMessage());
        return ResponseDto.fail(e);
    }

    // 필수 파라미터가 누락되었을 때 발생하는 예외
    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public ResponseDto<?> handleServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("handleArgumentNotValidException() in GlobalExceptionHandler throw MissingServletRequestParameterException : {}", e.getMessage());
        return ResponseDto.fail(e);
    }

    // 필수 RequestPart가 누락되었을 때 발생하는 예외
    @ExceptionHandler(value = {MissingServletRequestPartException.class})
    public ResponseDto<?> handleServletRequestParameterException(MissingServletRequestPartException e) {
        log.error("handleArgumentNotValidException() in GlobalExceptionHandler throw MissingServletRequestPartException : {}", e.getMessage());
        return ResponseDto.fail(new CommonException(ErrorCode.MISSING_REQUEST_PARAMETER));
    }

    // post 요청 body가 없을 때 발생하는 에러
    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseDto<?> handleMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("handleArgumentNotValidException() in GlobalExceptionHandler throw HttpMessageNotReadableException : {}", e.getMessage());
        return ResponseDto.fail(e);
    }

    // 개발자가 직접 정의한 예외
    @ExceptionHandler(value = {CommonException.class})
    public ResponseDto<?> handleApiException(CommonException e) {
        log.error("handleApiException() in GlobalExceptionHandler throw CommonException : {}", e.getMessage());
        return ResponseDto.fail(e);
    }

    // 서버, DB 예외
    @ExceptionHandler(value = {Exception.class})
    public ResponseDto<?> handleException(Exception e) {
        log.error("handleException() in GlobalExceptionHandle throw Exception : {}");
        e.printStackTrace();
        return ResponseDto.fail(new CommonException(ErrorCode.SERVER_ERROR));
    }
}
