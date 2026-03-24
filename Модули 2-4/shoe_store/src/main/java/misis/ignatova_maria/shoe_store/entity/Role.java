package misis.ignatova_maria.shoe_store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "role_id")),
		@AttributeOverride(name = "name", column = @Column(name = "role_name", nullable = false, unique = true, length = 100))})
public class Role extends BaseEntity {
}
