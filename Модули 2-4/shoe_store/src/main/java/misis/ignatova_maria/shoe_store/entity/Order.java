package misis.ignatova_maria.shoe_store.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id", nullable = false)
	private Integer id;

	@Column(name = "order_date", nullable = false)
	private LocalDate orderDate;

	@Column(name = "delivery_date", nullable = false)
	private LocalDate deliveryDate;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "point_id", nullable = false)
	private PickupPoint pickupPoint;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "pickup_code", length = 20)
	private String pickupCode;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "status_id", nullable = false)
	private OrderStatus status;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<OrderItem> orderItems = new ArrayList<>();
}
