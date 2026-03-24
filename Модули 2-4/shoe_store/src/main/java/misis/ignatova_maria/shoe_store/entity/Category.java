package misis.ignatova_maria.shoe_store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "category_id")),
		@AttributeOverride(name = "name", column = @Column(name = "category_name", nullable = false, unique = true, length = 254))})
public class Category extends BaseEntity {
}
