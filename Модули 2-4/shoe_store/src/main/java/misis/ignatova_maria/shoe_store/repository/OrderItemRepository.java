package misis.ignatova_maria.shoe_store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import misis.ignatova_maria.shoe_store.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

	@Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.product.article = :article")
	boolean existsByProductArticle(@Param("article") String article);
}
