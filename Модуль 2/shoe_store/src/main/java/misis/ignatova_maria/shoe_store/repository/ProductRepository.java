package misis.ignatova_maria.shoe_store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import misis.ignatova_maria.shoe_store.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

	@Query("SELECT p FROM Product p " + "JOIN FETCH p.unit " + "JOIN FETCH p.supplier " + "JOIN FETCH p.manufacturer "
			+ "JOIN FETCH p.category")
	List<Product> findAllWithDetails();

	/**
	 * Проверяет существование товара с таким артикулом
	 */
	boolean existsByArticle(String article);
}
