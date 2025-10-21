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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfileResponse userInfo = userService.getUserInformation(email);
        return ApiResponse.success(userInfo,"Kullanıcı Bilgileri Getirildi.");
    }

    @PutMapping("/me")
    public ApiResponse<UserProfileResponse> updateUser(@Valid @RequestBody UserProfileUpdateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userService.getUserByEmail(email);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        UserProfileResponse updatedUser = userService.updateUser(user);
        return ApiResponse.success(updatedUser, "Kullanıcı Bilgileri Güncellendi.");
    }
}
