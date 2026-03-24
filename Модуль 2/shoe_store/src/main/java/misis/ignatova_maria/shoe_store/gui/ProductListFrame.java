package misis.ignatova_maria.shoe_store.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.*;

import misis.ignatova_maria.shoe_store.entity.Product;
import misis.ignatova_maria.shoe_store.entity.User;
import misis.ignatova_maria.shoe_store.gui.dialogs.ConfirmationDialog;
import misis.ignatova_maria.shoe_store.service.ProductService;
import misis.ignatova_maria.shoe_store.util.ImageLoader;
import misis.ignatova_maria.shoe_store.util.SpringContext;

public class ProductListFrame extends JFrame {

	private final ProductService productService;
	private final User currentUser;
	private JPanel cardsPanel;
	private JScrollPane scrollPane;
	private final DecimalFormat priceFormat;

	private JTextField searchField;
	private JComboBox<String> supplierFilterCombo;
	private JComboBox<String> sortCombo;
	private List<Product> allProducts;
	private List<Product> filteredAndSearchedProducts;

	private final AtomicBoolean isEditFormOpen = new AtomicBoolean(false);

	public ProductListFrame(User user) {
		this.productService = SpringContext.getBean(ProductService.class);
		this.currentUser = user;
		this.priceFormat = new DecimalFormat("#,##0.00");

		initUI();
		loadProducts();
	}

	private void initUI() {
		setTitle("Список товаров");
		setSize(1300, 900);
		setMinimumSize(new Dimension(1000, 700));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIconImage(ImageLoader.loadIcon().getImage());

		UIManager.put("Label.font", new Font("Times New Roman", Font.PLAIN, 12));
		UIManager.put("Button.font", new Font("Times New Roman", Font.PLAIN, 12));

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(Color.WHITE);
		add(mainPanel);

		JPanel topPanel = createTopPanel();
		JPanel controlPanel = createControlPanel();
		JPanel catalogPanel = createCatalogPanel();

		JPanel northContainer = new JPanel();
		northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
		northContainer.add(topPanel);
		northContainer.add(controlPanel);
		northContainer.add(catalogPanel);

		mainPanel.add(northContainer, BorderLayout.NORTH);

		cardsPanel = new JPanel();
		cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
		cardsPanel.setBackground(Color.WHITE);
		cardsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

		scrollPane = new JScrollPane(cardsPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setBorder(null);

		mainPanel.add(scrollPane, BorderLayout.CENTER);
	}

	private JPanel createTopPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		logoPanel.setBackground(Color.WHITE);

		ImageIcon logoIcon = ImageLoader.loadLogo();
		JLabel logoLabel = new JLabel();
		logoLabel.setHorizontalAlignment(SwingConstants.LEFT);

		if (logoIcon != null && logoIcon.getImage() != null) {
			logoLabel.setIcon(logoIcon);
			logoLabel.setText(null);
		} else {
			logoLabel.setText("МАГАЗИН ОБУВИ");
			logoLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
		}

		logoPanel.add(logoLabel);
		panel.add(logoPanel, BorderLayout.WEST);

		JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		userPanel.setBackground(Color.WHITE);

		String fio = currentUser.getFullName();
		if (fio == null || fio.isBlank()) {
			fio = currentUser.getLogin();
		}

		JLabel userLabel = new JLabel("Пользователь: " + fio);
		userLabel.setFont(new Font("Times New Roman", Font.PLAIN, 14));

		JButton logout = new JButton("Выйти");
		logout.setBackground(Color.decode("#00FA9A"));
		logout.setOpaque(true);
		logout.setContentAreaFilled(true);
		logout.setBorderPainted(false);
		logout.setFocusPainted(false);
		logout.setFont(new Font("Times New Roman", Font.BOLD, 12));
		logout.addActionListener(e -> logout());

		userPanel.add(userLabel);
		userPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		userPanel.add(logout);

		panel.add(userPanel, BorderLayout.EAST);

		return panel;
	}

