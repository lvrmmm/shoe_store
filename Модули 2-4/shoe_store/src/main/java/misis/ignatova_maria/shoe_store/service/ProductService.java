package misis.ignatova_maria.shoe_store.service;

import java.util.List;
import java.util.Optional;

import javax.swing.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import misis.ignatova_maria.shoe_store.entity.*;
import misis.ignatova_maria.shoe_store.repository.OrderItemRepository;
import misis.ignatova_maria.shoe_store.repository.ProductRepository;

@Service
@Transactional
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private ImageService imageService;

	public List<Product> getAllProducts() {
		return productRepository.findAllWithDetails();
	}

	@Transactional
	public Product createProduct(Product product, ImageIcon productImage) {
		if (product.getArticle() == null || product.getArticle().isEmpty()) {
			product.setArticle(generateArticle());
		}

		if (productImage != null && productImage.getImage() != null) {
			try {
				String fileName = imageService.saveImage(productImage, null);
				product.setPhoto(fileName);
			} catch (Exception e) {
				throw new RuntimeException("Ошибка при сохранении изображения: " + e.getMessage(), e);
			}
		}

		return productRepository.save(product);
	}

	@Transactional
	public Product updateProduct(Product product, ImageIcon newImage, String oldPhoto) {
		// Если загружено НОВОЕ изображение
		if (newImage != null && newImage.getImage() != null) {
			try {
				// Сохраняем новое изображение (старое будет удалено внутри saveImage)
				String fileName = imageService.saveImage(newImage, oldPhoto);
				product.setPhoto(fileName);
			} catch (Exception e) {
				e.printStackTrace();
				// В случае ошибки сохраняем старое фото
				product.setPhoto(oldPhoto);
			}
		} else {
			// Если новое фото НЕ выбрано - сохраняем существующее
			// НО важно: oldPhoto может быть null, если у товара не было фото
			product.setPhoto(oldPhoto);
		}

		return productRepository.save(product);
	}

	/**
	 * Удаляет товар, если он не присутствует в заказах
	 */
	@Transactional
	public boolean deleteProduct(String article) throws IllegalStateException {
		if (!orderItemRepository.existsByProductArticle(article)) {
			Optional<Product> product = productRepository.findById(article);
			if (product.isPresent()) {
				imageService.deleteImage(product.get().getPhoto());
				productRepository.delete(product.get());
				return true;
			}
			return false;
		} else {
			throw new IllegalStateException("Товар присутствует в заказах и не может быть удалён");
		}

	}

	/**
	 * Генерация уникального артикула Формат: 1 буква + 3 цифры + 1 буква + 1 цифра
	 * (A112T4) Генерируется следующий по порядку после максимального существующего
	 * с проверкой уникальности в БД
	 */
	private String generateArticle() {
		List<Product> allProducts = productRepository.findAllWithDetails();

		if (allProducts.isEmpty()) {
			return "A100A0";
		}

		String maxArticle = allProducts.stream().map(Product::getArticle).max(String::compareTo).orElse("A100A0");

		return generateNextArticle(maxArticle);
	}

	private String generateNextArticle(String currentArticle) {
		char firstLetter = currentArticle.charAt(0);
		int numberPart = Integer.parseInt(currentArticle.substring(1, 4));
		char secondLetter = currentArticle.charAt(4);
		int lastDigit = Character.getNumericValue(currentArticle.charAt(5));

		lastDigit++;

		// Обработка переполнения
		ArticleComponents components = handleOverflow(firstLetter, numberPart, secondLetter, lastDigit);

		String newArticle = String.format("%c%03d%c%d", components.firstLetter, components.numberPart,
				components.secondLetter, components.lastDigit);

		// Проверка уникальности
		if (productRepository.existsByArticle(newArticle)) {
			return generateNextArticle(newArticle);
		}

		return newArticle;
	}

	private ArticleComponents handleOverflow(char firstLetter, int numberPart, char secondLetter, int lastDigit) {
		if (lastDigit <= 9) {
			return new ArticleComponents(firstLetter, numberPart, secondLetter, lastDigit);
		}

		lastDigit = 0;
		numberPart++;

		if (numberPart <= 999) {
			return new ArticleComponents(firstLetter, numberPart, secondLetter, lastDigit);
		}

		numberPart = 100;
		secondLetter++;

		if (secondLetter <= 'Z') {
			return new ArticleComponents(firstLetter, numberPart, secondLetter, lastDigit);
		}

		secondLetter = 'A';
		firstLetter++;

		if (firstLetter > 'Z') {
			throw new RuntimeException("Исчерпаны все возможные комбинации артикулов");
		}

		return new ArticleComponents(firstLetter, numberPart, secondLetter, lastDigit);
	}

	private static class ArticleComponents {
		char firstLetter;
		int numberPart;
		char secondLetter;
		int lastDigit;

		ArticleComponents(char firstLetter, int numberPart, char secondLetter, int lastDigit) {
			this.firstLetter = firstLetter;
			this.numberPart = numberPart;
			this.secondLetter = secondLetter;
			this.lastDigit = lastDigit;
		}
	}

}
