package misis.ignatova_maria.shoe_store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import misis.ignatova_maria.shoe_store.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

	@Query("SELECT o FROM Order o " + "JOIN FETCH o.user " + "JOIN FETCH o.status " + "JOIN FETCH o.pickupPoint "
			+ "LEFT JOIN FETCH o.orderItems oi " + "LEFT JOIN FETCH oi.product")
	List<Order> findAllWithDetails();
}
