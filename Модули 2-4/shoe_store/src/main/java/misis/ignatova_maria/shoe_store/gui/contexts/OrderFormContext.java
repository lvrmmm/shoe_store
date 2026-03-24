package misis.ignatova_maria.shoe_store.gui.contexts;

import javax.swing.*;

import lombok.Getter;
import misis.ignatova_maria.shoe_store.entity.*;
import misis.ignatova_maria.shoe_store.gui.managers.OrderItemManager;

@Getter
public class OrderFormContext {

	private final JComboBox<User> userCombo;
	private final JComboBox<OrderStatus> statusCombo;
	private final JComboBox<PickupPoint> pickupPointCombo;
	private final JSpinner orderDateSpinner;
	private final JSpinner deliveryDateSpinner;
	private final JTextField pickupCodeField;
	private final OrderItemManager itemManager;

	public OrderFormContext(JComboBox<User> userCombo, JComboBox<OrderStatus> statusCombo,
			JComboBox<PickupPoint> pickupPointCombo, JSpinner orderDateSpinner, JSpinner deliveryDateSpinner,
			JTextField pickupCodeField, OrderItemManager itemManager) {
		this.userCombo = userCombo;
		this.statusCombo = statusCombo;
		this.pickupPointCombo = pickupPointCombo;
		this.orderDateSpinner = orderDateSpinner;
		this.deliveryDateSpinner = deliveryDateSpinner;
		this.pickupCodeField = pickupCodeField;
		this.itemManager = itemManager;
	}

}
