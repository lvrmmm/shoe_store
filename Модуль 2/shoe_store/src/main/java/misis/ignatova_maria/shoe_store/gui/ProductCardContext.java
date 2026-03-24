package misis.ignatova_maria.shoe_store.gui;

import java.awt.Color;

import misis.ignatova_maria.shoe_store.entity.Product;

/**
 * Контекст для создания карточки товара Группирует все параметры для уменьшения
 * количества параметров в методах
 */
public class ProductCardContext {
	private final Product product;
	private final boolean inStock;
	private final double discount;
	private final Color bg;
	private final Color textColor;
	private final Color borderColor;

	public ProductCardContext(Product product) {
		this.product = product;
		this.inStock = product.getQuantity() != null && product.getQuantity() > 0;
		this.discount = product.getDiscount() != null ? product.getDiscount().doubleValue() : 0;
		this.bg = discount > 15 ? Color.decode("#2E8B57") : Color.WHITE;
		this.textColor = (discount > 15 && inStock) ? Color.WHITE : Color.BLACK;
		this.borderColor = Color.decode("#00FA9A");
	}

	public Product getProduct() {
		return product;
	}
	public boolean isInStock() {
		return inStock;
	}
	public double getDiscount() {
		return discount;
	}
	public Color getBg() {
		return bg;
	}
	public Color getTextColor() {
		return textColor;
	}
	public Color getBorderColor() {
		return borderColor;
	}
}
