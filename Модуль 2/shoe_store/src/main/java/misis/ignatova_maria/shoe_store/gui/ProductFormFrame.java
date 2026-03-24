package misis.ignatova_maria.shoe_store.gui;

import java.awt.*;
import java.math.BigDecimal;
import java.util.Objects;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.*;

import lombok.Getter;
import misis.ignatova_maria.shoe_store.entity.*;
import misis.ignatova_maria.shoe_store.service.ProductService;
import misis.ignatova_maria.shoe_store.util.ImageLoader;
import misis.ignatova_maria.shoe_store.util.SpringContext;

/**
 * Форма для добавления и редактирования товара Доступна только администратору
 */
public class ProductFormFrame extends JFrame {
	private final ProductService productService;
	private final ProductListFrame parentFrame;
	private final Product editingProduct;
	private final boolean isEditMode;
	@Getter
	private boolean isFrameOpen = true;

	// Компоненты формы
	private JTextField articleField;
	private JTextField nameField;
	private JComboBox<Category> categoryCombo;
	private JTextArea descriptionArea;
	private JComboBox<Manufacturer> manufacturerCombo;
	private JComboBox<Supplier> supplierCombo;
	private JTextField priceField;
	private JComboBox<Unit> unitCombo;
	private JSpinner quantitySpinner;
	private JSpinner discountSpinner;
	private JLabel photoLabel;

	private ImageIcon selectedPhoto;
	private String oldPhotoFileName;

	public ProductFormFrame(ProductListFrame parent, Product productToEdit) {
		this.productService = SpringContext.getBean(ProductService.class);
		this.parentFrame = parent;
		this.editingProduct = productToEdit;
		this.isEditMode = (productToEdit != null);
		initUI();
		loadData();
		setLocationRelativeTo(parent);
	}

	private void initUI() {
		setTitle(isEditMode ? "Редактирование товара" : "Добавление товара");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(750, 750);
		setMinimumSize(new Dimension(700, 700));
		setLocationRelativeTo(null);
		setIconImage(ImageLoader.loadIcon().getImage());

		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		mainPanel.setBackground(Color.WHITE);

		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 8, 8, 8);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		int row = 0;

