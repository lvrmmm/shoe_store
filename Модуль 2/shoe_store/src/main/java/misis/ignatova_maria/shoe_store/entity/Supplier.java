package misis.ignatova_maria.shoe_store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "supplier_id")),
		@AttributeOverride(name = "name", column = @Column(name = "supplier_name", nullable = false, unique = true, length = 254))})
public class Supplier extends BaseEntity {
}
