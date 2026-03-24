package misis.ignatova_maria.shoe_store.gui.frames;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.swing.*;

import misis.ignatova_maria.shoe_store.entity.Product;
import misis.ignatova_maria.shoe_store.entity.User;
import misis.ignatova_maria.shoe_store.gui.managers.ProductCardManager;
import misis.ignatova_maria.shoe_store.gui.panels.ProductFilterPanel;
import misis.ignatova_maria.shoe_store.service.ProductService;
import misis.ignatova_maria.shoe_store.util.ImageLoader;
import misis.ignatova_maria.shoe_store.util.SpringContext;

public class ProductListFrame extends JFrame {

	private final ProductService productService;
	private final User currentUser;
	private final DecimalFormat priceFormat;

	private JPanel cardsPanel;
	private JScrollPane scrollPane;
	private ProductFilterPanel filterPanel;
	private ProductCardManager cardManager;

	private List<Product> allProducts;
	private List<Product> filteredProducts;
	private final AtomicBoolean isEditFormOpen = new AtomicBoolean(false);

	public ProductListFrame(User user) {
		this.productService = SpringContext.getBean(ProductService.class);
		this.currentUser = user;
		this.priceFormat = new DecimalFormat("#,##0.00");

		initUI();
		setupComponents();
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

		if (currentUser.isManager() || currentUser.isAdmin()) {
			filterPanel = new ProductFilterPanel();
			filterPanel.setOnFilterChanged(v -> applyFiltersAndRefresh());
		} else {
			filterPanel = null;
		}

		JPanel catalogPanel = createCatalogPanel();

		JPanel northContainer = new JPanel();
		northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
		northContainer.add(topPanel);
		if (filterPanel != null) {
			northContainer.add(filterPanel);
		}
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

	private void setupComponents() {
		cardManager = new ProductCardManager(productService, currentUser, this, priceFormat);
		cardManager.setOnRefreshCallback(this::refreshProducts);
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

	private JPanel createCatalogPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.decode("#7FFF00"));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		JLabel title = new JLabel("КАТАЛОГ ТОВАРОВ");
		title.setFont(new Font("Times New Roman", Font.BOLD, 24));
		panel.add(title, BorderLayout.WEST);

		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		rightPanel.setBackground(Color.decode("#7FFF00"));

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
		this.setVisible(false);
		OrderListFrame orderListFrame = new OrderListFrame(this, currentUser);
		orderListFrame.setVisible(true);
	}

	private void loadProducts() {
		allProducts = productService.getAllProducts();
		if (filterPanel != null) {
			filterPanel.updateSuppliers(allProducts);
		}
		filteredProducts = allProducts;
		refreshCardsDisplay();
	}

	private void applyFiltersAndRefresh() {
		if (allProducts == null || filterPanel == null) {
			return;
		}

		List<Product> filtered = allProducts;
		filtered = applySearchFilter(filtered, filterPanel.getSearchText());
		filtered = applySupplierFilter(filtered, filterPanel.getSelectedSupplier());
		filtered = applySorting(filtered, filterPanel.getSortOption());

		filteredProducts = filtered;
		refreshCardsDisplay();
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
		if (sortOption == null) {
			return products;
		}

		List<Product> sorted = new ArrayList<>(products);
		if (sortOption.equals("Количество (возрастание)")) {
			sorted.sort((p1, p2) -> Integer.compare(getQuantity(p1), getQuantity(p2)));
		} else if (sortOption.equals("Количество (убывание)")) {
			sorted.sort((p1, p2) -> Integer.compare(getQuantity(p2), getQuantity(p1)));
		}
		return sorted;
	}

	private boolean matchesSearch(Product p, String searchText) {
		return containsIgnoreCase(p.getArticle(), searchText) || containsIgnoreCase(p.getName(), searchText)
				|| containsIgnoreCase(p.getDescription(), searchText)
				|| containsIgnoreCase(p.getCategory().getName(), searchText)
				|| containsIgnoreCase(p.getManufacturer().getName(), searchText)
				|| containsIgnoreCase(p.getSupplier().getName(), searchText);
	}

	private boolean containsIgnoreCase(String text, String searchText) {
		if (text == null) {
			return false;
		}
		return text.toLowerCase().contains(searchText);
	}

	private int getQuantity(Product p) {
		return p.getQuantity() != null ? p.getQuantity() : 0;
	}

	private void refreshCardsDisplay() {
		cardsPanel.removeAll();

		if (filteredProducts == null || filteredProducts.isEmpty()) {
			JLabel emptyLabel = new JLabel("Товары не найдены");
			emptyLabel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
			emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			cardsPanel.add(emptyLabel);
		} else {
			for (Product product : filteredProducts) {
				if (product.getPhoto() != null) {
					ImageLoader.refreshImage(product.getPhoto());
				}
				JPanel card = cardManager.createProductCard(product);
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
				refreshProducts();
			}
		});
		isEditFormOpen.set(true);
		form.setVisible(true);
	}

	public void refreshProducts() {
		allProducts = productService.getAllProducts();
		if (filterPanel != null) {
			filterPanel.updateSuppliers(allProducts);
		}
		filteredProducts = allProducts;
		refreshCardsDisplay();
	}

	private void logout() {
		dispose();
		new LoginFrame().setVisible(true);
	}
}
