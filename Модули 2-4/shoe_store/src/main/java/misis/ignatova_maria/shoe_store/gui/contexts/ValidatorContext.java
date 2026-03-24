package misis.ignatova_maria.shoe_store.gui.contexts;

import javax.swing.*;

import lombok.Getter;

@Getter
public class ValidatorContext {
	private final JComboBox<?> userCombo;
	private final JComboBox<?> statusCombo;
	private final JComboBox<?> pickupPointCombo;
	private final JSpinner orderDateSpinner;
	private final JSpinner deliveryDateSpinner;

	public ValidatorContext(JComboBox<?> userCombo, JComboBox<?> statusCombo, JComboBox<?> pickupPointCombo,
			JSpinner orderDateSpinner, JSpinner deliveryDateSpinner) {
		this.userCombo = userCombo;
		this.statusCombo = statusCombo;
		this.pickupPointCombo = pickupPointCombo;
		this.orderDateSpinner = orderDateSpinner;
		this.deliveryDateSpinner = deliveryDateSpinner;
	}

}
