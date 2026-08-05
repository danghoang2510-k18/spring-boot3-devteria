package com.example.identify_service.controller;


import com.example.identify_service.dto.request.UserCreationRequest;
import com.example.identify_service.dto.request.UserUpdateRequest;
import com.example.identify_service.dto.response.UserResponse;
import com.example.identify_service.entity.Role;
import com.example.identify_service.entity.User;
import com.example.identify_service.repository.RoleRepository;
import com.example.identify_service.repository.UserRepository;
import com.example.identify_service.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;


import java.time.LocalDate;

import java.util.HashSet;
import java.util.List;
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class UserControllerIntegrationTest {

//    Cấu hình này giúp mỗi lần chạy test thì sẽ khởi tạo container mới lên
    @Container
    static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer("mysql:latest");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry)
    {
        registry.add("spring.datasource.url",()->MY_SQL_CONTAINER.getJdbcUrl());
        registry.add("spring.datasource.username",()->MY_SQL_CONTAINER.getUsername());
        registry.add("spring.datasource.password",()->MY_SQL_CONTAINER.getPassword());
        registry.add("spring.datasource.driverClassName",()->MY_SQL_CONTAINER.getDriverClassName());
        registry.add("spring.jpa.hibernate.ddl-auto",()->"update");
    }

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    private UserCreationRequest request;

    private UserResponse userResponse;

    private LocalDate dob;

    private UserUpdateRequest updateRequest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private String userId;

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

        updateRequest = UserUpdateRequest.builder()
                .password("12345678")
                .firstName("Hoang")
                .lastName("Dang")
                .dob(dob)
                .roles(List.of("USERS"))
                .build();

        Role userAdmin = Role.builder()
                .name("ADMIN")
                .build();
        roleRepository.save(userAdmin);

        User savedUser = userRepository.save(User.builder()
                .username("hoangdang123")
                .password("12345678")
                .roles(new HashSet<Role>(List.of(userAdmin)))
                .build());

        userId = savedUser.getId();
    }



    @Test
    void createUser_validRequest_success() throws Exception {

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
//                then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value(1000)

                )
                .andExpect(MockMvcResultMatchers.jsonPath("result.username").value("hoang123"));

    }

    @Test
    @WithMockUser(username = "hoang8")
    void updateUser_validRequest_success() throws Exception
    {
//Given
        String content = objectMapper.writeValueAsString(updateRequest);


//        When
        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}", userId)

                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)

                )

                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("username").value("hoangdang123"))
        ;
    }
}
