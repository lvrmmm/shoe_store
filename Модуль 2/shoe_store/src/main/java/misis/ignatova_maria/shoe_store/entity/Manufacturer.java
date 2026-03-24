package misis.ignatova_maria.shoe_store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "manufacturers")
@Getter
@Setter
@NoArgsConstructor
@AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "manufacturer_id")),
		@AttributeOverride(name = "name", column = @Column(name = "manufacturer_name", nullable = false, unique = true, length = 254))})
public class Manufacturer extends BaseEntity {
}
