package com.alpeerkaraca.karga.trip.api;

import com.alpeerkaraca.karga.core.dto.ApiResponse;
import com.alpeerkaraca.karga.driver.application.DriverService;
import com.alpeerkaraca.karga.trip.application.TripManagementService;
import com.alpeerkaraca.karga.trip.application.TripRequestService;
import com.alpeerkaraca.karga.trip.domain.Trips;
import com.alpeerkaraca.karga.trip.domain.TripsRepository;
import com.alpeerkaraca.karga.trip.dto.CurrentLocation;
import com.alpeerkaraca.karga.trip.dto.NearbyDriversResponse;
import com.alpeerkaraca.karga.trip.dto.TripRequest;
import com.alpeerkaraca.karga.user.application.UserService;
import com.alpeerkaraca.karga.user.domain.Users;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trips")
public class TripsController {

    private static final double DEFAULT_RADIUS_KM = 5.0;

    private final TripRequestService tripRequestService;
    private final UserService userService;
    private final TripsRepository tripsRepository;
    private final TripManagementService tripManagementService;
    private final DriverService driverService;

    @GetMapping("/nearby-drivers")
    public ApiResponse<List<NearbyDriversResponse>> getNearbyDrivers(
            @NotNull @RequestParam("latitude") Double latitude,
            @NotNull @RequestParam("longitude") Double longitude
    ) {
        List<NearbyDriversResponse> nearbyDrivers = tripRequestService.findNearbyDrivers(
                latitude,
                longitude,
                DEFAULT_RADIUS_KM
        );
        return ApiResponse.success(
                nearbyDrivers,
                "Yakındaki sürücüler listelendi."
        );
    }

    @PostMapping("/request")
    public ApiResponse<Trips> requestTrip(@Valid @RequestBody TripRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userService.getUserByEmail(email);
        Trips trip = tripRequestService.requestTrip(request, user);

        return ApiResponse.success(trip,"Yolculuk talebiniz alındı.");
    }

    @GetMapping("/available")
    public ApiResponse<List<Trips>> getAvailableTrips() {
        List<Trips> trips = tripManagementService.getAvailableTrips();
        return ApiResponse.success(trips, "Mevcut yolculuklar listelendi.");
    }
    @PostMapping("/{tripId}/accept")
    public ApiResponse<Trips> acceptTrip(@PathVariable("tripId") UUID tripId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID driverID = userService.getUserByEmail(email).getUserId();
        Trips trip = tripManagementService.acceptTrip(tripId, driverID);
        return ApiResponse.success(trip, "Yolculuk kabul edildi.");
    }

    @PostMapping("/{tripId}/start")
    public ApiResponse<Void> startTrip(@PathVariable("tripId") UUID tripId) {
        tripManagementService.startTrip(tripId);
        return ApiResponse.success(null, "Yolculuk başlatıldı.");
    }
    @PostMapping("/{tripId}/complete")
    public ApiResponse<Void> completeTrip(@PathVariable("tripId") UUID tripId) {
        tripManagementService.completeTrip(tripId);
        return ApiResponse.success(null, "Yolculuk tamamlandı.");
    }
    @PostMapping("/{tripId}/cancel")
    public ApiResponse<Void> cancelTrip(@PathVariable("tripId") UUID tripId) {
        tripManagementService.cancelTrip(tripId);
        return ApiResponse.success(null, "Yolculuk iptal edildi.");
    }
}
