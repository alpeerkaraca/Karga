package com.alpeerkaraca.karga.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "drivers")
//@SQLDelete(sql = "UPDATE drivers SET deleted_at = NOW() WHERE driver_id = ?")
public class Driver extends BaseClass {
    @Id
    private UUID driverId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private Users users;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    private boolean isApproved;

    private boolean isActive;
}
