package com.alpeerkaraca.karga.Controllers;

import com.alpeerkaraca.karga.DTO.ApiResponse;
import com.alpeerkaraca.karga.Models.Users;
import com.alpeerkaraca.karga.Services.AuthService;
import com.alpeerkaraca.karga.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final UserService userService;
    private final AuthService authService;

    public UsersController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }


    @GetMapping("/get/{email}")
    public ResponseEntity<ApiResponse<Users>> getUser(@PathVariable String email) {
        try {
            var user = userService.getUserByEmail(email);
            return ResponseEntity.ok(ApiResponse.success(user, "Kullanıcı verileri başarıyla alındı."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }
}
