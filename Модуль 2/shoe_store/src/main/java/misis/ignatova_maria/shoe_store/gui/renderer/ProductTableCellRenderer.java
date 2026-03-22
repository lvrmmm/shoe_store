package misis.ignatova_maria.shoe_store.gui.renderer;

import misis.ignatova_maria.shoe_store.entity.Product;
import misis.ignatova_maria.shoe_store.gui.model.ProductTableModel;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class ProductTableCellRenderer extends DefaultTableCellRenderer {

    private final ProductTableModel model;
    private final DecimalFormat priceFormat;

    public ProductTableCellRenderer(ProductTableModel model) {
        this.model = model;
        this.priceFormat = new DecimalFormat("#,##0.00");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (!isSelected) {
            Product product = model.getProductAt(row);

            // Определяем наличие на складе
            boolean inStock = product.getQuantity() != null && product.getQuantity() > 0;

            // Получаем скидку как double
            double discountValue = product.getDiscount() != null ? product.getDiscount().doubleValue() : 0;
            boolean hasDiscount = discountValue > 0;
            boolean highDiscount = discountValue > 15;

            // Подсветка строки
            if (!inStock) {
                c.setBackground(Color.CYAN);
            } else if (highDiscount) {
                c.setBackground(Color.decode("#2E8B57"));
                c.setForeground(Color.WHITE);
            } else {
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
            }

            // Обработка колонки цены (column == 6)
            if (column == 6 && value instanceof Product p) {
                setHorizontalAlignment(RIGHT);
                BigDecimal price = p.getPrice();
                if (hasDiscount) {
                    BigDecimal finalPrice = price.multiply(BigDecimal.valueOf(1 - discountValue / 100));
                    setText("<html><strike>" + priceFormat.format(price) +
                            "</strike> <b>" + priceFormat.format(finalPrice) + "</b></html>");
                } else {
                    setText(priceFormat.format(price));
                }
            } else {
                setHorizontalAlignment(LEFT);
            }
        } else {
            c.setBackground(table.getSelectionBackground());
        }

        return c;
    }
}