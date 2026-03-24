package misis.ignatova_maria.shoe_store.gui;

import javax.swing.*;

import misis.ignatova_maria.shoe_store.entity.*;

/**
 * Загрузчик данных для выпадающих списков
 */
public class EntityLoader {

	public static void loadCategories(JComboBox<Category> comboBox) {
		Category women = new Category();
		women.setId(1);
		women.setName("Женская обувь");
		comboBox.addItem(women);

		Category men = new Category();
		men.setId(2);
		men.setName("Мужская обувь");
		comboBox.addItem(men);
	}

	public static void loadManufacturers(JComboBox<Manufacturer> comboBox) {
		String[] manufacturers = {"Kari", "Marco Tozzi", "Рос", "Rieker", "Alessio Nesca", "CROSBY"};
		for (int i = 0; i < manufacturers.length; i++) {
			Manufacturer m = new Manufacturer();
			m.setId(i + 1);
			m.setName(manufacturers[i]);
			comboBox.addItem(m);
		}
	}

	public static void loadSuppliers(JComboBox<Supplier> comboBox) {
		String[] suppliers = {"Kari", "Обувь для вас"};
		for (int i = 0; i < suppliers.length; i++) {
			Supplier s = new Supplier();
			s.setId(i + 1);
			s.setName(suppliers[i]);
			comboBox.addItem(s);
		}
	}

	public static void loadUnits(JComboBox<Unit> comboBox) {
		Unit unit = new Unit();
		unit.setId(1);
		unit.setName("шт.");
		comboBox.addItem(unit);
	}

	public static <T> void selectComboItem(JComboBox<T> combo, T item) {
		for (int i = 0; i < combo.getItemCount(); i++) {
			if (item != null && item.equals(combo.getItemAt(i))) {
				combo.setSelectedIndex(i);
				break;
			}
		}
	}
}
