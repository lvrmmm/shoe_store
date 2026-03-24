package misis.ignatova_maria.shoe_store.gui.validators;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.*;

import misis.ignatova_maria.shoe_store.gui.contexts.ValidatorContext;

public class OrderFormValidator {

	private final Component parent;
	private final ValidatorContext context;

	public OrderFormValidator(Component parent, ValidatorContext context) {
		this.parent = parent;
		this.context = context;
	}

	public boolean validate() {
		List<String> errors = new ArrayList<>();

		validateUserCombo(errors);
		validateStatusCombo(errors);
		validatePickupPointCombo(errors);
		validateDateSpinners(errors);
		validateDeliveryDate(errors);

		if (!errors.isEmpty()) {
			showError(errors.get(0));
			return false;
		}

		return true;
	}

	private void validateUserCombo(List<String> errors) {
		JComboBox<?> userCombo = context.getUserCombo();
		if (userCombo == null) {
			errors.add("Ошибка инициализации формы: не найден список покупателей");
		} else if (userCombo.getSelectedItem() == null) {
			errors.add("Выберите покупателя");
		}
	}

	private void validateStatusCombo(List<String> errors) {
		JComboBox<?> statusCombo = context.getStatusCombo();
		if (statusCombo == null) {
			errors.add("Ошибка инициализации формы: не найден список статусов");
		} else if (statusCombo.getSelectedItem() == null) {
			errors.add("Выберите статус заказа");
		}
	}

	private void validatePickupPointCombo(List<String> errors) {
		JComboBox<?> pickupPointCombo = context.getPickupPointCombo();
		if (pickupPointCombo == null) {
			errors.add("Ошибка инициализации формы: не найден список пунктов выдачи");
		} else if (pickupPointCombo.getSelectedItem() == null) {
			errors.add("Выберите адрес пункта выдачи");
		}
	}

	private void validateDateSpinners(List<String> errors) {
		JSpinner orderDateSpinner = context.getOrderDateSpinner();
		JSpinner deliveryDateSpinner = context.getDeliveryDateSpinner();
		if (orderDateSpinner == null || deliveryDateSpinner == null) {
			errors.add("Ошибка инициализации формы: не найдены даты");
		}
	}

	private void validateDeliveryDate(List<String> errors) {
		JSpinner orderDateSpinner = context.getOrderDateSpinner();
		JSpinner deliveryDateSpinner = context.getDeliveryDateSpinner();

		if (orderDateSpinner != null && deliveryDateSpinner != null) {
			Date orderDate = (Date) orderDateSpinner.getValue();
			Date deliveryDate = (Date) deliveryDateSpinner.getValue();

			if (deliveryDate != null && orderDate != null && deliveryDate.before(orderDate)) {
				errors.add("Дата выдачи не может быть раньше даты заказа");
			}
		}
	}

	private void showError(String message) {
		Component windowParent = SwingUtilities.getWindowAncestor(parent);
		JOptionPane.showMessageDialog(windowParent != null ? windowParent : parent, message, "Ошибка валидации",
				JOptionPane.ERROR_MESSAGE);
	}
}
