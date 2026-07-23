package com.example.identify_service.controller;


import com.example.identify_service.dto.request.ApiResponse;
import com.example.identify_service.dto.request.AuthenticationRequest;
import com.example.identify_service.dto.response.AuthenticationResponse;
import com.example.identify_service.service.AuthenticationService;
import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//Phục vụ việc xác minh
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

//    Xem lại --> lú
    @PostMapping("/log-in")
    ApiResponse<AuthenticationResponse> handleLogin(@RequestBody  AuthenticationRequest request)
    {
        boolean result =  authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(AuthenticationResponse.builder().authenticated(result).build())
                .build();
            }
}
