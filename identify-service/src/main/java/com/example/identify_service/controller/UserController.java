package com.example.identify_service.controller;

import com.example.identify_service.dto.request.ApiResponse;
import com.example.identify_service.dto.request.UserCreationRequest;
import com.example.identify_service.dto.request.UserUpdateRequest;
import com.example.identify_service.entity.User;
import com.example.identify_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;


//    @Valid khai báo cho framework validation cho object này dựa trên
//    các role đã được cấu hình trong object(ở đây là UserCreationRequest)
    @PostMapping
    ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request)
    {
        ApiResponse<User> apiResponse = new ApiResponse<>();

        apiResponse.setResult(userService.createRequest(request));
        return apiResponse;

    }

    @GetMapping
    List<User> getUsers()
    {
        return  userService.getUsers();
    }

    @GetMapping("/{userId}")
    User getUser(@PathVariable("userId") String userId)
    {
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    User updateUser(@PathVariable("userId") String userId,@RequestBody UserUpdateRequest request)
    {
        return  userService.updateUser(userId , request);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable("userId") String userId)
    {
        userService.deleteUser(userId);
        return "User has been deleted";
    }
}
