package com.alpeerkaraca.karga.Controllers;

import com.alpeerkaraca.karga.DTO.ApiResponse;
import com.alpeerkaraca.karga.DTO.DriverUpdateStatus;
import com.alpeerkaraca.karga.DTO.LocationUpdateRequest;
import com.alpeerkaraca.karga.Models.Users;
import com.alpeerkaraca.karga.Repositories.UsersRepository;
import com.alpeerkaraca.karga.Services.DriverLocationService;
import com.alpeerkaraca.karga.Services.DriverStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drivers")
@PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
public class DriverLocationController {

    private final DriverLocationService driverLocationService;
    private final UsersRepository usersRepository;

    @PostMapping("/location")
    public ResponseEntity<Void> updateLocation(@Valid @RequestBody LocationUpdateRequest driverUpdateStatus){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Users currentUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        driverLocationService.publishDriverLocationMessage(
                currentUser.getUserId(),
                driverUpdateStatus.latitude(),
                driverUpdateStatus.longitude()
        );
        return ResponseEntity.accepted().build();
    }
}
