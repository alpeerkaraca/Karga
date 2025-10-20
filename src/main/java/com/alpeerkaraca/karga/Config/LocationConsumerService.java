package com.alpeerkaraca.karga.Config;

import com.alpeerkaraca.karga.DTO.DriverLocationMessage;
import com.alpeerkaraca.karga.Models.DriverStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationConsumerService {
    private final RedisTemplate<String,String> redisTemplate;

    private static final String KEY_DRIVER_STATUS = "driver:status:";
    private static final String KEY_ONLINE_DRIVERS_GEO = "online_drivers_locations";
    private static final String TOPIC_LOCATION_UPDATES = "driver_location_updates";

    @KafkaListener(topics = TOPIC_LOCATION_UPDATES, groupId = "location_consumer_group")
    public void consumeLocationUpdate(DriverLocationMessage message) {
        String driverIdStr = message.driverId().toString();
        String statusKey = KEY_DRIVER_STATUS + driverIdStr;

        String status = redisTemplate.opsForValue().get(statusKey);

        if (DriverStatus.ONLINE.name().equals(status)) {
            redisTemplate.opsForGeo().add(
                    KEY_ONLINE_DRIVERS_GEO,
                    new Point(message.longitude(), message.latitude()),
                    driverIdStr
            );
        } else {
            log.warn("Çevrımdışı sürücü için güncelleme alındı:{}", driverIdStr);
        }
    }
}
