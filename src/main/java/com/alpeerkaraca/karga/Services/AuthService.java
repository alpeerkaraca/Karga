package com.alpeerkaraca.karga.Services;

import com.alpeerkaraca.karga.DTO.RefreshTokenRequest;
import com.alpeerkaraca.karga.DTO.TokenPair;
import com.alpeerkaraca.karga.DTO.UserLoginRequest;
import com.alpeerkaraca.karga.DTO.UserRegistirationRequest;
import com.alpeerkaraca.karga.Exceptions.ConflictException;
import com.alpeerkaraca.karga.Exceptions.ResourceNotFoundException;
import com.alpeerkaraca.karga.Models.UserRole;
import com.alpeerkaraca.karga.Models.Users;
import com.alpeerkaraca.karga.Repositories.UsersRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jWTService;
    private final UserDetailsService userDetailsService;


    public Users RegisterUser(UserRegistirationRequest request) {
        usersRepository.findByEmail(request.email()).ifPresent(user -> {
            throw new ConflictException("Bu E-Posta Adresi Zaten Kullanılıyor.");
        });
        Users user = Users.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .role(UserRole.PASSENGER)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .rating(0.0)
                .build();
        return usersRepository.save(user);
    }
    public TokenPair login(UserLoginRequest request) {
        var user =  usersRepository.findByEmail(request.email()).orElseThrow( () -> new ResourceNotFoundException("E-posta ya da şifre yanlış."));
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jWTService.generateTokenPair(authentication);
    }

    public TokenPair refreshToken(@Valid RefreshTokenRequest request) {
        String token = request.refreshToken();
        if (!jWTService.isRefreshToken(token)) {
            throw new IllegalArgumentException("Geçersiz refresh token.");
        }
        String user = jWTService.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user);
        if (user == null) {
            throw new ResourceNotFoundException("Kullanıcı bulunamadı.");
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        return jWTService.generateTokenPair(authentication);

    }
}
