package misis.ignatova_maria.shoe_store.entity;

import java.util.Objects;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer id;

	@Column(name = "full_name", nullable = false, length = 254)
	private String fullName;

	@Column(name = "login", nullable = false, unique = true, length = 100)
	private String login;

	@Column(name = "password", nullable = false, length = 254)
	private String password;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	public boolean isAdmin() {
		return role != null && "Администратор".equals(role.getName());
	}

	public boolean isManager() {
		return role != null && "Менеджер".equals(role.getName());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof User user))
			return false;
		return login != null && login.equals(user.login);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(login);
	}
}
