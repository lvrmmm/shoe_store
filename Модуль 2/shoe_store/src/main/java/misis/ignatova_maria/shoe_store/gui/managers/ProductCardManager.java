package misis.ignatova_maria.shoe_store.gui.managers;

import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.swing.*;

import lombok.Setter;
import misis.ignatova_maria.shoe_store.entity.Product;
import misis.ignatova_maria.shoe_store.entity.User;
import misis.ignatova_maria.shoe_store.gui.contexts.ProductCardContext;
import misis.ignatova_maria.shoe_store.gui.dialogs.ConfirmationDialog;
import misis.ignatova_maria.shoe_store.gui.frames.ProductFormFrame;
import misis.ignatova_maria.shoe_store.gui.frames.ProductListFrame;
import misis.ignatova_maria.shoe_store.service.ProductService;
import misis.ignatova_maria.shoe_store.util.ImageLoader;

public class ProductCardManager {

	private final ProductService productService;
	private final User currentUser;
	private final ProductListFrame parentFrame; // <-- ИЗМЕНИТЕ ТИП С JFrame НА ProductListFrame
	private final DecimalFormat priceFormat;
	@Setter
    private Runnable onRefreshCallback;

	public ProductCardManager(ProductService productService, User currentUser, ProductListFrame parentFrame,
			DecimalFormat priceFormat) { // <-- ИЗМЕНИТЕ ТИП
		this.productService = productService;
		this.currentUser = currentUser;
		this.parentFrame = parentFrame;
		this.priceFormat = priceFormat;
	}

    public JPanel createProductCard(Product product) {
		ProductCardContext ctx = new ProductCardContext(product);

		JPanel card = new JPanel(new BorderLayout(15, 10));
		card.setBackground(ctx.getBg());
		card.setBorder(BorderFactory.createLineBorder(ctx.getBorderColor(), 2));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

		setupCardDoubleClick(card, product);

		card.add(createPhotoPanel(ctx), BorderLayout.WEST);
		card.add(createInfoPanel(ctx), BorderLayout.CENTER);
		card.add(createRightPanel(ctx), BorderLayout.EAST);

		return card;
	}

	private JPanel createPhotoPanel(ProductCardContext ctx) {
		JPanel photoPanel = new JPanel(new BorderLayout());
		photoPanel.setBackground(ctx.getBg());
		photoPanel.setPreferredSize(new Dimension(150, 120));
		photoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ctx.getBorderColor(), 1),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		JLabel image = new JLabel();
		image.setHorizontalAlignment(SwingConstants.CENTER);
		image.setVerticalAlignment(SwingConstants.CENTER);
		image.setIcon(ImageLoader.loadImage(ctx.getProduct().getPhoto(), 120, 120));
		photoPanel.add(image);

