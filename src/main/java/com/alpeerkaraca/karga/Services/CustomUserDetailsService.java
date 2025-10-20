package com.alpeerkaraca.karga.Services;

import com.alpeerkaraca.karga.Models.Users;
import com.alpeerkaraca.karga.Repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = usersRepository.findByEmail(username).orElseThrow(()  -> new UsernameNotFoundException(username));
        return new org.springframework.security.core.userdetails.User(
                users.getEmail(),
                users.getPassword(),
                getAuthority(users)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthority(Users users) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_" + users.getRole().name());
        return List.of(grantedAuthority);
    }
}
