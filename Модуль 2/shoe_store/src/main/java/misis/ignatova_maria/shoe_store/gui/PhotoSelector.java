package misis.ignatova_maria.shoe_store.gui;

import java.awt.*;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.*;

import lombok.Getter;
import misis.ignatova_maria.shoe_store.util.ImageLoader;

/**
 * Компонент для выбора и отображения фото
 */
public class PhotoSelector {

	private final JLabel photoLabel;
	private final Component parent;
	@Getter
	private ImageIcon selectedPhoto;
	@Getter
	private String oldPhotoFileName;

	public PhotoSelector(Component parent, JLabel photoLabel) {
		this.parent = parent;
		this.photoLabel = photoLabel;
	}

	public void selectPhoto() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Выберите фото товара");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Изображения", "jpg", "jpeg", "png", "gif", "bmp");
		fileChooser.setFileFilter(filter);

		int result = fileChooser.showOpenDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			try {
				ImageIcon original = new ImageIcon(fileChooser.getSelectedFile().getAbsolutePath());

				if (original.getIconWidth() > 300 || original.getIconHeight() > 200) {
					int confirm = JOptionPane.showConfirmDialog(parent, """
							Размер изображения превышает допустимый (300×200).
							Изображение будет автоматически уменьшено.
							Продолжить?""", "Подтверждение", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

					if (confirm != JOptionPane.YES_OPTION) {
						return;
					}
				}

				selectedPhoto = original;
				Image scaled = original.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH);
				photoLabel.setIcon(new ImageIcon(scaled));

			} catch (Exception e) {
				JOptionPane.showMessageDialog(parent, "Не удалось загрузить изображение: " + e.getMessage(), "Ошибка",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void setPlaceholder() {
		ImageIcon placeholder = ImageLoader.loadImage("picture.png", 150, 100);
		photoLabel.setIcon(placeholder);
	}

	public void loadExistingPhoto(String photoFileName) {
		if (photoFileName != null && !photoFileName.isEmpty()) {
			oldPhotoFileName = photoFileName;
			selectedPhoto = ImageLoader.loadImage(photoFileName, 150, 100);
			photoLabel.setIcon(selectedPhoto);
		} else {
			setPlaceholder();
		}
	}

}
