package misis.ignatova_maria.shoe_store.gui;

import java.awt.*;
import java.math.BigDecimal;

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

	private PhotoSelector photoSelector;
	private ProductFormValidator validator;

	public ProductFormFrame(ProductListFrame parent, Product productToEdit) {
		this.productService = SpringContext.getBean(ProductService.class);
		this.parentFrame = parent;
		this.editingProduct = productToEdit;
		this.isEditMode = (productToEdit != null);

		initUI();
		setupComponents();
		loadData();
		setLocationRelativeTo(parent);
	}

	private void setupComponents() {
		photoSelector = new PhotoSelector(this, photoLabel);
		validator = new ProductFormValidator(this, nameField, categoryCombo, manufacturerCombo, supplierCombo,
				priceField, unitCombo);
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

		JPanel formPanel = createFormPanel();
		JPanel buttonPanel = createButtonPanel();

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

	private JPanel createFormPanel() {
		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(Color.WHITE);
		FormFieldBuilder builder = new FormFieldBuilder(formPanel);

		// Артикул
		builder.addLabel("Артикул:");
		articleField = new JTextField(20);
		if (isEditMode) {
			articleField.setEditable(false);
			articleField.setBackground(Color.LIGHT_GRAY);
			articleField.setToolTipText("Артикул нельзя изменить");
		} else {
			articleField.setVisible(false);
			builder.addComponent(new JPanel()); // пустышка
		}
		builder.addComponent(articleField);

		// Наименование
		builder.addLabel("Наименование:*");
		nameField = new JTextField(20);
		builder.addComponent(nameField);

		// Категория
		builder.addLabel("Категория:*");
		categoryCombo = new JComboBox<>();
		EntityLoader.loadCategories(categoryCombo);
		categoryCombo.setRenderer(new EntityListCellRenderer());
		builder.addComponent(categoryCombo);

		// Описание
		builder.addLabel("Описание:");
		descriptionArea = new JTextArea(4, 20);
		descriptionArea.setLineWrap(true);
		descriptionArea.setWrapStyleWord(true);
		JScrollPane descScroll = new JScrollPane(descriptionArea);
		builder.addComponent(descScroll);

		// Производитель
		builder.addLabel("Производитель:*");
		manufacturerCombo = new JComboBox<>();
		EntityLoader.loadManufacturers(manufacturerCombo);
		manufacturerCombo.setRenderer(new EntityListCellRenderer());
		builder.addComponent(manufacturerCombo);

		// Поставщик
		builder.addLabel("Поставщик:*");
		supplierCombo = new JComboBox<>();
		EntityLoader.loadSuppliers(supplierCombo);
		supplierCombo.setRenderer(new EntityListCellRenderer());
		builder.addComponent(supplierCombo);

		// Цена
		builder.addLabel("Цена:*");
		priceField = new JTextField(15);
		priceField.setToolTipText("Введите положительное число (например: 4990.00)");
		builder.addComponent(priceField);

		// Единица измерения
		builder.addLabel("Единица измерения:*");
		unitCombo = new JComboBox<>();
		EntityLoader.loadUnits(unitCombo);
		unitCombo.setRenderer(new EntityListCellRenderer());
		builder.addComponent(unitCombo);

		// Количество
		builder.addLabel("Количество на складе:");
		quantitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99999, 1));
		builder.addComponent(quantitySpinner);

		// Скидка
		builder.addLabel("Скидка (%):");
		discountSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		builder.addComponent(discountSpinner);

		// Фото
		builder.addLabel("Фото:");
		photoLabel = new JLabel();
		photoLabel.setPreferredSize(new Dimension(150, 100));
		photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		photoLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		photoPanel.setBackground(Color.WHITE);

		JButton selectPhotoButton = new JButton("Выбрать фото");
		selectPhotoButton.addActionListener(e -> photoSelector.selectPhoto());

		photoPanel.add(photoLabel);
		photoPanel.add(selectPhotoButton);
		builder.addComponent(photoPanel);

		// Подсказка
		JLabel hintLabel = new JLabel("* — обязательные поля. Макс. размер фото: 300×200 пикселей");
		hintLabel.setFont(new Font("Times New Roman", Font.ITALIC, 10));
		hintLabel.setForeground(Color.GRAY);
		builder.addFullWidthComponent(hintLabel);

		return formPanel;
	}

	private JPanel createButtonPanel() {
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

		return buttonPanel;
	}

	private void loadData() {
		if (isEditMode && editingProduct != null) {
			articleField.setText(editingProduct.getArticle());
			nameField.setText(editingProduct.getName());
			EntityLoader.selectComboItem(categoryCombo, editingProduct.getCategory());
			descriptionArea.setText(editingProduct.getDescription());
			EntityLoader.selectComboItem(manufacturerCombo, editingProduct.getManufacturer());
			EntityLoader.selectComboItem(supplierCombo, editingProduct.getSupplier());
			priceField.setText(editingProduct.getPrice().toString());
			EntityLoader.selectComboItem(unitCombo, editingProduct.getUnit());
			quantitySpinner.setValue(editingProduct.getQuantity() != null ? editingProduct.getQuantity() : 0);
			discountSpinner
					.setValue(editingProduct.getDiscount() != null ? editingProduct.getDiscount().intValue() : 0);

			photoSelector.loadExistingPhoto(editingProduct.getPhoto());
		} else {
			photoSelector.setPlaceholder();
		}
	}

	private void saveProduct() {
		if (!validator.validate()) {
			return;
		}

		try {
			Product product = isEditMode ? editingProduct : new Product();

			product.setName(nameField.getText().trim());
			product.setCategory((Category) categoryCombo.getSelectedItem());
			product.setDescription(descriptionArea.getText().trim());
			product.setManufacturer((Manufacturer) manufacturerCombo.getSelectedItem());
			product.setSupplier((Supplier) supplierCombo.getSelectedItem());
			product.setPrice(new BigDecimal(priceField.getText().trim()));
			product.setUnit((Unit) unitCombo.getSelectedItem());
			product.setQuantity((Integer) quantitySpinner.getValue());

			Number discountValue = (Number) discountSpinner.getValue();
			product.setDiscount(
					discountValue != null ? BigDecimal.valueOf(discountValue.doubleValue()) : BigDecimal.ZERO);

			if (isEditMode) {
				productService.updateProduct(product, photoSelector.getSelectedPhoto(),
						photoSelector.getOldPhotoFileName());
				showSuccessMessage("Товар успешно обновлён");
			} else {
				productService.createProduct(product, photoSelector.getSelectedPhoto());
				showSuccessMessage("Товар успешно добавлен\nАртикул: " + product.getArticle());
			}

			if (parentFrame != null) {
				parentFrame.refreshProducts();
			}

			dispose();

		} catch (NumberFormatException e) {
			showErrorMessage("Неверный формат цены. Используйте числа с точкой (например: 4990.00)");
		} catch (Exception e) {
			showErrorMessage("Ошибка при сохранении товара: " + e.getMessage());
		}
	}

	private void showSuccessMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Успешно", JOptionPane.INFORMATION_MESSAGE);
	}

	private void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
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
