package misis.ignatova_maria.shoe_store.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.*;

import lombok.Getter;
import misis.ignatova_maria.shoe_store.entity.OrderItem;
import misis.ignatova_maria.shoe_store.entity.Product;
import misis.ignatova_maria.shoe_store.gui.model.OrderItemsTableModel;

public class OrderItemManager {

	@Getter
	private final List<OrderItem> orderItems;
	private final JTable itemsTable;
	private final OrderItemsTableModel tableModel;
	private final Component parent;

	public OrderItemManager(Component parent, JTable itemsTable, OrderItemsTableModel tableModel) {
		this.parent = parent;
		this.itemsTable = itemsTable;
		this.tableModel = tableModel;
		this.orderItems = new ArrayList<>();
	}

	public void setOrderItems(List<OrderItem> items) {
		orderItems.clear();
		if (items != null) {
			orderItems.addAll(items);
		}
		tableModel.setOrderItems(orderItems);
	}

	public boolean canAddQuantity(Product product, int additionalQuantity) {
		int currentInOrder = orderItems.stream()
				.filter(item -> item.getProduct().getArticle().equals(product.getArticle()))
				.mapToInt(OrderItem::getQuantity).sum();

		int totalAfterAdd = currentInOrder + additionalQuantity;
		int availableInStock = product.getQuantity() != null ? product.getQuantity() : 0;

		if (totalAfterAdd > availableInStock) {
			Component windowParent = SwingUtilities.getWindowAncestor(parent);
			JOptionPane.showMessageDialog(windowParent != null ? windowParent : parent,
					String.format("""
							Недостаточно товара на складе!

							Товар: %s
							Артикул: %s
							Уже в заказе: %d шт.
							Пытаетесь добавить: %d шт.
							Всего будет: %d шт.
							Доступно на складе: %d шт.""", product.getName(), product.getArticle(), currentInOrder,
							additionalQuantity, totalAfterAdd, availableInStock),
					"Ошибка добавления", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	public boolean addItem(Product product, int quantity) {
		if (product == null) {
			Component windowParent = SwingUtilities.getWindowAncestor(parent);
			JOptionPane.showMessageDialog(windowParent != null ? windowParent : parent, "Выберите товар", "Ошибка",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (quantity <= 0) {
			Component windowParent = SwingUtilities.getWindowAncestor(parent);
			JOptionPane.showMessageDialog(windowParent != null ? windowParent : parent,
					"Количество должно быть больше 0", "Ошибка", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (!canAddQuantity(product, quantity)) {
			return false;
		}

		Optional<OrderItem> existing = orderItems.stream()
				.filter(item -> item.getProduct().getArticle().equals(product.getArticle())).findFirst();

		if (existing.isPresent()) {
			existing.get().setQuantity(existing.get().getQuantity() + quantity);
		} else {
			OrderItem newItem = new OrderItem();
			newItem.setProduct(product);
			newItem.setQuantity(quantity);
			orderItems.add(newItem);
		}

		tableModel.setOrderItems(orderItems);

		Component windowParent = SwingUtilities.getWindowAncestor(parent);
		JOptionPane.showMessageDialog(windowParent != null ? windowParent : parent, "Товар добавлен в заказ", "Успешно",
				JOptionPane.INFORMATION_MESSAGE);
		return true;
	}

	public void removeSelectedItem() {
		int selectedRow = itemsTable.getSelectedRow();
		if (selectedRow >= 0 && selectedRow < orderItems.size()) {
			orderItems.remove(selectedRow);
			tableModel.setOrderItems(orderItems);
		} else {
			Component windowParent = SwingUtilities.getWindowAncestor(parent);
			JOptionPane.showMessageDialog(windowParent != null ? windowParent : parent, "Выберите товар для удаления",
					"Внимание", JOptionPane.WARNING_MESSAGE);
		}
	}

	public boolean isEmpty() {
		return orderItems.isEmpty();
	}
}
