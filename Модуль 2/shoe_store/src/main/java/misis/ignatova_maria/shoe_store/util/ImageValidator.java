package misis.ignatova_maria.shoe_store.util;

import java.awt.*;

import javax.swing.*;

public class ImageValidator {
	private static final int MAX_WIDTH = 300;
	private static final int MAX_HEIGHT = 200;

	public static ImageIcon validateAndScale(ImageIcon imageIcon) {
		if (imageIcon == null || imageIcon.getImage() == null)
			return null;

		int width = imageIcon.getIconWidth();
		int height = imageIcon.getIconHeight();

		if (width <= MAX_WIDTH && height <= MAX_HEIGHT)
			return imageIcon;

		double widthRatio = (double) MAX_WIDTH / width;
		double heightRatio = (double) MAX_HEIGHT / height;
		double scale = Math.min(widthRatio, heightRatio);

		int newWidth = (int) (width * scale);
		int newHeight = (int) (height * scale);

		Image scaled = imageIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

		JOptionPane.showMessageDialog(null,
				"Изображение было автоматически уменьшено до " + newWidth + "x" + newHeight + " пикселей\n"
						+ "Максимальный допустимый размер: " + MAX_WIDTH + "x" + MAX_HEIGHT,
				"Изображение уменьшено", JOptionPane.INFORMATION_MESSAGE);

		return new ImageIcon(scaled);
	}

	public static boolean isValidSize(ImageIcon imageIcon) {
		if (imageIcon == null)
			return false;
		int width = imageIcon.getIconWidth();
		int height = imageIcon.getIconHeight();
		return width <= MAX_WIDTH && height <= MAX_HEIGHT;
	}

	public static Dimension getMaxSize() {
		return new Dimension(MAX_WIDTH, MAX_HEIGHT);
	}
}
