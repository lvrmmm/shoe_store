package misis.ignatova_maria.shoe_store.gui.loaders;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.swing.*;

import misis.ignatova_maria.shoe_store.entity.*;
import misis.ignatova_maria.shoe_store.gui.contexts.OrderFormContext;
import misis.ignatova_maria.shoe_store.service.OrderService;
import misis.ignatova_maria.shoe_store.service.ProductService;
import misis.ignatova_maria.shoe_store.service.UserService;

public class OrderFormDataLoader {

	private final OrderService orderService;
	private final ProductService productService;
	private final UserService userService;

	public OrderFormDataLoader(OrderService orderService, ProductService productService, UserService userService) {
		this.orderService = orderService;
		this.productService = productService;
		this.userService = userService;
	}

	public void loadUsers(JComboBox<User> comboBox) {
		List<User> allUsers = userService.getAllUsers();
		for (User user : allUsers) {
			comboBox.addItem(user);
		}
	}

	public void loadStatuses(JComboBox<OrderStatus> comboBox) {
		List<OrderStatus> statuses = orderService.getAllStatuses();
		for (OrderStatus status : statuses) {
			comboBox.addItem(status);
		}
	}

	public void loadPickupPoints(JComboBox<PickupPoint> comboBox) {
		List<PickupPoint> points = orderService.getAllPickupPoints();
		for (PickupPoint point : points) {
			comboBox.addItem(point);
		}
	}

	public void loadProducts(JComboBox<Product> comboBox) {
		List<Product> products = productService.getAllProducts();
		for (Product product : products) {
			comboBox.addItem(product);
		}
	}

	public void loadOrderData(Order order, OrderFormContext context) {
		if (order == null) {
			context.getOrderDateSpinner().setValue(new Date());
			context.getDeliveryDateSpinner()
					.setValue(Date.from(LocalDate.now().plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant()));
			context.getPickupCodeField().setText("(будет сгенерирован автоматически)");
			context.getItemManager().setOrderItems(new ArrayList<>());
			return;
		}

		selectComboItem(context.getUserCombo(), order.getUser(), User::getId);
		selectComboItem(context.getStatusCombo(), order.getStatus(), OrderStatus::getId);
		selectComboItem(context.getPickupPointCombo(), order.getPickupPoint(), PickupPoint::getId);

		if (order.getOrderDate() != null) {
			context.getOrderDateSpinner()
					.setValue(Date.from(order.getOrderDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}
		if (order.getDeliveryDate() != null) {
			context.getDeliveryDateSpinner()
					.setValue(Date.from(order.getDeliveryDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}

		if (order.getPickupCode() != null) {
			context.getPickupCodeField().setText(order.getPickupCode());
		}

		if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
			context.getItemManager().setOrderItems(order.getOrderItems());
		} else {
			context.getItemManager().setOrderItems(new ArrayList<>());
		}
	}

	private <T> void selectComboItem(JComboBox<T> comboBox, T item, Function<T, Integer> idExtractor) {
		if (item == null)
			return;

		for (int i = 0; i < comboBox.getItemCount(); i++) {
			T current = comboBox.getItemAt(i);
			if (current != null && idExtractor.apply(current).equals(idExtractor.apply(item))) {
				comboBox.setSelectedIndex(i);
				break;
			}
		}
	}
}
