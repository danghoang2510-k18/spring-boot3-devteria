package com.example.identify_service.service;


import com.example.identify_service.dto.request.AuthenticationRequest;
import com.example.identify_service.exception.AppException;
import com.example.identify_service.exception.ErrorCode;
import com.example.identify_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;
    public boolean authenticate(AuthenticationRequest request)
    {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder encoder = new BCryptPasswordEncoder(10);
        return encoder.matches(request.getPassword(),user.getPassword());

    }
}
