package misis.ignatova_maria.shoe_store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import misis.ignatova_maria.shoe_store.entity.OrderStatus;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {
	Optional<OrderStatus> findByName(String name);
}
