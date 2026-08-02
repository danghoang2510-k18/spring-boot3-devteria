package com.example.identify_service.dto.request;

import com.example.identify_service.validator.dob.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

//    Còn nhiều annotation khác ==> cần tìm hiểu
//    Có thể custom validation ==>tìm hiểu
    @Size(min = 3 , message = "USERNAME_INVALID")
    String username;

    @Size(min = 6 , message = "PASSWORD_INVALID")
    String password;
    String firstName;
    String lastName;


    @DobConstraint(min = 16,message = "INVALID_DOB")
    LocalDate dob;


}
