package com.example.identify_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,"uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid message key",HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed",HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003,"Username must be at least {min} characters",HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004,"Password must be at least {min} character",HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005,"User not existed",HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006,"Unauthenticated",HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007,"Unauthorization",HttpStatus.FORBIDDEN),
    INVALID_DOB(1008,"Your age must be at least {min}",HttpStatus.BAD_REQUEST);

    ;

    ErrorCode(int code, String message,HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = httpStatusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;


}
