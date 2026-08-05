package com.example.identify_service.controller;


import com.example.identify_service.dto.request.UserCreationRequest;
import com.example.identify_service.dto.request.UserUpdateRequest;
import com.example.identify_service.dto.response.UserResponse;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class UserControllerTest {

//    Viết unit test gọi api controller create user

    @Autowired
    private MockMvc mockMvc;



    @MockitoBean
    private UserService userService;


    @Autowired
    private ObjectMapper objectMapper;

    private UserCreationRequest request;

    private UserResponse userResponse;

    private LocalDate dob;

    private UserUpdateRequest updateRequest;

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
    }

    @Test
    void createUser_validRequest_success() throws Exception {
//        Given
        String content = objectMapper.writeValueAsString(request);
//      ArgumentMatchers.any() cho phép input vào hàm createRequest
//      Line này có nghĩ là đầu vào là bất cứ cái gì chỉ cần trả ra 1 userResponse
        Mockito.when(userService.createRequest(ArgumentMatchers.any()))
                .thenReturn(userResponse);

//        When
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
//                then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value(1000)


        );

    }


    @Test
    void createUser_usernameInvalid_fail() throws Exception {
//        Given
        request.setUsername("han");
        String content = objectMapper.writeValueAsString(request);


//        When
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
//                then
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value(1003))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Username must be at least 4 characters")



                );

    }

    @Test
    @WithMockUser(username = "hoang8")
    void updateUser_validRequest_success() throws Exception
    {
//Given
        String content = objectMapper.writeValueAsString(updateRequest);
        Mockito.when(userService.updateUser(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(userResponse);

//        When
        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}", "123")
//                Cấu hình fake authentication để test các method cần authenticated
//                        .with(SecurityMockMvcRequestPostProcessors.jwt()
//                                .jwt(jwt -> jwt.subject("hoang8"))
//                        )
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)

        )

                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("username").value("hoang123"))
        ;
    }
}
