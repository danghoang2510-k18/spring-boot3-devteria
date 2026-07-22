package com.example.identify_service.mapper;

import com.example.identify_service.dto.request.UserCreationRequest;
import com.example.identify_service.dto.request.UserUpdateRequest;
import com.example.identify_service.dto.response.UserResponse;
import com.example.identify_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

//Khai báo kiểu mapper này kiểu spring(dependency injection)
@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    @Mapping(source = "firstName",target = "lastName")
    UserResponse toUserResponse(User user);


    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
