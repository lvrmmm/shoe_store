package misis.ignatova_maria.shoe_store.gui.builders;

import java.awt.*;

import javax.swing.*;

/**
 * Вспомогательный класс для создания полей формы с единым стилем
 */
public class FormFieldBuilder {

	private final JPanel formPanel;
	private final GridBagConstraints gbc;
	private int row = 0;

	public FormFieldBuilder(JPanel formPanel) {
		this.formPanel = formPanel;
		this.gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 8, 8, 8);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
	}

	public FormFieldBuilder addLabel(String text) {
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0;
		formPanel.add(new JLabel(text), gbc);
		return this;
	}

	public FormFieldBuilder addComponent(Component component) {
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		formPanel.add(component, gbc);
		row++;
		return this;
	}

	public FormFieldBuilder addFullWidthComponent(Component component) {
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		formPanel.add(component, gbc);
		row++;
		gbc.gridwidth = 1;
		return this;
	}
}
