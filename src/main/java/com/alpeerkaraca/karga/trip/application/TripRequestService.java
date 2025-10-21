package com.alpeerkaraca.karga.trip.application;

import com.alpeerkaraca.karga.trip.dto.NearbyDriversResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.ReactiveGeoCommands;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.GeoOperations;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TripRequestService {
    private RedisTemplate<String, String> redisTemplate;
    private final String ONLINE_DRIVERS_GEO_KEY = "online_drivers_locations";

    public TripRequestService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<NearbyDriversResponse> findNearbyDrivers(double latitude, double longitude, double radiusKm) {
        GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();

        Point center = new Point(longitude, latitude);
        Distance radius = new Distance(radiusKm, Metrics.KILOMETERS);
        Circle circle = new Circle(center, radius);

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeCoordinates()
                .includeDistance()
                .sortAscending()
                .limit(10);
        GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults = geoOps.radius(
                ONLINE_DRIVERS_GEO_KEY,
                circle,
                args
        );
        return geoResults.getContent().stream()
                .map(result -> {
                    String driverIdStr = result.getContent().getName();
                    UUID driverId = UUID.fromString(driverIdStr);
                    Point driverPoint = result.getContent().getPoint();
                    double driverLatitude = driverPoint.getY();
                    double driverLongitude = driverPoint.getX();
                    double distanceKm = result.getDistance().getValue();

                    return new NearbyDriversResponse(driverId, driverLatitude, driverLongitude, distanceKm);
                }).collect(Collectors.toList());
    }

}
