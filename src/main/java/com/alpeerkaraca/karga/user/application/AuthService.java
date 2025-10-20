package com.alpeerkaraca.karga.user.application;

import com.alpeerkaraca.karga.core.exception.ConflictException;
import com.alpeerkaraca.karga.core.exception.InvalidTokenException;
import com.alpeerkaraca.karga.core.exception.ResourceNotFoundException;
import com.alpeerkaraca.karga.user.domain.UserRole;
import com.alpeerkaraca.karga.user.domain.Users;
import com.alpeerkaraca.karga.user.domain.UsersRepository;
import com.alpeerkaraca.karga.user.dto.RefreshTokenRequest;
import com.alpeerkaraca.karga.user.dto.TokenPair;
import com.alpeerkaraca.karga.user.dto.UserLoginRequest;
import com.alpeerkaraca.karga.user.dto.UserRegistirationRequest;
import com.alpeerkaraca.karga.user.security.JWTService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;


    public Users RegisterUser(UserRegistirationRequest request) {
        usersRepository.findByEmail(request.email()).ifPresent(user -> {
            throw new ConflictException("Bu E-Posta Adresi Zaten Kullanılıyor.");
        });
        Users users = Users.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .role(UserRole.PASSENGER)
                .rating(0.0)
                .build();
        return usersRepository.save(users);
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

        return jwtService.generateTokenPair(authentication);
    }

    public TokenPair refreshToken(@Valid RefreshTokenRequest request) {
        String token = request.refreshToken();
        if (!jwtService.isRefreshToken(token)) {
            throw new InvalidTokenException("Bilgileriniz doğrulanamadı lütfen tekrar giriş yapınız.");
        }
        String user = jwtService.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user);
        if (user == null) {
            throw new ResourceNotFoundException("Kullanıcı bulunamadı.");
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        return jwtService.generateTokenPair(authentication);
    }
}
