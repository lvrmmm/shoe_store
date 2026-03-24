package misis.ignatova_maria.shoe_store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import misis.ignatova_maria.shoe_store.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.login = :login AND u.password = :password")
	Optional<User> findByLoginAndPassword(@Param("login") String login, @Param("password") String password);
}