	private JPanel createControlPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.decode("#7FFF00"));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		// ЛЕВАЯ ПАНЕЛЬ - ПОИСК, ФИЛЬТР, СОРТИРОВКА
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		leftPanel.setBackground(Color.decode("#7FFF00"));

		// Поиск, фильтр и сортировка - ТОЛЬКО для менеджера и админа
		if (currentUser.isManager() || currentUser.isAdmin()) {
			leftPanel.add(new JLabel("🔍 Поиск:"));
			searchField = new JTextField(20);
			searchField.setToolTipText("Поиск по артикулу, названию, описанию, категории, производителю, поставщику");
			leftPanel.add(searchField);

			leftPanel.add(new JLabel("Поставщик:"));
			supplierFilterCombo = new JComboBox<>();
			supplierFilterCombo.addItem("Все поставщики");
			leftPanel.add(supplierFilterCombo);

			leftPanel.add(new JLabel("Сортировка:"));
			sortCombo = new JComboBox<>(
					new String[]{"Без сортировки", "Количество (возрастание)", "Количество (убывание)"});
			leftPanel.add(sortCombo);

			addSearchAndFilterListeners();
		}

		panel.add(leftPanel, BorderLayout.WEST);

		// ПРАВАЯ ПАНЕЛЬ - КНОПКИ ДЕЙСТВИЙ
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		rightPanel.setBackground(Color.decode("#7FFF00"));

		// КНОПКА "ЗАКАЗЫ" для менеджера и администратора
		if (currentUser.isManager() || currentUser.isAdmin()) {
			JButton ordersButton = new JButton("📋 Заказы");
			ordersButton.setBackground(Color.decode("#FFA500"));
			ordersButton.setForeground(Color.BLACK);
			ordersButton.setFont(new Font("Times New Roman", Font.BOLD, 12));
			ordersButton.setFocusPainted(false);
			ordersButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
			ordersButton.addActionListener(e -> openOrderList());
			rightPanel.add(ordersButton);
		}

		// КНОПКА "ДОБАВИТЬ ТОВАР" только для администратора
		if (currentUser.isAdmin()) {
			JButton addButton = new JButton("+ Добавить товар");
			addButton.setBackground(Color.decode("#00FA9A"));
			addButton.setForeground(Color.BLACK);
			addButton.setFont(new Font("Times New Roman", Font.BOLD, 12));
			addButton.setFocusPainted(false);
			addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
			addButton.addActionListener(e -> openAddProductForm());
			rightPanel.add(addButton);
		}

		panel.add(rightPanel, BorderLayout.EAST);

		return panel;
	}

	private void openOrderList() {
		// Скрываем текущее окно и открываем окно заказов
		this.setVisible(false);
		OrderListFrame orderListFrame = new OrderListFrame(this, currentUser);
		orderListFrame.setVisible(true);
	}

	private JPanel createCatalogPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.decode("#7FFF00"));

		JLabel title = new JLabel("КАТАЛОГ ТОВАРОВ");
		title.setFont(new Font("Times New Roman", Font.BOLD, 24));

		panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
		panel.add(title, BorderLayout.WEST);

		return panel;
	}

	private void addSearchAndFilterListeners() {
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				applyFiltersAndRefresh();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				applyFiltersAndRefresh();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				applyFiltersAndRefresh();
			}
		});

		supplierFilterCombo.addActionListener(e -> applyFiltersAndRefresh());
		sortCombo.addActionListener(e -> applyFiltersAndRefresh());
	}

	private void loadSuppliersForFilter() {
		if (supplierFilterCombo == null)
			return;

		supplierFilterCombo.removeAllItems();
		supplierFilterCombo.addItem("Все поставщики");

		if (allProducts != null) {
			allProducts.stream().map(p -> p.getSupplier().getName()).distinct().sorted()
					.forEach(supplierFilterCombo::addItem);
		}
	}

	private void applyFiltersAndRefresh() {
		if (allProducts == null)
			return;

		String searchText = getSearchText();
		String selectedSupplier = getSelectedSupplier();
		String sortOption = getSortOption();

		List<Product> filtered = allProducts;
		filtered = applySearchFilter(filtered, searchText);
		filtered = applySupplierFilter(filtered, selectedSupplier);
		filtered = applySorting(filtered, sortOption);

		filteredAndSearchedProducts = filtered;
		refreshCardsDisplay();
	}

	private String getSearchText() {
		return searchField != null ? searchField.getText() : "";
	}

	private String getSelectedSupplier() {
		return supplierFilterCombo != null ? (String) supplierFilterCombo.getSelectedItem() : "Все поставщики";
	}

	private String getSortOption() {
		return sortCombo != null ? (String) sortCombo.getSelectedItem() : "Без сортировки";
	}

	private List<Product> applySearchFilter(List<Product> products, String searchText) {
		if (searchText == null || searchText.trim().isEmpty()) {
			return products;
		}
		String lowerSearch = searchText.toLowerCase().trim();
		return products.stream().filter(p -> matchesSearch(p, lowerSearch)).collect(Collectors.toList());
	}

	private List<Product> applySupplierFilter(List<Product> products, String selectedSupplier) {
		if (selectedSupplier == null || selectedSupplier.equals("Все поставщики")) {
			return products;
		}
		return products.stream().filter(p -> p.getSupplier().getName().equals(selectedSupplier))
				.collect(Collectors.toList());
	}

	private List<Product> applySorting(List<Product> products, String sortOption) {
		if (sortOption == null)
			return products;

		List<Product> sorted = new ArrayList<>(products);
		if (sortOption.equals("Количество (возрастание)")) {
			sorted.sort((p1, p2) -> Integer.compare(getQuantity(p1), getQuantity(p2)));
		} else if (sortOption.equals("Количество (убывание)")) {
			sorted.sort((p1, p2) -> Integer.compare(getQuantity(p2), getQuantity(p1)));
		}
		return sorted;
	}

	private boolean matchesSearch(Product p, String searchText) {
		String lowerSearch = searchText.toLowerCase().trim();

		return matchesField(p.getArticle(), lowerSearch) || matchesField(p.getName(), lowerSearch)
				|| matchesDescription(p.getDescription(), lowerSearch)
				|| matchesField(p.getCategory().getName(), lowerSearch)
				|| matchesField(p.getManufacturer().getName(), lowerSearch)
				|| matchesField(p.getSupplier().getName(), lowerSearch);
	}

	private boolean matchesField(String field, String searchText) {
		return field != null && field.toLowerCase().contains(searchText);
	}

	private boolean matchesDescription(String description, String searchText) {
		return description != null && description.toLowerCase().contains(searchText);
	}

	private int getQuantity(Product p) {
		return p.getQuantity() != null ? p.getQuantity() : 0;
	}

	private void loadProducts() {
		allProducts = productService.getAllProducts();
		loadSuppliersForFilter();
		filteredAndSearchedProducts = allProducts;
		refreshCardsDisplay();
	}

	// При обновлении списка товаров очищаем кэш для обновленных фото
	private void refreshCardsDisplay() {
		cardsPanel.removeAll();

		if (filteredAndSearchedProducts == null || filteredAndSearchedProducts.isEmpty()) {
			JLabel emptyLabel = new JLabel("Товары не найдены");
			emptyLabel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
			emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			cardsPanel.add(emptyLabel);
		} else {
			for (Product product : filteredAndSearchedProducts) {
				// Очищаем кэш для этого товара
				if (product.getPhoto() != null) {
					ImageLoader.refreshImage(product.getPhoto());
				}
				JPanel card = createProductCard(product);
				cardsPanel.add(card);
				cardsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
			}
		}

		cardsPanel.revalidate();
		cardsPanel.repaint();

		SwingUtilities.invokeLater(() -> {
			if (scrollPane != null && scrollPane.getVerticalScrollBar() != null) {
				scrollPane.getVerticalScrollBar().setValue(0);
			}
		});
	}

	private JPanel createProductCard(Product product) {
		boolean inStock = product.getQuantity() != null && product.getQuantity() > 0;
		double discount = product.getDiscount() != null ? product.getDiscount().doubleValue() : 0;

		Color bg = discount > 15 ? Color.decode("#2E8B57") : Color.WHITE;
		Color textColor = (discount > 15 && inStock) ? Color.WHITE : Color.BLACK;
		Color borderColor = Color.decode("#00FA9A");

		JPanel card = new JPanel(new BorderLayout(15, 10));
		card.setBackground(bg);
		card.setBorder(BorderFactory.createLineBorder(borderColor, 2));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

		setupCardDoubleClick(card, product);

		JPanel photoPanel = createPhotoPanel(product, bg, borderColor);
		JPanel infoPanel = createInfoPanel(product, bg, borderColor, textColor, inStock, discount);
		JPanel rightPanel = createRightPanel(product, bg, borderColor, textColor, discount);

		card.add(photoPanel, BorderLayout.WEST);
		card.add(infoPanel, BorderLayout.CENTER);
		card.add(rightPanel, BorderLayout.EAST);

		return card;
	}

	private JPanel createPhotoPanel(Product product, Color bg, Color borderColor) {
		JPanel photoPanel = new JPanel(new BorderLayout());
		photoPanel.setBackground(bg);
		photoPanel.setPreferredSize(new Dimension(150, 120));
		photoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(borderColor, 1),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		JLabel image = new JLabel();
		image.setHorizontalAlignment(SwingConstants.CENTER);
		image.setVerticalAlignment(SwingConstants.CENTER);
		image.setIcon(ImageLoader.loadImage(product.getPhoto(), 120, 120));
		photoPanel.add(image);

		return photoPanel;
	}

	private JPanel createInfoPanel(Product product, Color bg, Color borderColor, Color textColor, boolean inStock,
			double discount) {
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.setBackground(bg);
		infoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(borderColor, 1),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));

		JPanel headerPanel = createHeaderPanel(product, textColor);
		infoPanel.add(headerPanel);
		infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));

		infoPanel.add(createInfoLabel("Описание: " + safe(product.getDescription()), textColor));
		infoPanel.add(createInfoLabel("Производитель: " + product.getManufacturer().getName(), textColor));
		infoPanel.add(createInfoLabel("Поставщик: " + product.getSupplier().getName(), textColor));

		addPriceInfo(infoPanel, product, textColor, discount);

		infoPanel.add(createInfoLabel("Ед. изм.: " + product.getUnit().getName(), textColor));

		addQuantityInfo(infoPanel, product, bg, textColor, inStock);

		return infoPanel;
	}

	private JPanel createHeaderPanel(Product product, Color textColor) {
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(null);
		headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel title = new JLabel(product.getCategory().getName() + " | " + product.getName());
		title.setFont(new Font("Times New Roman", Font.BOLD, 14));
		title.setForeground(textColor);
		headerPanel.add(title, BorderLayout.WEST);

		if (currentUser.isAdmin()) {
			JButton deleteButton = createDeleteButton(product);
			headerPanel.add(deleteButton, BorderLayout.EAST);
		}

		return headerPanel;
	}

	private JButton createDeleteButton(Product product) {
		JButton deleteButton = new JButton("Удалить");
		deleteButton.setBackground(Color.decode("#FF6B6B"));
		deleteButton.setForeground(Color.BLACK);
		deleteButton.setFont(new Font("Times New Roman", Font.BOLD, 11));
		deleteButton.setPreferredSize(new Dimension(75, 25));
		deleteButton.setMaximumSize(new Dimension(75, 25));
		deleteButton.setFocusPainted(false);
		deleteButton.addActionListener(e -> handleDelete(product));
		return deleteButton;
	}

	private void handleDelete(Product product) {
		if (ConfirmationDialog.confirmDelete(this, product.getName())) {
			try {
				productService.deleteProduct(product.getArticle());
				refreshProducts();
				JOptionPane.showMessageDialog(this, "Товар успешно удалён", "Успешно", JOptionPane.INFORMATION_MESSAGE);
			} catch (IllegalStateException ex) {
				ConfirmationDialog.showDeleteError(this, product.getName());
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Ошибка при удалении: " + ex.getMessage(), "Ошибка",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void addPriceInfo(JPanel panel, Product product, Color textColor, double discount) {
		if (discount > 0) {
			BigDecimal finalPrice = product.getPrice().multiply(BigDecimal.valueOf(1 - discount / 100));
			String priceHtml = "<html>Цена: <font color='red'><strike>" + priceFormat.format(product.getPrice())
					+ " ₽</strike></font>" + "  <font color='black'><b>" + priceFormat.format(finalPrice)
					+ " ₽</b></font></html>";
			JLabel priceLabel = new JLabel(priceHtml);
			priceLabel.setFont(new Font("Times New Roman", Font.PLAIN, 12));
			priceLabel.setForeground(textColor);
			priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			priceLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel.add(priceLabel);
		} else {
			panel.add(createInfoLabel("Цена: " + priceFormat.format(product.getPrice()) + " ₽", textColor));
		}
	}

	private void addQuantityInfo(JPanel panel, Product product, Color bg, Color textColor, boolean inStock) {
		JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		qtyPanel.setBackground(bg);
		qtyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		qtyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

		JLabel qty = new JLabel("Количество: " + product.getQuantity());
		qty.setFont(new Font("Times New Roman", Font.PLAIN, 12));

		if (!inStock) {
			qty.setOpaque(true);
			qty.setBackground(Color.CYAN);
			qty.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		} else {
			qty.setForeground(textColor);
		}

		qtyPanel.add(qty);
		panel.add(qtyPanel);
	}

	private JPanel createRightPanel(Product product, Color bg, Color borderColor, Color textColor, double discount) {
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.setBackground(bg);
		rightPanel.setPreferredSize(new Dimension(140, 140));
		rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		if (discount > 0) {
			JPanel discountPanel = createDiscountPanel(bg, borderColor, textColor, discount);
			rightPanel.add(discountPanel, BorderLayout.CENTER);
		}

		return rightPanel;
	}

	private JPanel createDiscountPanel(Color bg, Color borderColor, Color textColor, double discount) {
		JPanel discountPanel = new JPanel(new GridBagLayout());
		discountPanel.setBackground(bg);
		discountPanel.setPreferredSize(new Dimension(120, 80));
		discountPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(borderColor, 1),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.CENTER;

		JLabel discountTitle = new JLabel("Действующая скидка");
		discountTitle.setFont(new Font("Times New Roman", Font.PLAIN, 10));
		discountTitle.setForeground(textColor);
		discountTitle.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridy = 0;
		discountPanel.add(discountTitle, gbc);

		JLabel discountLabel = new JLabel((int) discount + "%");
		discountLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
		discountLabel.setForeground(Color.RED);
		gbc.gridy = 1;
		discountPanel.add(discountLabel, gbc);

		return discountPanel;
	}

	private void setupCardDoubleClick(JPanel card, Product product) {
		if (currentUser.isAdmin()) {
			card.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						openEditProductForm(product);
					}
				}
			});
			card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	}

	// Вспомогательный метод - ВСЕГДА с LEFT_ALIGNMENT
	private JLabel createInfoLabel(String text, Color textColor) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		label.setForeground(textColor);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		return label;
	}

	private void openAddProductForm() {
		if (isEditFormOpen.get()) {
			JOptionPane.showMessageDialog(this,
					"Окно редактирования уже открыто. Закройте его перед созданием нового товара.", "Внимание",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		ProductFormFrame form = new ProductFormFrame(this, null);
		form.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent e) {
				isEditFormOpen.set(false);
			}
		});
		isEditFormOpen.set(true);
		form.setVisible(true);
	}

	private void openEditProductForm(Product product) {
		if (isEditFormOpen.get()) {
			JOptionPane.showMessageDialog(this,
					"Окно редактирования уже открыто. Закройте его перед редактированием другого товара.", "Внимание",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		ProductFormFrame form = new ProductFormFrame(this, product);
		form.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent e) {
				isEditFormOpen.set(false);
			}
		});
		isEditFormOpen.set(true);
		form.setVisible(true);
	}

	public void refreshProducts() {
		allProducts = productService.getAllProducts();
		loadSuppliersForFilter();
		applyFiltersAndRefresh();
	}

	private String safe(String text) {
		return text == null ? "" : text;
	}

	private void logout() {
		dispose();
		new LoginFrame().setVisible(true);
	}
}
