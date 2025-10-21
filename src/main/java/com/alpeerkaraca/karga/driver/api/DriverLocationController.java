package com.alpeerkaraca.karga.driver.api;

import com.alpeerkaraca.karga.driver.application.DriverLocationService;
import com.alpeerkaraca.karga.driver.dto.LocationUpdateRequest;
import com.alpeerkaraca.karga.user.application.UserService;
import com.alpeerkaraca.karga.user.domain.Users;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drivers")
public class DriverLocationController {

    private final DriverLocationService driverLocationService;
    private final UserService userService;

    @PostMapping("/location")
    public ResponseEntity<Void> updateLocation(@Valid @RequestBody LocationUpdateRequest driverUpdateStatus){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Users currentUser = userService.getUserByEmail(email);
        driverLocationService.publishDriverLocationMessage(
                currentUser.getUserId(),
                driverUpdateStatus.latitude(),
                driverUpdateStatus.longitude()
        );
        return ResponseEntity.accepted().build();
    }
}
