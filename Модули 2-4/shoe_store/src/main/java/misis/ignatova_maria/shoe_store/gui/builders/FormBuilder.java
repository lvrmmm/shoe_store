package misis.ignatova_maria.shoe_store.gui.builders;

import java.awt.*;

import javax.swing.*;

/**
 * Утилитарный класс для создания стандартных панелей формы
 */
public class FormBuilder {

	public static JPanel createFormPanel(String title) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createTitledBorder(title));
		return panel;
	}

	public static void addField(JPanel panel, GridBagConstraints gbc, String label, Component component, int row) {
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		panel.add(new JLabel(label), gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		panel.add(component, gbc);
	}

	public static JPanel createButtonPanel(JButton saveButton, JButton cancelButton) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
		panel.setBackground(Color.WHITE);
		panel.add(saveButton);
		panel.add(cancelButton);
		return panel;
	}

	public static JSpinner createDateSpinner() {
		SpinnerDateModel model = new SpinnerDateModel();
		JSpinner spinner = new JSpinner(model);
		JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd.MM.yyyy");
		spinner.setEditor(editor);
		return spinner;
	}
}
