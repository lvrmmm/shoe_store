package misis.ignatova_maria.shoe_store.gui.model;


import misis.ignatova_maria.shoe_store.entity.Product;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ProductTableModel extends AbstractTableModel {

    private List<Product> products;
    private final String[] columns = {
            "Фото", "Наименование", "Категория", "Описание", "Производитель",
            "Поставщик", "Цена", "Ед. изм.", "Кол-во на складе", "Скидка"
    };

    public void setProducts(List<Product> products) {
        this.products = products;
        fireTableDataChanged();
    }

    public Product getProductAt(int row) {
        return products.get(row);
    }

    @Override
    public int getRowCount() {
        return products == null ? 0 : products.size();
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
        Product p = products.get(row);
        switch (column) {
            case 0: return p.getPhoto();
            case 1: return p.getName();
            case 2: return p.getCategory().getName();
            case 3: return p.getDescription();
            case 4: return p.getManufacturer().getName();
            case 5: return p.getSupplier().getName();
            case 6: return p;
            case 7: return p.getUnit().getName();
            case 8: return p.getQuantity();
            case 9: return p.getDiscount() != null ? p.getDiscount().doubleValue() + "%" : "0%";
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int column) {
        if (column == 0) return String.class;
        if (column == 6) return Product.class;
        return String.class;
    }
}