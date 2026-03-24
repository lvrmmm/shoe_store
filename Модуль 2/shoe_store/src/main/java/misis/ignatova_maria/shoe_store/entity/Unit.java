package misis.ignatova_maria.shoe_store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "units")
@Getter
@Setter
@NoArgsConstructor
@AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "unit_id")),
		@AttributeOverride(name = "name", column = @Column(name = "unit_name", nullable = false, unique = true, length = 20))})
public class Unit extends BaseEntity {
}
