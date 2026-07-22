package com.example.identify_service.service;

import com.example.identify_service.dto.request.UserCreationRequest;
import com.example.identify_service.dto.request.UserUpdateRequest;
import com.example.identify_service.dto.response.UserResponse;
import com.example.identify_service.entity.User;
import com.example.identify_service.exception.AppException;
import com.example.identify_service.exception.ErrorCode;
import com.example.identify_service.mapper.UserMapper;
import com.example.identify_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;

    public User createRequest(UserCreationRequest request)
    {


        if(userRepository.existsByUsername(request.getUsername()))
           throw new AppException(ErrorCode.USER_EXISTED);


        User user = userMapper.toUser(request);
        userRepository.save(user);
        return user;
    }

    public UserResponse updateUser(String userId,UserUpdateRequest request)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(user,request);
        return userMapper.toUserResponse(userRepository.save(user));

    }

    public void deleteUser(String userId)
    {
        userRepository.deleteById(userId);
    }

    public List<UserResponse> getUsers()
    {
        return this.userRepository.findAll()
                .stream()
                .map(user -> userMapper.toUserResponse(user))
                .toList();
    }

    public UserResponse getUser(String userId)
    {
        return userMapper.toUserResponse(this.userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

}
