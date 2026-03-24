package misis.ignatova_maria.shoe_store.entity;

import java.util.Objects;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "manufacturers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Manufacturer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "manufacturer_id")
	private Integer id;

	@Column(name = "manufacturer_name", nullable = false, unique = true, length = 254)
	private String name;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Manufacturer that))
			return false;
		return name != null && name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}
}
