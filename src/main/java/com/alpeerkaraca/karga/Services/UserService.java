package com.alpeerkaraca.karga.Services;

import com.alpeerkaraca.karga.Models.DTO.UserLoginRequest;
import com.alpeerkaraca.karga.Models.DTO.UserRegistirationRequest;
import com.alpeerkaraca.karga.Models.UserRole;
import com.alpeerkaraca.karga.Models.Users;
import com.alpeerkaraca.karga.Repositories.UsersRepository;
import org.apache.kafka.common.config.types.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    final UsersRepository usersRepository;
    final PasswordEncoder passwordEncoder;

    public UserService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public Users RegisterUser(UserRegistirationRequest request) {
        usersRepository.findByEmail(request.email()).ifPresent(user -> {
            throw new IllegalStateException("Bu E-Posta Adresi Zaten Kullanılıyor.");
        });
        Users user = new Users();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.PASSENGER);
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        user.setPhoneNumber(request.phoneNumber());
        return usersRepository.save(user);
    }
    public boolean checkLogin(UserLoginRequest request) {
        Users user = usersRepository.findByEmail(request.email()).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
        return passwordEncoder.matches(request.password(), user.getPassword());
    }

    public Users getUserByEmail(String email) {
        return usersRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
    }
}
