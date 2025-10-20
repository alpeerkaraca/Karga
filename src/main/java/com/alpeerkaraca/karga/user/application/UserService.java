package com.alpeerkaraca.karga.user.application;

import com.alpeerkaraca.karga.user.domain.Users;
import com.alpeerkaraca.karga.user.domain.UsersRepository;
import com.alpeerkaraca.karga.user.dto.UserProfileResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    final UsersRepository usersRepository;
    final PasswordEncoder passwordEncoder;

    public UserService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users getUserByEmail(String email) {
        return usersRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
    }
    public UserProfileResponse updateUser(Users users) {
        usersRepository.save(users);
        return new UserProfileResponse(users.getFirstName(), users.getLastName(), users.getPhoneNumber(), users.getEmail());
    }
    public UserProfileResponse getUserInformation(String email) {
        Users users =  getUserByEmail(email);
        return new UserProfileResponse(users.getFirstName(), users.getLastName(), users.getPhoneNumber(), users.getEmail());
    }
}
