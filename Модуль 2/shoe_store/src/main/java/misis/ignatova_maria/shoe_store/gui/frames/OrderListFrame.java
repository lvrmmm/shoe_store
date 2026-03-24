package misis.ignatova_maria.shoe_store.gui.frames;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.*;

import misis.ignatova_maria.shoe_store.entity.Order;
import misis.ignatova_maria.shoe_store.entity.User;
import misis.ignatova_maria.shoe_store.service.OrderService;
import misis.ignatova_maria.shoe_store.util.ImageLoader;
import misis.ignatova_maria.shoe_store.util.SpringContext;

public class OrderListFrame extends JFrame {

	private final OrderService orderService;
	private final User currentUser;
	private final ProductListFrame parentFrame;

	private JPanel cardsPanel;
	private JScrollPane scrollPane;
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	private JTextField searchField;
	private JComboBox<String> statusFilterCombo;

	private List<Order> allOrders;
	private List<Order> filteredOrders;

	private final AtomicBoolean isEditFormOpen = new AtomicBoolean(false);

	public OrderListFrame(ProductListFrame parent, User user) {
		this.orderService = SpringContext.getBean(OrderService.class);
		this.currentUser = user;
		this.parentFrame = parent;
		initUI();
		loadOrders();
	}

	private void initUI() {
		setTitle("Управление заказами");
		setSize(1100, 700);
		setMinimumSize(new Dimension(900, 600));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(ImageLoader.loadIcon().getImage());

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(Color.WHITE);
		add(mainPanel);

		JPanel topPanel = createTopPanel();
		JPanel controlPanel = createControlPanel();

		JPanel northContainer = new JPanel();
		northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
		northContainer.add(topPanel);
		northContainer.add(controlPanel);

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

		JButton backButton = new JButton("← Назад к товарам");
		backButton.setBackground(Color.decode("#00FA9A"));
		backButton.setFont(new Font("Times New Roman", Font.BOLD, 12));
		backButton.setFocusPainted(false);
		backButton.addActionListener(e -> {
			dispose();
			if (parentFrame != null) {
				parentFrame.setVisible(true);
			}
		});
		panel.add(backButton, BorderLayout.WEST);

		JLabel titleLabel = new JLabel("ЗАКАЗЫ");
		titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(titleLabel, BorderLayout.CENTER);

		return panel;
	}