		return photoPanel;
	}

	private JPanel createInfoPanel(ProductCardContext ctx) {
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.setBackground(ctx.getBg());
		infoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ctx.getBorderColor(), 1),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));

		infoPanel.add(createHeaderPanel(ctx));
		infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));

		infoPanel.add(createInfoLabel("Описание: " + safe(ctx.getProduct().getDescription()), ctx.getTextColor()));
		infoPanel.add(
				createInfoLabel("Производитель: " + ctx.getProduct().getManufacturer().getName(), ctx.getTextColor()));
		infoPanel.add(createInfoLabel("Поставщик: " + ctx.getProduct().getSupplier().getName(), ctx.getTextColor()));

		addPriceInfo(infoPanel, ctx);
		infoPanel.add(createInfoLabel("Ед. изм.: " + ctx.getProduct().getUnit().getName(), ctx.getTextColor()));
		addQuantityInfo(infoPanel, ctx);

		return infoPanel;
	}

	private JPanel createHeaderPanel(ProductCardContext ctx) {
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(null);
		headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel title = new JLabel(ctx.getProduct().getCategory().getName() + " | " + ctx.getProduct().getName());
		title.setFont(new Font("Times New Roman", Font.BOLD, 14));
		title.setForeground(ctx.getTextColor());
		headerPanel.add(title, BorderLayout.WEST);

		if (currentUser.isAdmin()) {
			headerPanel.add(createDeleteButton(ctx.getProduct()), BorderLayout.EAST);
		}

		return headerPanel;
	}

	private JButton createDeleteButton(Product product) {
		JButton deleteButton = new JButton("Удалить");
		deleteButton.setBackground(Color.decode("#FF6B6B"));
		deleteButton.setForeground(Color.BLACK);
		deleteButton.setFont(new Font("Times New Roman", Font.BOLD, 11));
		deleteButton.setPreferredSize(new Dimension(75, 25));
		deleteButton.setMaximumSize(new Dimension(75, 25));
		deleteButton.setFocusPainted(false);
		deleteButton.addActionListener(e -> handleDelete(product));
		return deleteButton;
	}

	private void handleDelete(Product product) {
		if (ConfirmationDialog.confirmDelete(parentFrame, product.getName())) {
			try {
				productService.deleteProduct(product.getArticle());
				if (onRefreshCallback != null) {
					onRefreshCallback.run();
				}
				JOptionPane.showMessageDialog(parentFrame, "Товар успешно удалён", "Успешно",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IllegalStateException ex) {
				ConfirmationDialog.showDeleteError(parentFrame, product.getName());
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(parentFrame, "Ошибка при удалении: " + ex.getMessage(), "Ошибка",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void addPriceInfo(JPanel panel, ProductCardContext ctx) {
		if (ctx.getDiscount() > 0) {
			BigDecimal finalPrice = ctx.getProduct().getPrice()
					.multiply(BigDecimal.valueOf(1 - ctx.getDiscount() / 100));
			String priceHtml = "<html>Цена: <font color='red'><strike>"
					+ priceFormat.format(ctx.getProduct().getPrice()) + " ₽</strike></font>"
					+ "  <font color='black'><b>" + priceFormat.format(finalPrice) + " ₽</b></font></html>";
			JLabel priceLabel = new JLabel(priceHtml);
			priceLabel.setFont(new Font("Times New Roman", Font.PLAIN, 12));
			priceLabel.setForeground(ctx.getTextColor());
			priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			priceLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			panel.add(priceLabel);
		} else {
			panel.add(createInfoLabel("Цена: " + priceFormat.format(ctx.getProduct().getPrice()) + " ₽",
					ctx.getTextColor()));
		}
	}

	private void addQuantityInfo(JPanel panel, ProductCardContext ctx) {
		JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		qtyPanel.setBackground(ctx.getBg());
		qtyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		qtyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

		JLabel qty = new JLabel("Количество: " + ctx.getProduct().getQuantity());
		qty.setFont(new Font("Times New Roman", Font.PLAIN, 12));

		if (!ctx.isInStock()) {
			qty.setOpaque(true);
			qty.setBackground(Color.CYAN);
			qty.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		} else {
			qty.setForeground(ctx.getTextColor());
		}

		qtyPanel.add(qty);
		panel.add(qtyPanel);
	}

	private JPanel createRightPanel(ProductCardContext ctx) {
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.setBackground(ctx.getBg());
		rightPanel.setPreferredSize(new Dimension(140, 140));
		rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		if (ctx.getDiscount() > 0) {
			rightPanel.add(createDiscountPanel(ctx), BorderLayout.CENTER);
		}

		return rightPanel;
	}

	private JPanel createDiscountPanel(ProductCardContext ctx) {
		JPanel discountPanel = new JPanel(new GridBagLayout());
		discountPanel.setBackground(ctx.getBg());
		discountPanel.setPreferredSize(new Dimension(120, 80));
		discountPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(ctx.getBorderColor(), 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.CENTER;

		JLabel discountTitle = new JLabel("Действующая скидка");
		discountTitle.setFont(new Font("Times New Roman", Font.PLAIN, 10));
		discountTitle.setForeground(ctx.getTextColor());
		discountTitle.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridy = 0;
		discountPanel.add(discountTitle, gbc);

		JLabel discountLabel = new JLabel((int) ctx.getDiscount() + "%");
		discountLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
		discountLabel.setForeground(Color.RED);
		gbc.gridy = 1;
		discountPanel.add(discountLabel, gbc);

		return discountPanel;
	}

	private void setupCardDoubleClick(JPanel card, Product product) {
		if (currentUser.isAdmin()) {
			card.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (e.getClickCount() == 2) {
						openEditProductForm(product);
					}
				}
			});
			card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	}

	private void openEditProductForm(Product product) {
		ProductFormFrame form = new ProductFormFrame(parentFrame, product);
		form.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent e) {
				if (onRefreshCallback != null) {
					onRefreshCallback.run();
				}
			}
		});
		form.setVisible(true);
	}

	private JLabel createInfoLabel(String text, Color textColor) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		label.setForeground(textColor);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		return label;
	}

	private String safe(String text) {
		return text == null ? "" : text;
	}
}
