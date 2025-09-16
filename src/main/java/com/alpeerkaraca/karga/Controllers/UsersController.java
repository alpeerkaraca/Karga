package com.alpeerkaraca.karga.Controllers;

import com.alpeerkaraca.karga.Models.DTO.ApiResponse;
import com.alpeerkaraca.karga.Models.DTO.UserLoginRequest;
import com.alpeerkaraca.karga.Models.DTO.UserRegistirationRequest;
import com.alpeerkaraca.karga.Models.Users;
import com.alpeerkaraca.karga.Services.UserService;
import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Users>> register(@RequestBody UserRegistirationRequest request) {
        try {
            var user = userService.RegisterUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(user,"Hesabınız oluşturuldu."));
        } catch (IllegalStateException e ) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Users>> login(@RequestBody UserLoginRequest userLoginRequest) {
        try {
            if (userService.checkLogin(userLoginRequest))
                return ResponseEntity.ok(ApiResponse.success(null, "Giriş başarılı"));
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Kullanıcı adı ya da parola hatalı."));
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(e.getMessage()));
        }
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
