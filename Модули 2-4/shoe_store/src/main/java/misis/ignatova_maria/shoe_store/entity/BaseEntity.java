package misis.ignatova_maria.shoe_store.entity;

import java.util.Objects;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Integer id;

	protected String name;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof BaseEntity that))
			return false;
		return name != null && name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}
}
