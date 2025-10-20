package com.alpeerkaraca.karga.user.api;

import com.alpeerkaraca.karga.core.dto.ApiResponse;
import com.alpeerkaraca.karga.user.application.AuthService;
import com.alpeerkaraca.karga.user.application.UserService;
import com.alpeerkaraca.karga.user.domain.Users;
import com.alpeerkaraca.karga.user.dto.UserProfileResponse;
import com.alpeerkaraca.karga.user.dto.UserProfileUpdateRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final UserService userService;
    private final AuthService authService;

    public UsersController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }


    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getUser() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserProfileResponse userProfileResponse = userService.getUserInformation(email);
            return ApiResponse.success(userProfileResponse, "Bilgiler alındı");
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }

    @PutMapping("/me")
    public ApiResponse<UserProfileResponse> updateUser(@Valid @RequestBody UserProfileUpdateRequest request) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Users users = userService.getUserByEmail(email);
            users.setPhoneNumber(request.phoneNumber());
            users.setFirstName(request.firstName());
            users.setLastName(request.lastName());
            UserProfileResponse userInfo = userService.updateUser(users);
            return ApiResponse.success(userInfo,"Kullanıcı Güncellendi.");
        }
        catch (Exception e) {
            throw   new RuntimeException(e);
        }

    }
}
