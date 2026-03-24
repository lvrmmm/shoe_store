package misis.ignatova_maria.shoe_store.gui;

import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.*;

import javax.swing.table.AbstractTableModel;
import javax.swing.*;

import misis.ignatova_maria.shoe_store.entity.*;
import misis.ignatova_maria.shoe_store.service.OrderService;
import misis.ignatova_maria.shoe_store.service.ProductService;
import misis.ignatova_maria.shoe_store.service.UserService;
import misis.ignatova_maria.shoe_store.util.ImageLoader;
import misis.ignatova_maria.shoe_store.util.SpringContext;

public class OrderFormFrame extends JFrame {

	private final OrderService orderService;
	private final ProductService productService;
	private final UserService userService;
	private final OrderListFrame parentFrame;
	private final Order editingOrder;
	private final boolean isEditMode;

	// Основные поля
	private JComboBox<User> userCombo;
	private JComboBox<OrderStatus> statusCombo;
	private JComboBox<PickupPoint> pickupPointCombo;
	private JSpinner orderDateSpinner;
	private JSpinner deliveryDateSpinner;
	private JTextField pickupCodeField;

	// Таблица товаров в заказе
	private JTable itemsTable;
	private OrderItemsTableModel itemsTableModel;
	private List<OrderItem> orderItems;

	// Добавление товара
	private JComboBox<Product> productCombo;
	private JSpinner quantitySpinner;

	public OrderFormFrame(OrderListFrame parent, Order orderToEdit, User currentUser) {
		this.orderService = SpringContext.getBean(OrderService.class);
		this.productService = SpringContext.getBean(ProductService.class);
		this.userService = SpringContext.getBean(UserService.class);
		this.parentFrame = parent;
		this.editingOrder = orderToEdit;
		this.isEditMode = (orderToEdit != null);
		this.orderItems = new ArrayList<>();

		initUI();
		loadData();
		setLocationRelativeTo(parent);
	}

