package misis.ignatova_maria.shoe_store.entity;

import java.util.Objects;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Integer id;

	@Column(name = "category_name", nullable = false, unique = true, length = 254)
	private String name;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Category category))
			return false;
		return name != null && name.equals(category.name);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}
}
