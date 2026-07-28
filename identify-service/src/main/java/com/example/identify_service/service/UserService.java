package com.example.identify_service.service;

import com.example.identify_service.dto.request.UserCreationRequest;
import com.example.identify_service.dto.request.UserUpdateRequest;
import com.example.identify_service.dto.response.UserResponse;
import com.example.identify_service.entity.User;
import com.example.identify_service.enums.Role;
import com.example.identify_service.exception.AppException;
import com.example.identify_service.exception.ErrorCode;
import com.example.identify_service.mapper.UserMapper;
import com.example.identify_service.repository.RoleRepository;
import com.example.identify_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    public UserResponse createRequest(UserCreationRequest request)
    {


        if(userRepository.existsByUsername(request.getUsername()))
           throw new AppException(ErrorCode.USER_EXISTED);


        User user = userMapper.toUser(request);


        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
//        user.setRoles(roles);


        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse updateUser(String userId,UserUpdateRequest request)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(user,request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));
        return userMapper.toUserResponse(userRepository.save(user));

    }

    public void deleteUser(String userId)
    {
        userRepository.deleteById(userId);
    }

    @PreAuthorize("hasAuthority('CREATE_DATA')")
    public List<UserResponse> getUsers()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(authentication.getName());
        return this.userRepository.findAll()
                .stream()
                .map(user -> userMapper.toUserResponse(user))
                .toList();
    }

    public UserResponse getMyInfo()
    {
        var context =SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name)
                .orElseThrow(() ->new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }
//    Sử dụng postauthorize để user khi get chỉ lấy đc thông tin chính mình
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(String userId)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(authentication.getName());
        return userMapper.toUserResponse(this.userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

}