	private void initUI() {
		setTitle(isEditMode ? "Редактирование заказа" : "Добавление заказа");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(900, 700);
		setMinimumSize(new Dimension(800, 650));
		setLocationRelativeTo(null);
		setIconImage(ImageLoader.loadIcon().getImage());

		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		mainPanel.setBackground(Color.WHITE);

		JPanel formPanel = createFormPanel();
		JPanel addItemPanel = createAddItemPanel();
		JPanel itemsPanel = createItemsPanel();
		JPanel buttonPanel = createButtonPanel();

		JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
		centerPanel.add(addItemPanel, BorderLayout.NORTH);
		centerPanel.add(itemsPanel, BorderLayout.CENTER);

		mainPanel.add(formPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		add(mainPanel);
	}

	private JPanel createFormPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createTitledBorder("Информация о заказе"));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 10, 5, 10);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		int row = 0;

		// Покупатель (выбор из существующих пользователей)
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		panel.add(new JLabel("Покупатель:*"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		userCombo = new JComboBox<>();
		loadUsers();
		userCombo.setRenderer(new UserListCellRenderer());
		panel.add(userCombo, gbc);
		row++;

		// Статус заказа
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		panel.add(new JLabel("Статус заказа:*"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		statusCombo = new JComboBox<>();
		loadStatuses();
		statusCombo.setRenderer(new StatusListCellRenderer());
		panel.add(statusCombo, gbc);
		row++;

		// Пункт выдачи
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		panel.add(new JLabel("Адрес пункта выдачи:*"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		pickupPointCombo = new JComboBox<>();
		loadPickupPoints();
		pickupPointCombo.setRenderer(new PickupPointListCellRenderer());
		panel.add(pickupPointCombo, gbc);
		row++;

		// Дата заказа
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		panel.add(new JLabel("Дата заказа:*"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		SpinnerDateModel dateModel = new SpinnerDateModel();
		orderDateSpinner = new JSpinner(dateModel);
		JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(orderDateSpinner, "dd.MM.yyyy");
		orderDateSpinner.setEditor(dateEditor);
		panel.add(orderDateSpinner, gbc);
		row++;

		// Дата выдачи
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		panel.add(new JLabel("Дата выдачи:*"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		SpinnerDateModel deliveryModel = new SpinnerDateModel();
		deliveryDateSpinner = new JSpinner(deliveryModel);
		JSpinner.DateEditor deliveryEditor = new JSpinner.DateEditor(deliveryDateSpinner, "dd.MM.yyyy");
		deliveryDateSpinner.setEditor(deliveryEditor);
		panel.add(deliveryDateSpinner, gbc);
		row++;

		// Код получения (только для чтения)
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		panel.add(new JLabel("Код получения:"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		pickupCodeField = new JTextField(20);
		pickupCodeField.setEditable(false);
		pickupCodeField.setBackground(Color.LIGHT_GRAY);
		pickupCodeField.setToolTipText("Генерируется автоматически при сохранении");
		panel.add(pickupCodeField, gbc);

		return panel;
	}

	private JPanel createAddItemPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		panel.setBackground(Color.decode("#F0F0F0"));
		panel.setBorder(BorderFactory.createTitledBorder("Добавить товар в заказ"));

		panel.add(new JLabel("Артикул товара:*"));

		productCombo = new JComboBox<>();
		loadProducts();
		productCombo.setRenderer(new ProductListCellRenderer());
		productCombo.setPreferredSize(new Dimension(350, 25));
		panel.add(productCombo);

		panel.add(new JLabel("Количество:*"));

		quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
		quantitySpinner.setPreferredSize(new Dimension(80, 25));
		panel.add(quantitySpinner);

		JButton addItemButton = new JButton("+ Добавить");
		addItemButton.setBackground(Color.decode("#00FA9A"));
		addItemButton.addActionListener(e -> addItemToOrder());
		panel.add(addItemButton);

		JButton removeItemButton = new JButton("− Удалить выбранный");
		removeItemButton.setBackground(Color.decode("#FFA07A"));
		removeItemButton.addActionListener(e -> removeSelectedItem());
		panel.add(removeItemButton);

		return panel;
	}

	private JPanel createItemsPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Товары в заказе"));

		itemsTableModel = new OrderItemsTableModel();
		itemsTable = new JTable(itemsTableModel);
		itemsTable.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		itemsTable.setRowHeight(25);
		itemsTable.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 12));
		itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPane = new JScrollPane(itemsTable);
		scrollPane.setPreferredSize(new Dimension(panel.getWidth(), 180));
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	private JPanel createButtonPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
		panel.setBackground(Color.WHITE);

		JButton saveButton = new JButton(isEditMode ? "Сохранить изменения" : "Добавить заказ");
		saveButton.setBackground(Color.decode("#00FA9A"));
		saveButton.setFont(new Font("Times New Roman", Font.BOLD, 12));
		saveButton.addActionListener(e -> saveOrder());

		JButton cancelButton = new JButton("Отмена");
		cancelButton.setBackground(Color.decode("#FFA07A"));
		cancelButton.addActionListener(e -> dispose());

		panel.add(saveButton);
		panel.add(cancelButton);

		return panel;
	}

	private void loadUsers() {
		List<User> allUsers = userService.getAllUsers();
		// Показываем всех пользователей (можно отфильтровать по роли "Авторизированный
		// клиент")
		for (User user : allUsers) {
			userCombo.addItem(user);
		}
	}

	private void loadStatuses() {
		List<OrderStatus> statuses = orderService.getAllStatuses();
		for (OrderStatus status : statuses) {
			statusCombo.addItem(status);
		}
	}

	private void loadPickupPoints() {
		List<PickupPoint> points = orderService.getAllPickupPoints();
		for (PickupPoint point : points) {
			pickupPointCombo.addItem(point);
		}
	}

	private void loadProducts() {
		List<Product> products = productService.getAllProducts();
		for (Product product : products) {
			productCombo.addItem(product);
		}
	}

	private boolean canAddQuantity(Product product, int additionalQuantity) {
		int currentInOrder = orderItems.stream()
				.filter(item -> item.getProduct().getArticle().equals(product.getArticle()))
				.mapToInt(OrderItem::getQuantity).sum();

		int totalAfterAdd = currentInOrder + additionalQuantity;
		int availableInStock = product.getQuantity() != null ? product.getQuantity() : 0;

		if (totalAfterAdd > availableInStock) {
			JOptionPane.showMessageDialog(this,
					String.format("""
							Недостаточно товара на складе!

							Товар: %s
							Артикул: %s
							Уже в заказе: %d шт.
							Пытаетесь добавить: %d шт.
							Всего будет: %d шт.
							Доступно на складе: %d шт.""", product.getName(), product.getArticle(), currentInOrder,
							additionalQuantity, totalAfterAdd, availableInStock),
					"Ошибка добавления", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	private void addItemToOrder() {
		Product selectedProduct = (Product) productCombo.getSelectedItem();
		int quantity = (Integer) quantitySpinner.getValue();

		if (selectedProduct == null) {
			JOptionPane.showMessageDialog(this, "Выберите товар", "Ошибка", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (quantity <= 0) {
			JOptionPane.showMessageDialog(this, "Количество должно быть больше 0", "Ошибка", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!canAddQuantity(selectedProduct, quantity)) {
			return;
		}

		Optional<OrderItem> existing = orderItems.stream()
				.filter(item -> item.getProduct().getArticle().equals(selectedProduct.getArticle())).findFirst();

		if (existing.isPresent()) {
			existing.get().setQuantity(existing.get().getQuantity() + quantity);
		} else {
			OrderItem newItem = new OrderItem();
			newItem.setProduct(selectedProduct);
			newItem.setQuantity(quantity);
			orderItems.add(newItem);
		}

		itemsTableModel.fireTableDataChanged();

		productCombo.setSelectedIndex(0);
		quantitySpinner.setValue(1);

		JOptionPane.showMessageDialog(this, "Товар добавлен в заказ", "Успешно", JOptionPane.INFORMATION_MESSAGE);
	}

	private void removeSelectedItem() {
		int selectedRow = itemsTable.getSelectedRow();
		if (selectedRow >= 0 && selectedRow < orderItems.size()) {
			orderItems.remove(selectedRow);
			itemsTableModel.fireTableDataChanged();
		} else {
			JOptionPane.showMessageDialog(this, "Выберите товар для удаления", "Внимание", JOptionPane.WARNING_MESSAGE);
		}
	}

	private void loadData() {
		if (isEditMode && editingOrder != null) {
			// Выбираем покупателя
			if (editingOrder.getUser() != null) {
				for (int i = 0; i < userCombo.getItemCount(); i++) {
					User user = userCombo.getItemAt(i);
					if (user != null && user.getId().equals(editingOrder.getUser().getId())) {
						userCombo.setSelectedIndex(i);
						break;
					}
				}
			}

			// Выбираем статус
			if (editingOrder.getStatus() != null) {
				for (int i = 0; i < statusCombo.getItemCount(); i++) {
					OrderStatus status = statusCombo.getItemAt(i);
					if (status != null && status.getId().equals(editingOrder.getStatus().getId())) {
						statusCombo.setSelectedIndex(i);
						break;
					}
				}
			}

			// Выбираем пункт выдачи
			if (editingOrder.getPickupPoint() != null) {
				for (int i = 0; i < pickupPointCombo.getItemCount(); i++) {
					PickupPoint point = pickupPointCombo.getItemAt(i);
					if (point != null && point.getId().equals(editingOrder.getPickupPoint().getId())) {
						pickupPointCombo.setSelectedIndex(i);
						break;
					}
				}
			}

			// Даты
			if (editingOrder.getOrderDate() != null) {
				Date date = Date.from(editingOrder.getOrderDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
				orderDateSpinner.setValue(date);
			}
			if (editingOrder.getDeliveryDate() != null) {
				Date date = Date.from(editingOrder.getDeliveryDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
				deliveryDateSpinner.setValue(date);
			}

			// Код получения
			if (editingOrder.getPickupCode() != null) {
				pickupCodeField.setText(editingOrder.getPickupCode());
			}

			// Товары в заказе
			if (editingOrder.getOrderItems() != null) {
				orderItems = new ArrayList<>(editingOrder.getOrderItems());
				itemsTableModel.fireTableDataChanged();
			}
		} else {
			// Новый заказ
			orderDateSpinner.setValue(new Date());
			deliveryDateSpinner
					.setValue(Date.from(LocalDate.now().plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant()));
			pickupCodeField.setText("(будет сгенерирован автоматически)");
		}
	}

	private void saveOrder() {
		if (!validateForm()) {
			return;
		}

		if (orderItems.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Добавьте хотя бы один товар в заказ", "Ошибка валидации",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			Order order;

			if (isEditMode) {
				order = editingOrder;
				// Очищаем старые товары
				if (order.getOrderItems() != null) {
					order.getOrderItems().clear();
				} else {
					order.setOrderItems(new ArrayList<>());
				}
				order.getOrderItems().addAll(orderItems);
				for (OrderItem item : orderItems) {
					item.setOrder(order);
				}
			} else {
				order = new Order();
				order.setUser((User) userCombo.getSelectedItem()); // Выбранный покупатель
				order.setOrderItems(new ArrayList<>());
				order.getOrderItems().addAll(orderItems);
				for (OrderItem item : orderItems) {
					item.setOrder(order);
				}
			}

			order.setStatus((OrderStatus) statusCombo.getSelectedItem());
			order.setPickupPoint((PickupPoint) pickupPointCombo.getSelectedItem());

			Date orderDate = (Date) orderDateSpinner.getValue();
			order.setOrderDate(orderDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

			Date deliveryDate = (Date) deliveryDateSpinner.getValue();
			order.setDeliveryDate(deliveryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

			if (isEditMode) {
				orderService.updateOrder(order);
				JOptionPane.showMessageDialog(this, "Заказ успешно обновлён", "Успешно",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				// СОЗДАЕМ ЗАКАЗ
				Order savedOrder = orderService.createOrder(order);
				String code = orderService.generatePickupCode(savedOrder);
				savedOrder.setPickupCode(code);

				// ВАЖНО: Обновляем только код, НЕ вызываем updateOrder!
				// Просто сохраняем заказ с кодом
				orderService.updatePickupCode(savedOrder.getId(), code);

				JOptionPane.showMessageDialog(this, "Заказ успешно добавлен\nКод получения: " + code, "Успешно",
						JOptionPane.INFORMATION_MESSAGE);
			}

			if (parentFrame != null) {
				parentFrame.refreshOrders();
			}

			dispose();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Ошибка при сохранении заказа: " + e.getMessage(), "Ошибка",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private boolean validateForm() {
		if (userCombo.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(this, "Выберите покупателя", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (statusCombo.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(this, "Выберите статус заказа", "Ошибка валидации",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (pickupPointCombo.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(this, "Выберите адрес пункта выдачи", "Ошибка валидации",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		Date orderDate = (Date) orderDateSpinner.getValue();
		Date deliveryDate = (Date) deliveryDateSpinner.getValue();

		if (deliveryDate.before(orderDate)) {
			JOptionPane.showMessageDialog(this, "Дата выдачи не может быть раньше даты заказа", "Ошибка валидации",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	// Модель таблицы товаров
	private class OrderItemsTableModel extends AbstractTableModel {
		private final String[] columns = {"Артикул", "Наименование товара", "Количество"};

		@Override
		public int getRowCount() {
			return orderItems.size();
		}

		@Override
		public int getColumnCount() {
			return columns.length;
		}

		@Override
		public String getColumnName(int column) {
			return columns[column];
		}

		@Override
		public Object getValueAt(int row, int column) {
			OrderItem item = orderItems.get(row);
			return switch (column) {
				case 0 -> item.getProduct().getArticle();
				case 1 -> item.getProduct().getName();
				case 2 -> item.getQuantity();
				default -> null;
			};
		}
	}

	// Рендереры
	private static class UserListCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof User) {
				setText(((User) value).getFullName() + " (" + ((User) value).getLogin() + ")");
			}
			return this;
		}
	}

	private static class StatusListCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof OrderStatus) {
				setText(((OrderStatus) value).getName());
			}
			return this;
		}
	}

	private static class PickupPointListCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof PickupPoint) {
				setText(((PickupPoint) value).getFullAddress());
			}
			return this;
		}
	}

	private static class ProductListCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof Product p) {
				int stock = p.getQuantity() != null ? p.getQuantity() : 0;
				setText(p.getArticle() + " — " + p.getName() + " (в наличии: " + stock + " шт.)");
			}
			return this;
		}
	}
}