	private JPanel createControlPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.decode("#7FFF00"));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		leftPanel.setBackground(Color.decode("#7FFF00"));

		leftPanel.add(new JLabel("🔍 Поиск:"));
		searchField = new JTextField(20);
		searchField.setToolTipText("Поиск по номеру заказа, клиенту, пункту выдачи");
		leftPanel.add(searchField);

		leftPanel.add(new JLabel("Статус:"));
		statusFilterCombo = new JComboBox<>();
		statusFilterCombo.addItem("Все статусы");
		leftPanel.add(statusFilterCombo);

		panel.add(leftPanel, BorderLayout.WEST);

		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		rightPanel.setBackground(Color.decode("#7FFF00"));

		if (currentUser.isAdmin()) {
			JButton addButton = new JButton("+ Добавить заказ");
			addButton.setBackground(Color.decode("#00FA9A"));
			addButton.setFont(new Font("Times New Roman", Font.BOLD, 12));
			addButton.setFocusPainted(false);
			addButton.addActionListener(e -> openAddOrderForm());
			rightPanel.add(addButton);
		}

		JButton refreshButton = new JButton("⟳ Обновить");
		refreshButton.setBackground(Color.decode("#FFD700"));
		refreshButton.setFont(new Font("Times New Roman", Font.BOLD, 12));
		refreshButton.setFocusPainted(false);
		refreshButton.addActionListener(e -> refreshOrders());
		rightPanel.add(refreshButton);

		panel.add(rightPanel, BorderLayout.EAST);

		addSearchAndFilterListeners();

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

		statusFilterCombo.addActionListener(e -> applyFiltersAndRefresh());
	}

	private void loadStatusesForFilter() {
		statusFilterCombo.removeAllItems();
		statusFilterCombo.addItem("Все статусы");

		if (allOrders != null) {
			allOrders.stream().map(o -> o.getStatus().getName()).distinct().sorted()
					.forEach(statusFilterCombo::addItem);
		}
	}

	private void loadOrders() {
		try {
			allOrders = orderService.getAllOrders();
			loadStatusesForFilter();
			filteredOrders = allOrders;
			refreshCardsDisplay();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Ошибка загрузки заказов: " + e.getMessage(), "Ошибка",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void applyFiltersAndRefresh() {
		if (allOrders == null)
			return;

		String searchText = searchField.getText().trim().toLowerCase();
		String selectedStatus = (String) statusFilterCombo.getSelectedItem();

		List<Order> filtered = allOrders;

		if (!searchText.isEmpty()) {
			filtered = filtered.stream()
					.filter(o -> String.valueOf(o.getId()).contains(searchText)
							|| (o.getUser() != null && o.getUser().getFullName().toLowerCase().contains(searchText))
							|| (o.getPickupPoint() != null
									&& o.getPickupPoint().getFullAddress().toLowerCase().contains(searchText)))
					.collect(Collectors.toList());
		}

		if (selectedStatus != null && !selectedStatus.equals("Все статусы")) {
			filtered = filtered.stream().filter(o -> o.getStatus().getName().equals(selectedStatus))
					.collect(Collectors.toList());
		}

		filteredOrders = filtered;
		refreshCardsDisplay();
	}

	private void refreshCardsDisplay() {
		cardsPanel.removeAll();

		if (filteredOrders == null || filteredOrders.isEmpty()) {
			JLabel emptyLabel = new JLabel("Заказы не найдены");
			emptyLabel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
			emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			cardsPanel.add(emptyLabel);
		} else {
			for (Order order : filteredOrders) {
				JPanel card = createOrderCard(order);
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

	private JPanel createOrderCard(Order order) {
		String status = order.getStatus() != null ? order.getStatus().getName() : "";
		boolean isCompleted = "Завершен".equals(status);
		boolean isNew = "Новый".equals(status);

		Color bgColor;
		if (isCompleted) {
			bgColor = new Color(200, 230, 200);
		} else if (isNew) {
			bgColor = new Color(255, 245, 200);
		} else {
			bgColor = Color.WHITE;
		}

		Color borderColor = Color.decode("#7FFF00");
		Color textColor = Color.BLACK;

		JPanel card = new JPanel(new BorderLayout(15, 10));
		card.setBackground(bgColor);
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(borderColor, 2),
				BorderFactory.createEmptyBorder(15, 20, 15, 20)));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

		if (currentUser.isAdmin()) {
			card.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						openEditOrderForm(order);
					}
				}
			});
			card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		// Левая часть
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBackground(bgColor);
		leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

		JLabel orderIdLabel = new JLabel("Заказ №" + order.getId());
		orderIdLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
		orderIdLabel.setForeground(textColor);
		orderIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		leftPanel.add(orderIdLabel);
		leftPanel.add(Box.createVerticalStrut(6));

		JLabel statusLabel = new JLabel(status);
		statusLabel.setFont(new Font("Times New Roman", Font.BOLD, 14));
		statusLabel.setForeground(isCompleted ? new Color(34, 139, 34) : (isNew ? new Color(255, 140, 0) : textColor));
		statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		leftPanel.add(statusLabel);
		leftPanel.add(Box.createVerticalStrut(12));

		JLabel addressLabel = new JLabel("Адрес выдачи:");
		addressLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
		addressLabel.setForeground(textColor);
		addressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		leftPanel.add(addressLabel);
		leftPanel.add(Box.createVerticalStrut(3));

		String address = order.getPickupPoint() != null ? order.getPickupPoint().getFullAddress() : "Не указан";
		JLabel addressValueLabel = new JLabel(address);
		addressValueLabel.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		addressValueLabel.setForeground(textColor);
		addressValueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		leftPanel.add(addressValueLabel);
		leftPanel.add(Box.createVerticalStrut(10));

		JLabel orderDateLabel = new JLabel("Дата заказа:");
		orderDateLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
		orderDateLabel.setForeground(textColor);
		orderDateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		leftPanel.add(orderDateLabel);
		leftPanel.add(Box.createVerticalStrut(3));

		String orderDate = order.getOrderDate() != null ? order.getOrderDate().format(dateFormatter) : "—";
		JLabel orderDateValueLabel = new JLabel(orderDate);
		orderDateValueLabel.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		orderDateValueLabel.setForeground(textColor);
		orderDateValueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		leftPanel.add(orderDateValueLabel);

		// Правая часть
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setBackground(bgColor);
		rightPanel.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(100, 150, 100), 2),
						BorderFactory.createEmptyBorder(12, 15, 12, 15)));
		rightPanel.setPreferredSize(new Dimension(160, 110));
		rightPanel.setMaximumSize(new Dimension(160, 110));

		JLabel deliveryTitle = new JLabel("ДАТА ДОСТАВКИ");
		deliveryTitle.setFont(new Font("Times New Roman", Font.BOLD, 12));
		deliveryTitle.setForeground(new Color(34, 139, 34));
		deliveryTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		rightPanel.add(deliveryTitle);
		rightPanel.add(Box.createVerticalStrut(8));

		String deliveryDate = order.getDeliveryDate() != null ? order.getDeliveryDate().format(dateFormatter) : "—";
		JLabel deliveryDateLabel = new JLabel(deliveryDate);
		deliveryDateLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
		deliveryDateLabel.setForeground(new Color(34, 139, 34));
		deliveryDateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		rightPanel.add(deliveryDateLabel);

		card.add(leftPanel, BorderLayout.CENTER);
		card.add(rightPanel, BorderLayout.EAST);

		if (currentUser.isAdmin() && isNew) {
			JButton deleteButton = new JButton("Удалить заказ");
			deleteButton.setBackground(new Color(255, 100, 100));
			deleteButton.setForeground(Color.BLACK);
			deleteButton.setFont(new Font("Times New Roman", Font.BOLD, 11));
			deleteButton.setFocusPainted(false);
			deleteButton.setPreferredSize(new Dimension(110, 28));
			deleteButton.addActionListener(e -> deleteOrder(order));

			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			buttonPanel.setBackground(bgColor);
			buttonPanel.add(deleteButton);
			card.add(buttonPanel, BorderLayout.SOUTH);
		}

		return card;
	}

	private void openAddOrderForm() {
		if (isEditFormOpen.get()) {
			JOptionPane.showMessageDialog(this,
					"Окно редактирования уже открыто. Закройте его перед созданием нового заказа.", "Внимание",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		OrderFormFrame form = new OrderFormFrame(this, null, currentUser);
		form.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent e) {
				isEditFormOpen.set(false);
				refreshOrders(); // Обновляем список после закрытия формы
			}
		});
		isEditFormOpen.set(true);
		form.setVisible(true);
	}

	private void openEditOrderForm(Order order) {
		if (isEditFormOpen.get()) {
			JOptionPane.showMessageDialog(this,
					"Окно редактирования уже открыто. Закройте его перед редактированием другого заказа.", "Внимание",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		OrderFormFrame form = new OrderFormFrame(this, order, currentUser);
		form.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent e) {
				isEditFormOpen.set(false);
				refreshOrders(); // Обновляем список после закрытия формы
			}
		});
		isEditFormOpen.set(true);
		form.setVisible(true);
	}

	private void deleteOrder(Order order) {
		if (!"Новый".equals(order.getStatus().getName())) {
			JOptionPane.showMessageDialog(this,
					"Невозможно удалить заказ со статусом \"" + order.getStatus().getName() + "\".\n"
							+ "Удалять можно только заказы со статусом \"Новый\".",
					"Удаление невозможно", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String message = String.format("""
				Вы действительно хотите удалить заказ №%d?

				Клиент: %s
				Дата заказа: %s

				Это действие нельзя отменить.""", order.getId(),
				order.getUser() != null ? order.getUser().getFullName() : "",
				order.getOrderDate() != null ? order.getOrderDate().format(dateFormatter) : "");

		int result = JOptionPane.showConfirmDialog(this, message, "Подтверждение удаления", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);

		if (result == JOptionPane.YES_OPTION) {
			try {
				orderService.deleteOrder(order.getId());
				refreshOrders(); // Обновляем список после удаления
				JOptionPane.showMessageDialog(this, "Заказ успешно удалён", "Успешно", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Ошибка при удалении заказа: " + e.getMessage(), "Ошибка",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Обновляет список заказов, вызывается после добавления, редактирования или
	 * удаления
	 */
	public void refreshOrders() {
		// Сохраняем текущие настройки поиска и фильтра
		String currentSearch = searchField.getText();
		String currentStatus = (String) statusFilterCombo.getSelectedItem();

		// Загружаем свежие данные из БД
		loadOrders();

		// Восстанавливаем настройки поиска и фильтра
		if (currentSearch != null && !currentSearch.isEmpty()) {
			searchField.setText(currentSearch);
		}
		if (currentStatus != null && !currentStatus.equals("Все статусы")) {
			statusFilterCombo.setSelectedItem(currentStatus);
		}

		// Применяем фильтры
		applyFiltersAndRefresh();
	}
}
