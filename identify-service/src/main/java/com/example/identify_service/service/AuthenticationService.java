package com.example.identify_service.service;


import com.example.identify_service.dto.request.AuthenticationRequest;
import com.example.identify_service.dto.request.IntrospectRequest;
import com.example.identify_service.dto.response.AuthenticationResponse;
import com.example.identify_service.dto.response.IntrospectResponse;
import com.example.identify_service.exception.AppException;
import com.example.identify_service.exception.ErrorCode;
import com.example.identify_service.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;


    public IntrospectResponse introspectResponse(IntrospectRequest request) throws
            JOSEException, ParseException {

//        Lấy token và xác minh token
        var token = request.getToken();


        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());


        SignedJWT signedJWT = SignedJWT.parse(token);

        var verified = signedJWT.verify(verifier);

//        Kiểm tra token có hết hạn hay chưa
        Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        return IntrospectResponse.builder()
                .valid(verified && expityTime.after(new Date()))
                .build();

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request)
    {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder encoder = new BCryptPasswordEncoder(10);
        boolean authenticated = encoder.matches(request.getPassword(),user.getPassword());

        if(!authenticated)
            throw  new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(request.getUsername());

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();

    }
//  Các data trong body được gọi là claim
    private String generateToken(String username)
    {
//        Tạo Header với thuật toán HS512
//        Claim subject đại diện cho user đăng nhập
//        Claim issuer để xác định token được phát hành bởi ai(thường là domain service được cấu hình)
//        Claim issueTime thời gian mà phát hành token
//        Claim expirationTime để xác định thời hạn của token
//        Cos thể custome claim khác thông qua .claim()
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("hd2005.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()

                ))
                .claim("customClaim","custom")
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header,payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }
}
