package misis.ignatova_maria.shoe_store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import misis.ignatova_maria.shoe_store.entity.PickupPoint;

@Repository
public interface PickupPointRepository extends JpaRepository<PickupPoint, Integer> {
	@Query("SELECT p FROM PickupPoint p ORDER BY p.city, p.street")
	List<PickupPoint> findAllOrdered();
}
