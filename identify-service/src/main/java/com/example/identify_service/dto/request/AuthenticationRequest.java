package com.example.identify_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

//Xử lý việc đang nhập khi người dùng request login với username, password
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
    String username;
    String password;
}
