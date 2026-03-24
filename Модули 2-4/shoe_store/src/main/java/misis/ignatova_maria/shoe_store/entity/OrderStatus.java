package misis.ignatova_maria.shoe_store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_statuses")
@Getter
@Setter
@NoArgsConstructor
public class OrderStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "status_id")
	private Integer id;

	@Column(name = "status_name", length = 50, nullable = false, unique = true)
	private String name;
}
