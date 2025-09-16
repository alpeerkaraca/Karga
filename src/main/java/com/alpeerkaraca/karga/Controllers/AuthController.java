package com.alpeerkaraca.karga.Controllers;

import com.alpeerkaraca.karga.DTO.*;
import com.alpeerkaraca.karga.Models.Users;
import com.alpeerkaraca.karga.Services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Users>> register(@Valid @RequestBody UserRegistirationRequest request) {
        try {
            var user = authService.RegisterUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(user,"Hesabınız oluşturuldu."));
        } catch (IllegalStateException e ) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenPair>> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        TokenPair tokenPair = authService.login(userLoginRequest);
        return ResponseEntity.ok(ApiResponse.success(tokenPair, "Giriş başarılı."));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenPair>> refreshToken(@Valid @RequestBody RefreshTokenRequest request ) {
        TokenPair tokenPair = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(tokenPair, "Token bilgileriniz güncellendi."));
    }
}
