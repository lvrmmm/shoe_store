package misis.ignatova_maria.shoe_store.gui.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import lombok.Setter;
import misis.ignatova_maria.shoe_store.entity.OrderItem;

public class OrderItemsTableModel extends AbstractTableModel {

	private final String[] columns = {"Артикул", "Наименование товара", "Количество"};
	private List<OrderItem> orderItems;

	public interface DataChangeListener {
		void onDataChanged();
	}

	@Setter
	private DataChangeListener dataChangeListener;

	public OrderItemsTableModel(List<OrderItem> orderItems) {
		this.orderItems = orderItems != null ? orderItems : new ArrayList<>();
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems != null ? orderItems : new ArrayList<>();

		SwingUtilities.invokeLater(() -> {
			fireTableDataChanged();
			if (dataChangeListener != null) {
				dataChangeListener.onDataChanged();
			}
		});
	}

	@Override
	public int getRowCount() {
		return orderItems != null ? orderItems.size() : 0;
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public String getColumnName(int column) {
		return columns[column];
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (orderItems == null || row >= orderItems.size()) {
			return null;
		}

		OrderItem item = orderItems.get(row);

		return switch (column) {
			case 0 -> item.getProduct().getArticle();
			case 1 -> item.getProduct().getName();
			case 2 -> item.getQuantity();
			default -> null;
		};
	}
}
