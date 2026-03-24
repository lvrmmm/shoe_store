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

		String newArticle;
		int maxAttempts = 100;
		int attempts = 0;

		while (attempts < maxAttempts) {
			if (allProducts.isEmpty()) {
				// Если товаров нет, начинаем с минимального значения
				newArticle = "A100A0";
			} else {
				// Находим максимальный артикул (лексикографическая сортировка)
				String maxArticle = allProducts.stream().map(Product::getArticle).max(String::compareTo)
						.orElse("A100A0");

				// Парсим артикул формата: буква + 3 цифры + буква + 1 цифра
				// Пример: A112T4
				char firstLetter = maxArticle.charAt(0); // A
				int numberPart = Integer.parseInt(maxArticle.substring(1, 4)); // 112
				char secondLetter = maxArticle.charAt(4); // T
				int lastDigit = Character.getNumericValue(maxArticle.charAt(5)); // 4

				// Увеличиваем последнюю цифру
				lastDigit++;

				// Обработка переполнения с каскадным увеличением
				if (lastDigit > 9) {
					lastDigit = 0;
					numberPart++;

					if (numberPart > 999) {
						numberPart = 100;
						secondLetter++;

						if (secondLetter > 'Z') {
							secondLetter = 'A';
							firstLetter++;

							// Если первая буква > Z — закончились комбинации
							if (firstLetter > 'Z') {
								throw new RuntimeException("Исчерпаны все возможные комбинации артикулов");
							}
						}
					}
				}

				// Формируем новый артикул в том же формате
				newArticle = String.format("%c%03d%c%d", firstLetter, numberPart, secondLetter, lastDigit);
			}

			// Проверяем уникальность в БД
			if (!productRepository.existsByArticle(newArticle)) {
				return newArticle;
			}

			attempts++;
		}

		throw new RuntimeException("Не удалось сгенерировать уникальный артикул после " + maxAttempts + " попыток");
	}

}