		// ===== АРТИКУЛ (только для чтения при редактировании, скрыт при добавлении)
		// =====
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		JLabel articleLabel = new JLabel("Артикул:");
		formPanel.add(articleLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		articleField = new JTextField(20);

		if (isEditMode) {
			articleField.setEditable(false);
			articleField.setBackground(Color.LIGHT_GRAY);
			articleField.setToolTipText("Артикул нельзя изменить");
		} else {
			// При добавлении скрываем поле (артикул генерируется автоматически)
			articleField.setVisible(false);
			articleLabel.setVisible(false);
		}
		formPanel.add(articleField, gbc);
		row++;

		// ===== НАИМЕНОВАНИЕ =====
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		formPanel.add(new JLabel("Наименование:*"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		nameField = new JTextField(20);
		formPanel.add(nameField, gbc);
		row++;

		// ===== КАТЕГОРИЯ =====
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		formPanel.add(new JLabel("Категория:*"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		categoryCombo = new JComboBox<>();
		loadCategories();
		categoryCombo.setRenderer(new EntityListCellRenderer());
		formPanel.add(categoryCombo, gbc);
		row++;

		// ===== ОПИСАНИЕ =====
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		formPanel.add(new JLabel("Описание:"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		descriptionArea = new JTextArea(4, 20);
		descriptionArea.setLineWrap(true);
		descriptionArea.setWrapStyleWord(true);
		JScrollPane descScroll = new JScrollPane(descriptionArea);
		formPanel.add(descScroll, gbc);
		row++;

		// ===== ПРОИЗВОДИТЕЛЬ =====
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		formPanel.add(new JLabel("Производитель:*"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		manufacturerCombo = new JComboBox<>();
		loadManufacturers();
		manufacturerCombo.setRenderer(new EntityListCellRenderer());
		formPanel.add(manufacturerCombo, gbc);
		row++;

		// ===== ПОСТАВЩИК =====
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		formPanel.add(new JLabel("Поставщик:*"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		supplierCombo = new JComboBox<>();
		loadSuppliers();
		supplierCombo.setRenderer(new EntityListCellRenderer());
		formPanel.add(supplierCombo, gbc);
		row++;

		// ===== ЦЕНА =====
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		formPanel.add(new JLabel("Цена:*"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		priceField = new JTextField(15);
		priceField.setToolTipText("Введите положительное число (например: 4990.00)");
		formPanel.add(priceField, gbc);
		row++;

		// ===== ЕДИНИЦА ИЗМЕРЕНИЯ =====
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		formPanel.add(new JLabel("Единица измерения:*"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		unitCombo = new JComboBox<>();
		loadUnits();
		unitCombo.setRenderer(new EntityListCellRenderer());
		formPanel.add(unitCombo, gbc);
		row++;

		// ===== КОЛИЧЕСТВО =====
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		formPanel.add(new JLabel("Количество на складе:"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		quantitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99999, 1));
		formPanel.add(quantitySpinner, gbc);
		row++;

		// ===== СКИДКА =====
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		formPanel.add(new JLabel("Скидка (%):"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		discountSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		formPanel.add(discountSpinner, gbc);
		row++;

		// ===== ФОТО =====
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		formPanel.add(new JLabel("Фото:"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		photoPanel.setBackground(Color.WHITE);

		photoLabel = new JLabel();
		photoLabel.setPreferredSize(new Dimension(150, 100));
		photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		photoLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JButton selectPhotoButton = new JButton("Выбрать фото");
		selectPhotoButton.addActionListener(e -> selectPhoto());

		photoPanel.add(photoLabel);
		photoPanel.add(selectPhotoButton);
		formPanel.add(photoPanel, gbc);
		row++;

		// ===== ПОДСКАЗКА =====
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.gridwidth = 2;
		JLabel hintLabel = new JLabel("* — обязательные поля. Макс. размер фото: 300×200 пикселей");
		hintLabel.setFont(new Font("Times New Roman", Font.ITALIC, 10));
		hintLabel.setForeground(Color.GRAY);
		formPanel.add(hintLabel, gbc);

		// ===== КНОПКИ =====
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
		buttonPanel.setBackground(Color.WHITE);

		JButton saveButton = new JButton(isEditMode ? "Сохранить изменения" : "Добавить товар");
		saveButton.setBackground(Color.decode("#00FA9A"));
		saveButton.setFont(new Font("Times New Roman", Font.BOLD, 12));
		saveButton.addActionListener(e -> saveProduct());

		JButton cancelButton = new JButton("Отмена");
		cancelButton.setBackground(Color.decode("#FFA07A"));
		cancelButton.addActionListener(e -> dispose());

		buttonPanel.add(saveButton);
		buttonPanel.add(cancelButton);

		JScrollPane scrollPane = new JScrollPane(formPanel);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		mainPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		add(mainPanel);

		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent e) {
				isFrameOpen = false;
			}
		});
	}

	private void loadData() {
		if (isEditMode && editingProduct != null) {
			articleField.setText(editingProduct.getArticle());
			nameField.setText(editingProduct.getName());
			selectComboItem(categoryCombo, editingProduct.getCategory());
			descriptionArea.setText(editingProduct.getDescription());
			selectComboItem(manufacturerCombo, editingProduct.getManufacturer());
			selectComboItem(supplierCombo, editingProduct.getSupplier());
			priceField.setText(editingProduct.getPrice().toString());
			selectComboItem(unitCombo, editingProduct.getUnit());
			quantitySpinner.setValue(editingProduct.getQuantity() != null ? editingProduct.getQuantity() : 0);
			discountSpinner
					.setValue(editingProduct.getDiscount() != null ? editingProduct.getDiscount().intValue() : 0);

			oldPhotoFileName = editingProduct.getPhoto();
			if (oldPhotoFileName != null && !oldPhotoFileName.isEmpty()) {
				selectedPhoto = ImageLoader.loadImage(oldPhotoFileName, 150, 100);
				photoLabel.setIcon(selectedPhoto);
			} else {
				ImageIcon placeholder = ImageLoader.loadImage("picture.png", 150, 100);
				photoLabel.setIcon(placeholder);
			}
		} else {
			ImageIcon placeholder = ImageLoader.loadImage("picture.png", 150, 100);
			photoLabel.setIcon(placeholder);
		}
	}

	private void loadCategories() {
		categoryCombo.addItem(new Category(1, "Женская обувь"));
		categoryCombo.addItem(new Category(2, "Мужская обувь"));
	}

	private void loadManufacturers() {
		manufacturerCombo.addItem(new Manufacturer(1, "Kari"));
		manufacturerCombo.addItem(new Manufacturer(2, "Marco Tozzi"));
		manufacturerCombo.addItem(new Manufacturer(3, "Рос"));
		manufacturerCombo.addItem(new Manufacturer(4, "Rieker"));
		manufacturerCombo.addItem(new Manufacturer(5, "Alessio Nesca"));
		manufacturerCombo.addItem(new Manufacturer(6, "CROSBY"));
	}

	private void loadSuppliers() {
		supplierCombo.addItem(new Supplier(1, "Kari"));
		supplierCombo.addItem(new Supplier(2, "Обувь для вас"));
	}

	private void loadUnits() {
		unitCombo.addItem(new Unit(1, "шт."));
	}

	private <T> void selectComboItem(JComboBox<T> combo, T item) {
		for (int i = 0; i < combo.getItemCount(); i++) {
			if (Objects.equals(combo.getItemAt(i), item)) {
				combo.setSelectedIndex(i);
				break;
			}
		}
	}

	private void selectPhoto() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Выберите фото товара");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Изображения", "jpg", "jpeg", "png", "gif", "bmp");
		fileChooser.setFileFilter(filter);

		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			try {
				ImageIcon original = new ImageIcon(fileChooser.getSelectedFile().getAbsolutePath());

				if (original.getIconWidth() > 300 || original.getIconHeight() > 200) {
					int confirm = JOptionPane.showConfirmDialog(this, """
							Размер изображения превышает допустимый (300×200).
							Изображение будет автоматически уменьшено.
							Продолжить?""", "Подтверждение", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
							null);

					if (confirm != JOptionPane.YES_OPTION) {
						return;
					}
				}

				selectedPhoto = original;
				Image scaled = original.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH);
				photoLabel.setIcon(new ImageIcon(scaled));

			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Не удалось загрузить изображение: " + e.getMessage(), "Ошибка",
						JOptionPane.ERROR_MESSAGE, null);
			}
		}
	}

	private void saveProduct() {
		if (!validateForm()) {
			return;
		}

		try {
			Product product;

			if (isEditMode) {
				product = editingProduct;
			} else {
				product = new Product();
				// Артикул генерируется автоматически в ProductService
			}

			product.setName(nameField.getText().trim());
			product.setCategory((Category) categoryCombo.getSelectedItem());
			product.setDescription(descriptionArea.getText().trim());
			product.setManufacturer((Manufacturer) manufacturerCombo.getSelectedItem());
			product.setSupplier((Supplier) supplierCombo.getSelectedItem());
			product.setPrice(new BigDecimal(priceField.getText().trim()));
			product.setUnit((Unit) unitCombo.getSelectedItem());
			product.setQuantity((Integer) quantitySpinner.getValue());

			Number discountValue = (Number) discountSpinner.getValue();
			BigDecimal discount = discountValue != null
					? BigDecimal.valueOf(discountValue.doubleValue())
					: BigDecimal.ZERO;
			product.setDiscount(discount);

			if (isEditMode) {
				productService.updateProduct(product, selectedPhoto, oldPhotoFileName);
				JOptionPane.showMessageDialog(this, "Товар успешно обновлён", "Успешно",
						JOptionPane.INFORMATION_MESSAGE, null);
			} else {
				productService.createProduct(product, selectedPhoto);
				JOptionPane.showMessageDialog(this, "Товар успешно добавлен\nАртикул: " + product.getArticle(),
						"Успешно", JOptionPane.INFORMATION_MESSAGE, null);
			}

			if (parentFrame != null) {
				parentFrame.refreshProducts();
			}

			dispose();

		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Неверный формат цены. Используйте числа с точкой (например: 4990.00)",
					"Ошибка ввода", JOptionPane.ERROR_MESSAGE, null);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Ошибка при сохранении товара: " + e.getMessage(), "Ошибка",
					JOptionPane.ERROR_MESSAGE, null);
		}
	}

	private boolean validateForm() {
		// Артикул не проверяем - он генерируется автоматически

		if (nameField.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Наименование товара обязательно для заполнения", "Ошибка валидации",
					JOptionPane.ERROR_MESSAGE, null);
			nameField.requestFocus();
			return false;
		}

		if (categoryCombo.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(this, "Выберите категорию товара", "Ошибка валидации",
					JOptionPane.ERROR_MESSAGE, null);
			return false;
		}

		if (manufacturerCombo.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(this, "Выберите производителя", "Ошибка валидации", JOptionPane.ERROR_MESSAGE,
					null);
			return false;
		}

		if (supplierCombo.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(this, "Выберите поставщика", "Ошибка валидации", JOptionPane.ERROR_MESSAGE,
					null);
			return false;
		}

		try {
			BigDecimal price = new BigDecimal(priceField.getText().trim());
			if (price.compareTo(BigDecimal.ZERO) <= 0) {
				JOptionPane.showMessageDialog(this, "Цена должна быть положительным числом", "Ошибка валидации",
						JOptionPane.ERROR_MESSAGE, null);
				priceField.requestFocus();
				return false;
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Введите корректную цену (например: 4990.00)", "Ошибка валидации",
					JOptionPane.ERROR_MESSAGE, null);
			priceField.requestFocus();
			return false;
		}

		if (unitCombo.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(this, "Выберите единицу измерения", "Ошибка валидации",
					JOptionPane.ERROR_MESSAGE, null);
			return false;
		}

		return true;
	}

	private static class EntityListCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			if (value instanceof Category) {
				setText(((Category) value).getName());
			} else if (value instanceof Manufacturer) {
				setText(((Manufacturer) value).getName());
			} else if (value instanceof Supplier) {
				setText(((Supplier) value).getName());
			} else if (value instanceof Unit) {
				setText(((Unit) value).getName());
			}

			return this;
		}
	}
}
