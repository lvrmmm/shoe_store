package misis.ignatova_maria.shoe_store.gui;

import java.awt.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;

import misis.ignatova_maria.shoe_store.entity.*;
import misis.ignatova_maria.shoe_store.gui.model.OrderItemsTableModel;
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

	// Таблица товаров
	private JTable itemsTable;
	private OrderItemManager itemManager;

	// Добавление товара
	private JComboBox<Product> productCombo;
	private JSpinner quantitySpinner;

	// Валидатор и загрузчик
	private OrderFormValidator validator;
	private OrderFormDataLoader dataLoader;

	public OrderFormFrame(OrderListFrame parent, Order orderToEdit, User currentUser) {
		this.orderService = SpringContext.getBean(OrderService.class);
		this.productService = SpringContext.getBean(ProductService.class);
		this.userService = SpringContext.getBean(UserService.class);
		this.parentFrame = parent;
		this.editingOrder = orderToEdit;
		this.isEditMode = (orderToEdit != null);

		initUI();
		setupComponents();
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

		// Используем FormBuilder для создания панелей
		JPanel formPanel = createFormPanel();
		JPanel addItemPanel = createAddItemPanel();
		JPanel itemsPanel = createItemsPanel();
		JPanel buttonPanel = FormBuilder.createButtonPanel(createSaveButton(), createCancelButton());

		JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
		centerPanel.add(addItemPanel, BorderLayout.NORTH);
		centerPanel.add(itemsPanel, BorderLayout.CENTER);

		mainPanel.add(formPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		add(mainPanel);
	}

	private JPanel createFormPanel() {
		// Используем FormBuilder для создания панели с заголовком
		JPanel panel = FormBuilder.createFormPanel("Информация о заказе");
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 10, 5, 10);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		int row = 0;

		// Покупатель
		userCombo = new JComboBox<>();
		userCombo.setRenderer(new UserListCellRenderer());
		FormBuilder.addField(panel, gbc, "Покупатель:*", userCombo, row++);

		// Статус заказа
		statusCombo = new JComboBox<>();
		statusCombo.setRenderer(new StatusListCellRenderer());
		FormBuilder.addField(panel, gbc, "Статус заказа:*", statusCombo, row++);

		// Пункт выдачи
		pickupPointCombo = new JComboBox<>();
		pickupPointCombo.setRenderer(new PickupPointListCellRenderer());
		FormBuilder.addField(panel, gbc, "Адрес пункта выдачи:*", pickupPointCombo, row++);

		// Дата заказа
		orderDateSpinner = FormBuilder.createDateSpinner();
		FormBuilder.addField(panel, gbc, "Дата заказа:*", orderDateSpinner, row++);

		// Дата выдачи
		deliveryDateSpinner = FormBuilder.createDateSpinner();
		FormBuilder.addField(panel, gbc, "Дата выдачи:*", deliveryDateSpinner, row++);

		// Код получения
		pickupCodeField = new JTextField(20);
		pickupCodeField.setEditable(false);
		pickupCodeField.setBackground(Color.LIGHT_GRAY);
		pickupCodeField.setToolTipText("Генерируется автоматически при сохранении");
		FormBuilder.addField(panel, gbc, "Код получения:", pickupCodeField, row);

		return panel;
	}

	private JPanel createAddItemPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		panel.setBackground(Color.decode("#F0F0F0"));
		panel.setBorder(BorderFactory.createTitledBorder("Добавить товар в заказ"));

		panel.add(new JLabel("Артикул товара:*"));

		productCombo = new JComboBox<>();
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
		removeItemButton.addActionListener(e -> itemManager.removeSelectedItem());
		panel.add(removeItemButton);

		return panel;
	}

	private JPanel createItemsPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Товары в заказе"));

		JScrollPane scrollPane = new JScrollPane(itemsTable);
		scrollPane.setPreferredSize(new Dimension(panel.getWidth(), 180));
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	private JButton createSaveButton() {
		JButton saveButton = new JButton(isEditMode ? "Сохранить изменения" : "Добавить заказ");
		saveButton.setBackground(Color.decode("#00FA9A"));
		saveButton.setFont(new Font("Times New Roman", Font.BOLD, 12));
		saveButton.addActionListener(e -> saveOrder());
		return saveButton;
	}

	private JButton createCancelButton() {
		JButton cancelButton = new JButton("Отмена");
		cancelButton.setBackground(Color.decode("#FFA07A"));
		cancelButton.addActionListener(e -> dispose());
		return cancelButton;
	}

	private void setupComponents() {
		OrderItemsTableModel itemsTableModel = new OrderItemsTableModel(new ArrayList<>());
		itemsTable = new JTable(itemsTableModel);
		setupTable();

		itemManager = new OrderItemManager(this, itemsTable, itemsTableModel);
		validator = new OrderFormValidator(this, userCombo, statusCombo, pickupPointCombo, orderDateSpinner,
				deliveryDateSpinner);
		dataLoader = new OrderFormDataLoader(orderService, productService, userService);
	}

	private void setupTable() {
		itemsTable.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		itemsTable.setRowHeight(25);
		itemsTable.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 12));
		itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	private void loadData() {
		dataLoader.loadUsers(userCombo);
		dataLoader.loadStatuses(statusCombo);
		dataLoader.loadPickupPoints(pickupPointCombo);
		dataLoader.loadProducts(productCombo);

		dataLoader.loadOrderData(editingOrder, userCombo, statusCombo, pickupPointCombo, orderDateSpinner,
				deliveryDateSpinner, pickupCodeField, itemManager);
	}

	private void addItemToOrder() {
		Product selectedProduct = (Product) productCombo.getSelectedItem();
		int quantity = (Integer) quantitySpinner.getValue();

		if (itemManager.addItem(selectedProduct, quantity)) {
			productCombo.setSelectedIndex(0);
			quantitySpinner.setValue(1);
		}
	}

	private void saveOrder() {
		if (!validator.validate()) {
			return;
		}

		if (itemManager.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Добавьте хотя бы один товар в заказ", "Ошибка валидации",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			Order order = buildOrder();

			if (isEditMode) {
				orderService.updateOrder(order);
				showSuccess("Заказ успешно обновлён");
			} else {
				Order savedOrder = orderService.createOrder(order);
				String code = orderService.generatePickupCode(savedOrder);
				orderService.updatePickupCode(savedOrder.getId(), code);
				showSuccess("Заказ успешно добавлен\nКод получения: " + code);
			}

			if (parentFrame != null) {
				parentFrame.refreshOrders();
			}
			dispose();

		} catch (Exception e) {
			showError("Ошибка при сохранении заказа: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private Order buildOrder() {
		Order order = isEditMode ? editingOrder : new Order();

		order.setUser((User) userCombo.getSelectedItem());
		order.setStatus((OrderStatus) statusCombo.getSelectedItem());
		order.setPickupPoint((PickupPoint) pickupPointCombo.getSelectedItem());

		Date orderDate = (Date) orderDateSpinner.getValue();
		order.setOrderDate(orderDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

		Date deliveryDate = (Date) deliveryDateSpinner.getValue();
		order.setDeliveryDate(deliveryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

		// Обработка товаров
		if (isEditMode && order.getOrderItems() != null) {
			order.getOrderItems().clear();
		} else {
			order.setOrderItems(new ArrayList<>());
		}

		order.getOrderItems().addAll(itemManager.getOrderItems());
		for (OrderItem item : itemManager.getOrderItems()) {
			item.setOrder(order);
		}

		return order;
	}

	private void showSuccess(String message) {
		JOptionPane.showMessageDialog(this, message, "Успешно", JOptionPane.INFORMATION_MESSAGE);
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
	}

	// Рендереры
	private static class UserListCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof User user) {
				setText(user.getFullName() + " (" + user.getLogin() + ")");
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
