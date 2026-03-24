package misis.ignatova_maria.shoe_store.gui.validators;

import java.awt.Component;
import java.util.Date;

import javax.swing.*;

public class OrderFormValidator {

	private final Component parent;
	private final JComboBox<?> userCombo;
	private final JComboBox<?> statusCombo;
	private final JComboBox<?> pickupPointCombo;
	private final JSpinner orderDateSpinner;
	private final JSpinner deliveryDateSpinner;

	public OrderFormValidator(Component parent, JComboBox<?> userCombo, JComboBox<?> statusCombo,
			JComboBox<?> pickupPointCombo, JSpinner orderDateSpinner, JSpinner deliveryDateSpinner) {
		this.parent = parent;
		this.userCombo = userCombo;
		this.statusCombo = statusCombo;
		this.pickupPointCombo = pickupPointCombo;
		this.orderDateSpinner = orderDateSpinner;
		this.deliveryDateSpinner = deliveryDateSpinner;
	}

	public boolean validate() {
		if (userCombo == null) {
			showError("Ошибка инициализации формы: не найден список покупателей");
			return false;
		}
		if (userCombo.getSelectedItem() == null) {
			showError("Выберите покупателя");
			return false;
		}

		if (statusCombo == null) {
			showError("Ошибка инициализации формы: не найден список статусов");
			return false;
		}
		if (statusCombo.getSelectedItem() == null) {
			showError("Выберите статус заказа");
			return false;
		}

		if (pickupPointCombo == null) {
			showError("Ошибка инициализации формы: не найден список пунктов выдачи");
			return false;
		}
		if (pickupPointCombo.getSelectedItem() == null) {
			showError("Выберите адрес пункта выдачи");
			return false;
		}

		if (orderDateSpinner == null || deliveryDateSpinner == null) {
			showError("Ошибка инициализации формы: не найдены даты");
			return false;
		}

		Date orderDate = (Date) orderDateSpinner.getValue();
		Date deliveryDate = (Date) deliveryDateSpinner.getValue();

		if (deliveryDate.before(orderDate)) {
			showError("Дата выдачи не может быть раньше даты заказа");
			return false;
		}

		return true;
	}

	private void showError(String message) {
		Component windowParent = SwingUtilities.getWindowAncestor(parent);
		JOptionPane.showMessageDialog(windowParent != null ? windowParent : parent, message, "Ошибка валидации",
				JOptionPane.ERROR_MESSAGE);
	}
}
