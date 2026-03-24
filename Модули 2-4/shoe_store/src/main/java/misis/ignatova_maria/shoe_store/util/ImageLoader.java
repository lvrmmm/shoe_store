package misis.ignatova_maria.shoe_store.util;

import java.awt.*;
import java.io.File;
import java.net.URL;

import javax.swing.*;

public class ImageLoader {
	private static final String STATIC_IMAGES_PATH = "src/main/resources/static/images/";
	private static final String PRODUCTS_PATH = STATIC_IMAGES_PATH + "products/";

	// Кэш для изображений
	private static final java.util.Map<String, ImageIcon> cache = new java.util.HashMap<>();

	public static ImageIcon loadImage(String fileName, int maxWidth, int maxHeight) {
		if (fileName == null || fileName.isEmpty()) {
			return loadPlaceholder(maxWidth, maxHeight);
		}

		// Проверяем кэш
		String cacheKey = fileName + "_" + maxWidth + "_" + maxHeight;
		if (cache.containsKey(cacheKey)) {
			return cache.get(cacheKey);
		}

		// Пытаемся загрузить из файловой системы
		File imageFile = new File(PRODUCTS_PATH + fileName);
		if (imageFile.exists()) {
			try {
				ImageIcon original = new ImageIcon(imageFile.getAbsolutePath());
				ImageIcon scaled = scaleIcon(original, maxWidth, maxHeight);
				cache.put(cacheKey, scaled);
				return scaled;
			} catch (Exception e) {
				System.err.println("Ошибка загрузки файла: " + imageFile.getAbsolutePath());
				e.printStackTrace();
			}
		}

		// Если не нашли в файловой системе, пробуем из classpath
		String classpathPath = "/static/images/products/" + fileName;
		URL imgUrl = ImageLoader.class.getResource(classpathPath);

		if (imgUrl == null) {
			System.err.println("Изображение не найдено: " + classpathPath);
			return loadPlaceholder(maxWidth, maxHeight);
		}

		ImageIcon result = scaleIcon(imgUrl, maxWidth, maxHeight);
		cache.put(cacheKey, result);
		return result;
	}

	// Метод для принудительной перезагрузки изображения
	public static void refreshImage(String fileName) {
		cache.keySet().removeIf(key -> key.startsWith(fileName + "_"));
	}

	public static ImageIcon loadLogo() {
		// Пробуем из файловой системы
		File logoFile = new File(STATIC_IMAGES_PATH + "Icon.png");
		if (logoFile.exists()) {
			ImageIcon original = new ImageIcon(logoFile.getAbsolutePath());
			return scaleIcon(original, 120, 60);
		}

		// Пробуем из classpath
		String[] paths = {"/static/images/Icon.ico", "/static/images/Icon.png", "/static/images/Icon.JPG"};
		for (String path : paths) {
			URL url = ImageLoader.class.getResource(path);
			if (url != null)
				return scaleIcon(url, 120, 60);
		}
		return null;
	}

	public static ImageIcon loadIcon() {
		// Пробуем из файловой системы
		File iconFile = new File(STATIC_IMAGES_PATH + "Icon.JPG");
		if (iconFile.exists()) {
			ImageIcon original = new ImageIcon(iconFile.getAbsolutePath());
			return scaleIcon(original, 32, 32);
		}

		// Пробуем из classpath
		URL url = ImageLoader.class.getResource("/static/images/Icon.JPG");
		if (url != null)
			return scaleIcon(url, 32, 32);
		return new ImageIcon();
	}

	private static ImageIcon loadPlaceholder(int width, int height) {
		// Пробуем из файловой системы
		File placeholderFile = new File(STATIC_IMAGES_PATH + "picture.png");
		if (placeholderFile.exists()) {
			ImageIcon original = new ImageIcon(placeholderFile.getAbsolutePath());
			return scaleIcon(original, width, height);
		}

		// Пробуем из classpath
		URL url = ImageLoader.class.getResource("/static/images/picture.png");
		if (url != null)
			return scaleIcon(url, width, height);
		return new ImageIcon();
	}

	private static ImageIcon scaleIcon(URL url, int maxWidth, int maxHeight) {
		ImageIcon original = new ImageIcon(url);
		return scaleIcon(original, maxWidth, maxHeight);
	}

	private static ImageIcon scaleIcon(ImageIcon original, int maxWidth, int maxHeight) {
		int originalWidth = original.getIconWidth();
		int originalHeight = original.getIconHeight();

		if (originalWidth <= 0 || originalHeight <= 0)
			return original;
		if (originalWidth <= maxWidth && originalHeight <= maxHeight)
			return original;

		double widthRatio = (double) maxWidth / originalWidth;
		double heightRatio = (double) maxHeight / originalHeight;
		double scale = Math.min(widthRatio, heightRatio);

		int newWidth = (int) (originalWidth * scale);
		int newHeight = (int) (originalHeight * scale);

		Image scaled = original.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}
}
