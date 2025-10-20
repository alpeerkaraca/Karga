package com.alpeerkaraca.karga.Controllers;

import com.alpeerkaraca.karga.DTO.ApiResponse;
import com.alpeerkaraca.karga.DTO.DriverUpdateStatus;
import com.alpeerkaraca.karga.Models.Users;
import com.alpeerkaraca.karga.Repositories.UsersRepository;
import com.alpeerkaraca.karga.Services.DriverStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drivers")
@Slf4j
@PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
public class DriverStatusController {
    private  final DriverStatusService driverStatusService;
    private final UsersRepository usersRepository;

    @PostMapping("/status")
    public ApiResponse<Void> updateDriverStatus(@Valid @RequestBody DriverUpdateStatus request) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Users currentUser = usersRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

            driverStatusService.updateDriverStatus(
                    currentUser.getUserId(),
                    request.status(),
                    request.longitude(),
                    request.latitude()
            );
            return ApiResponse.success(null, "Sürücü durumu başarıyla güncellendi.");
        }
        catch (Exception e) {
            return ApiResponse.error("Sürücü durumu güncellenirken hata oluştu.");
        }

    }
}
