package misis.ignatova_maria.shoe_store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pickup_points")
@Getter
@Setter
@NoArgsConstructor
public class PickupPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Integer id;

    @Column(name = "postal_code", length = 10, nullable = false)
    private String postalCode;

    @Column(name = "city", length = 100, nullable = false)
    private String city;

    @Column(name = "street", length = 255, nullable = false)
    private String street;

    @Column(name = "house_number", length = 20, nullable = false)
    private String houseNumber;

    @Column(name = "full_address", columnDefinition = "TEXT", unique = true)
    private String fullAddress;
}