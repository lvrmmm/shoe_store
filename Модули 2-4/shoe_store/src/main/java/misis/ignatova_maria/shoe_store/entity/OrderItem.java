package misis.ignatova_maria.shoe_store.entity;

import java.util.Objects;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_item_id", nullable = false)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "product_article", nullable = false)
	private Product product;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof OrderItem that))
			return false;
		return Objects.equals(product, that.product);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(product);
	}
}
