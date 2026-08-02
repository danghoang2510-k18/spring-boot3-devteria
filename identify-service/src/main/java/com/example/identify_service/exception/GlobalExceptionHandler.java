package com.example.identify_service.exception;



import com.example.identify_service.dto.request.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Objects;

//Khai báo annotation này để spring biết khi có 1 exception xảy ra
//thì class này sẽ chịu trách nhiệm và handling các exception
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";

//    RuntimeException là gì, tìm hiểu các kiểu exception
//    Tìm hiểu các dạng của ResponEntity
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception)
    {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception)
    {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception)
    {
        String enumKey = exception.getFieldError().getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String,Object> attributes = null;

        try {
           errorCode = ErrorCode.valueOf(enumKey.trim());

           var contrainViolation = exception.getBindingResult()
            .getAllErrors().getFirst().unwrap(ConstraintViolation.class);

           attributes = contrainViolation.getConstraintDescriptor().getAttributes();


           log.warn(attributes.get("min").toString());

        }
        catch (IllegalArgumentException e)
        {

        }
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(Objects.nonNull(attributes)
                    ? mapAttribute(errorCode.getMessage(),attributes)
                    : errorCode.getMessage()

        );
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    private String mapAttribute(String message, Map<String,Object> attributes)
    {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE)) ;

        return message.replace("{" + MIN_ATTRIBUTE + "}",minValue);
    }

    @ExceptionHandler({
            AccessDeniedException.class,
            AuthorizationDeniedException.class})
    ResponseEntity<ApiResponse> hanldingAuthorizationDeniedException(Exception exception)
    {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        ApiResponse apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }


}
