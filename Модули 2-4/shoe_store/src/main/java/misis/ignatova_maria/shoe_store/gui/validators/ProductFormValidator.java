package misis.ignatova_maria.shoe_store.gui.validators;

import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

/**
 * Валидатор формы товара
 */
public class ProductFormValidator {

	private final JTextField nameField;
	private final JComboBox<?> categoryCombo;
	private final JComboBox<?> manufacturerCombo;
	private final JComboBox<?> supplierCombo;
	private final JTextField priceField;
	private final JComboBox<?> unitCombo;
	private final Component parent;

	public ProductFormValidator(Component parent, JTextField nameField, JComboBox<?> categoryCombo,
			JComboBox<?> manufacturerCombo, JComboBox<?> supplierCombo, JTextField priceField, JComboBox<?> unitCombo) {
		this.parent = parent;
		this.nameField = nameField;
		this.categoryCombo = categoryCombo;
		this.manufacturerCombo = manufacturerCombo;
		this.supplierCombo = supplierCombo;
		this.priceField = priceField;
		this.unitCombo = unitCombo;
	}

	public boolean validate() {
		List<String> errors = new ArrayList<>();

		validateName(errors);
		validateCategory(errors);
		validateManufacturer(errors);
		validateSupplier(errors);
		validatePrice(errors);
		validateUnit(errors);

		if (!errors.isEmpty()) {
			JOptionPane.showMessageDialog(parent, errors.get(0), "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	private void validateName(List<String> errors) {
		if (nameField.getText().trim().isEmpty()) {
			errors.add("Наименование товара обязательно для заполнения");
			nameField.requestFocus();
		}
	}

	private void validateCategory(List<String> errors) {
		if (categoryCombo.getSelectedItem() == null) {
			errors.add("Выберите категорию товара");
		}
	}

	private void validateManufacturer(List<String> errors) {
		if (manufacturerCombo.getSelectedItem() == null) {
			errors.add("Выберите производителя");
		}
	}

	private void validateSupplier(List<String> errors) {
		if (supplierCombo.getSelectedItem() == null) {
			errors.add("Выберите поставщика");
		}
	}

	private void validatePrice(List<String> errors) {
		try {
			BigDecimal price = new BigDecimal(priceField.getText().trim());
			if (price.compareTo(BigDecimal.ZERO) <= 0) {
				errors.add("Цена должна быть положительным числом");
				priceField.requestFocus();
			}
		} catch (NumberFormatException e) {
			errors.add("Введите корректную цену (например: 4990.00)");
			priceField.requestFocus();
		}
	}

	private void validateUnit(List<String> errors) {
		if (unitCombo.getSelectedItem() == null) {
			errors.add("Выберите единицу измерения");
		}
	}
}
