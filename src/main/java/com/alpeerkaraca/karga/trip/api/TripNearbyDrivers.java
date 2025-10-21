package com.alpeerkaraca.karga.trip.api;

import com.alpeerkaraca.karga.core.dto.ApiResponse;
import com.alpeerkaraca.karga.trip.application.TripRequestService;
import com.alpeerkaraca.karga.trip.dto.NearbyDriversRequest;
import com.alpeerkaraca.karga.trip.dto.NearbyDriversResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trips")
public class TripNearbyDrivers {

    private final TripRequestService tripRequestService;


    @GetMapping("/nearby-drivers")
    public ApiResponse<List<NearbyDriversResponse>> getNearbyDrivers(@Valid @RequestBody NearbyDriversRequest request) {
        List<NearbyDriversResponse> nearbyDrivers = tripRequestService.findNearbyDrivers(
                request.latitude(),
                request.longitude(),
                5.0
        );
        return ApiResponse.success(
                nearbyDrivers,
                "Yakındaki sürücüler listelendi."
        );
    }
}
