package com.example.identify_service.service;


import com.example.identify_service.dto.request.UserCreationRequest;
import com.example.identify_service.dto.request.UserUpdateRequest;
import com.example.identify_service.dto.response.UserResponse;
import com.example.identify_service.entity.Role;
import com.example.identify_service.entity.User;
import com.example.identify_service.exception.AppException;
import com.example.identify_service.repository.RoleRepository;
import com.example.identify_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {

//    Tiêm object thật vào object test là userService
    @Autowired
    private UserService userService;


//    Trong method cần test có sử dụng userRepositor(layer dưới ) để thực hiện
//    xử lý logic --> tiêm object giả ( test nên không được gọi thẳng tới object thật)
    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RoleRepository roleRepository;


    @Autowired
    private ObjectMapper objectMapper;

    private UserCreationRequest request;

    private UserUpdateRequest updateRequest;

    private UserResponse userResponse;

    private List<UserResponse> userResponses;

    private LocalDate dob;

    private User user;

    private Role role;


    @BeforeEach
    void initData()
    {
        dob = LocalDate.of(2005,10,25);
        request = UserCreationRequest.builder()
                .username("hoang123")
                .password("12345678910")
                .firstName("Hoang")
                .lastName("Dang")
                .dob(dob)
                .build();

        userResponse = UserResponse.builder()
                .id("123jdakald342")
                .username("hoang123")
                .firstName("Hoang")
                .lastName("Dang")
                .dob(dob)
                .build();

        user = User.builder()
                .id("123jdakald342")
                .username("hoang123")
                .firstName("Hoang")
                .lastName("Dang")
                .dob(dob)
                .build();

        updateRequest = UserUpdateRequest.builder()
                .password("12345678")
                .firstName("Hoang")
                .lastName("Dang")
                .dob(dob)
                .roles(List.of("ADMIN"))
                .build();

        role = Role.builder()
                .name("ADMIN")
                .build();

        userResponses = List.of(userResponse);


    }

    @Test
    void createUser_validRequest_success()
    {
//        Given
        Mockito.when(userRepository.existsByUsername(ArgumentMatchers.anyString()))
                .thenReturn(false);
        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(user);

//        When
        var response = userService.createRequest(request);
//      THEN

        Assertions.assertThat(response.getId()).isEqualTo("123jdakald342");
        Assertions.assertThat(response.getUsername()).isEqualTo("hoang123");
    }

    @Test
    void createUser_userExisted_fail()
    {
//        Given
        Mockito.when(userRepository.existsByUsername(ArgumentMatchers.anyString()))
                .thenReturn(true);
;

//        When
        var exception = org.junit.jupiter.api.Assertions.assertThrows(AppException.class,()-> userService.createRequest(request));
//      THEN
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1002);

    }

    @Test
    @WithMockUser(username = "hoang13")
    void getMyInfo_validRequest_success()
    {

        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(user));

        var response = userService.getMyInfo();

        Assertions.assertThat(response.getUsername()).isEqualTo("hoang123");
        Assertions.assertThat(response.getId()).isEqualTo("123jdakald342");
    }

    @Test
    @WithMockUser(username = "hoang13")
    void getMyInfo_userNotFound_fail()
    {
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.ofNullable(null));

        var exception = org.junit.jupiter.api.Assertions
                .assertThrows(AppException.class,()->userService.getMyInfo());

        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1005);
    }

    @Test
//    @WithMockUser(username = "hoang123",roles = {"ADMIN"})
    void updateUser_validRequest_success()
    {
//        Given

        Mockito.when(userRepository.findById(ArgumentMatchers.anyString()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(roleRepository.findAllById(ArgumentMatchers.any()))
                .thenReturn(
                        List.of(role)
                );

        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenReturn(user);
//        When

        var response = userService.updateUser("c62067bc-f93f-492f-b135-40b10e3b6a59",updateRequest);
//        Then
        Assertions.assertThat(response.getUsername()).isEqualTo("hoang123");

    }

    @Test
    void updateUser_userNotFound_fail()
    {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyString()))
                .thenReturn(Optional.ofNullable(null));

        var exception = org.junit.jupiter.api.Assertions.assertThrows(AppException.class,()->userService.updateUser(ArgumentMatchers.anyString(),updateRequest));

        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1005);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getUsers_validRequest_success()
    {
        Mockito.when(userRepository.findAllById(ArgumentMatchers.any()))
                .thenReturn(List.of(user));

        var response = userService.getUsers();

        Assertions.assertThat(response).isNotNull();

    }

}
