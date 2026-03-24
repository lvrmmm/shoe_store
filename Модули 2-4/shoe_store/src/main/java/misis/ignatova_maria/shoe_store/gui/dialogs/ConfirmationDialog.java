package misis.ignatova_maria.shoe_store.gui.dialogs;

import java.awt.*;

import javax.swing.*;

public class ConfirmationDialog {

	public static boolean confirmDelete(Component parent, String productName) {
		String message = String.format("""
				Вы действительно хотите удалить товар "%s"?

				Это действие нельзя отменить. Если товар присутствует в заказах,
				удаление будет невозможно.""", productName);

		int result = JOptionPane.showConfirmDialog(parent, message, "Подтверждение удаления", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE, null);
		return result == JOptionPane.YES_OPTION;
	}

	public static void showDeleteError(Component parent, String productName) {
		JOptionPane.showMessageDialog(parent,
				"Невозможно удалить товар \"" + productName + "\",\n" + "так как он присутствует в заказах.\n\n"
						+ "Сначала удалите или измените заказы с этим товаром.",
				"Удаление невозможно", JOptionPane.ERROR_MESSAGE, null);
	}

}
