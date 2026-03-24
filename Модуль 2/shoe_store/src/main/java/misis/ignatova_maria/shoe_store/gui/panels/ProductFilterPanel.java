package misis.ignatova_maria.shoe_store.gui.panels;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.*;

import lombok.Setter;
import misis.ignatova_maria.shoe_store.entity.Product;

public class ProductFilterPanel extends JPanel {

	private JTextField searchField;
	private JComboBox<String> supplierFilterCombo;
	private JComboBox<String> sortCombo;
	@Setter
    private Consumer<Void> onFilterChanged;

	public ProductFilterPanel() {
		initUI();
	}

	private void initUI() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));
		setBackground(Color.decode("#7FFF00"));

		add(new JLabel("🔍 Поиск:"));
		searchField = new JTextField(20);
		searchField.setToolTipText("Поиск по артикулу, названию, описанию, категории, производителю, поставщику");
		add(searchField);

		add(new JLabel("Поставщик:"));
		supplierFilterCombo = new JComboBox<>();
		supplierFilterCombo.addItem("Все поставщики");
		add(supplierFilterCombo);

		add(new JLabel("Сортировка:"));
		sortCombo = new JComboBox<>(
				new String[]{"Без сортировки", "Количество (возрастание)", "Количество (убывание)"});
		add(sortCombo);

		addListeners();
	}

	private void addListeners() {
		DocumentListener listener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				notifyFilterChanged();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				notifyFilterChanged();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				notifyFilterChanged();
			}
		};
		searchField.getDocument().addDocumentListener(listener);

		supplierFilterCombo.addActionListener(e -> notifyFilterChanged());
		sortCombo.addActionListener(e -> notifyFilterChanged());
	}

    private void notifyFilterChanged() {
		if (onFilterChanged != null) {
			onFilterChanged.accept(null);
		}
	}

	public String getSearchText() {
		return searchField.getText();
	}

	public String getSelectedSupplier() {
		return (String) supplierFilterCombo.getSelectedItem();
	}

	public String getSortOption() {
		return (String) sortCombo.getSelectedItem();
	}

	public void updateSuppliers(List<Product> allProducts) {
		supplierFilterCombo.removeAllItems();
		supplierFilterCombo.addItem("Все поставщики");

		if (allProducts != null) {
			allProducts.stream().map(p -> p.getSupplier().getName()).distinct().sorted()
					.forEach(supplierFilterCombo::addItem);
		}
	}
}
