package misis.ignatova_maria.shoe_store.service;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.springframework.stereotype.Service;

@Service
public class ImageService {

	private static final String IMAGES_DIR = "src/main/resources/static/images/products/";

	public String saveImage(ImageIcon imageIcon, String oldFileName) throws IOException {
		if (imageIcon == null || imageIcon.getImage() == null) {
			System.out.println("saveImage: imageIcon is null");
			return oldFileName;
		}

		// Создаем директорию если не существует
		File dir = new File(IMAGES_DIR);
		if (!dir.exists()) {
			boolean created = dir.mkdirs();
			System.out.println("Создана директория: " + IMAGES_DIR + ", успешно: " + created);
		}

		// Генерируем уникальное имя файла
		String fileName = UUID.randomUUID() + ".png";
		File outputFile = new File(IMAGES_DIR + fileName);

		System.out.println("Сохраняем изображение: " + outputFile.getAbsolutePath());
		System.out.println("Размер изображения: " + imageIcon.getIconWidth() + "x" + imageIcon.getIconHeight());

		// Создаем BufferedImage для сохранения
		BufferedImage bufferedImage = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bufferedImage.createGraphics();

		// Улучшаем качество
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		imageIcon.paintIcon(null, g2d, 0, 0);
		g2d.dispose();

		// Сохраняем
		boolean saved = ImageIO.write(bufferedImage, "png", outputFile);
		if (!saved) {
			throw new IOException("Не удалось сохранить изображение");
		}

		System.out.println("Изображение сохранено: " + fileName);
		System.out.println("Файл существует: " + outputFile.exists());

		// Удаляем старое изображение
		if (oldFileName != null && !oldFileName.isEmpty() && !oldFileName.equals(fileName)) {
			deleteImage(oldFileName);
		}

		return fileName;
	}

	public boolean deleteImage(String fileName) {
		if (fileName == null || fileName.isEmpty())
			return false;
		File imageFile = new File(IMAGES_DIR + fileName);
		try {
			boolean deleted = imageFile.delete();
			if (deleted) {
				System.out.println("Удалено изображение: " + imageFile.getAbsolutePath());
			}
			return deleted;
		} catch (Exception e) {
			System.err.println("Не удалось удалить изображение: " + fileName);
			e.printStackTrace();
			return false;
		}
	}

}
