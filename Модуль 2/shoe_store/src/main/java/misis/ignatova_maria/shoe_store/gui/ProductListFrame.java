package misis.ignatova_maria.shoe_store.gui;

import misis.ignatova_maria.shoe_store.entity.Product;
import misis.ignatova_maria.shoe_store.entity.User;
import misis.ignatova_maria.shoe_store.service.ProductService;
import misis.ignatova_maria.shoe_store.util.ImageLoader;
import misis.ignatova_maria.shoe_store.util.SpringContext;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class ProductListFrame extends JFrame {

    private final ProductService productService;
    private final User currentUser;
    private JPanel cardsPanel;
    private JScrollPane scrollPane;
    private final DecimalFormat priceFormat;

    public ProductListFrame(User user) {
        this.productService = SpringContext.getBean(ProductService.class);
        this.currentUser = user;
        this.priceFormat = new DecimalFormat("#,##0.00");

        initUI();
        loadProducts();
    }

    private void initUI() {
        setTitle("Список товаров");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setIconImage(ImageLoader.loadIcon().getImage());

        UIManager.put("Label.font", new Font("Times New Roman", Font.PLAIN, 12));
        UIManager.put("Button.font", new Font("Times New Roman", Font.PLAIN, 12));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel);

        JPanel topPanel = createTopPanel();
        JPanel catalogPanel = createCatalogPanel();

        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
        northContainer.add(topPanel);
        northContainer.add(catalogPanel);

        mainPanel.add(northContainer, BorderLayout.NORTH);

        cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(Color.WHITE);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // 🔥 отступ

        scrollPane = new JScrollPane(cardsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Левая часть с логотипом
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setBackground(Color.WHITE);

        ImageIcon logoIcon = ImageLoader.loadLogo();
        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.LEFT);

        if (logoIcon != null && logoIcon.getImage() != null) {
            logoLabel.setIcon(logoIcon);
            logoLabel.setText(null);
        } else {
            logoLabel.setText("МАГАЗИН ОБУВИ");
            logoLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        }

        logoPanel.add(logoLabel);
        panel.add(logoPanel, BorderLayout.WEST);

        // Правая часть с пользователем и кнопкой выхода
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(Color.WHITE);

        String fio = currentUser.getFullName();
        if (fio == null || fio.isBlank()) {
            fio = currentUser.getLogin();
        }

        JLabel userLabel = new JLabel("Пользователь: " + fio);
        userLabel.setFont(new Font("Times New Roman", Font.PLAIN, 14));

        JButton logout = new JButton("Выйти");
        logout.setBackground(Color.decode("#00FA9A"));
        logout.setOpaque(true);
        logout.setContentAreaFilled(true);
        logout.setBorderPainted(false);
        logout.setFocusPainted(false);
        logout.setFont(new Font("Times New Roman", Font.BOLD, 12));
        logout.addActionListener(e -> logout());

        userPanel.add(userLabel);
        userPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        userPanel.add(logout);

        panel.add(userPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.decode("#7FFF00"));

        JLabel title = new JLabel("КАТАЛОГ ТОВАРОВ");
        title.setFont(new Font("Times New Roman", Font.BOLD, 24));

        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); // 🔥 отступ снизу

        panel.add(title, BorderLayout.WEST);

        return panel;
    }

    private void loadProducts() {
        List<Product> products = productService.getAllProducts();

        cardsPanel.removeAll();

        for (Product product : products) {
            cardsPanel.add(createProductCard(product));
            cardsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel createProductCard(Product product) {

        boolean inStock = product.getQuantity() != null && product.getQuantity() > 0;
        double discount = product.getDiscount() != null ? product.getDiscount().doubleValue() : 0;

        Color bg = discount > 15 ? Color.decode("#2E8B57") : Color.WHITE;

        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createLineBorder(Color.decode("#00FA9A"), 2));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        // ===== ФОТО =====
        JPanel photoPanel = new JPanel(new BorderLayout());
        photoPanel.setBackground(bg);
        photoPanel.setPreferredSize(new Dimension(150, 120));

        JLabel image = new JLabel();
        image.setHorizontalAlignment(SwingConstants.CENTER);
        image.setVerticalAlignment(SwingConstants.CENTER);
        image.setIcon(ImageLoader.loadImage(product.getPhoto(), 120, 120));
        photoPanel.add(image);

        // ===== ОПИСАНИЕ =====
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(bg);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        Color textColor = (discount > 15 && inStock) ? Color.WHITE : Color.BLACK;

        JLabel title = new JLabel(product.getCategory().getName() + " | " + product.getName());
        title.setFont(new Font("Times New Roman", Font.BOLD, 14));
        title.setForeground(textColor);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(title);
        infoPanel.add(new JLabel("Описание: " + safe(product.getDescription())));
        infoPanel.add(new JLabel("Производитель: " + product.getManufacturer().getName()));
        infoPanel.add(new JLabel("Поставщик: " + product.getSupplier().getName()));

        // ===== ЦЕНА =====
        String priceText;
        if (discount > 0) {
            BigDecimal finalPrice = product.getPrice()
                    .multiply(BigDecimal.valueOf(1 - discount / 100));
            priceText = String.format(
                    "<html>Цена: <font color='red'><strike>%s ₽</strike></font> <b>%s ₽</b></html>",
                    priceFormat.format(product.getPrice()),
                    priceFormat.format(finalPrice)
            );
        } else {
            priceText = "Цена: " + priceFormat.format(product.getPrice()) + " ₽";
        }

        JLabel priceLabel = new JLabel(priceText);
        priceLabel.setForeground(textColor);
        priceLabel.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(priceLabel);

        infoPanel.add(new JLabel("Единица измерения: " + product.getUnit().getName()));

        // ===== КОЛИЧЕСТВО (ТОЛЬКО ОНО ПОДСВЕЧИВАЕТСЯ) =====
        JLabel qty = new JLabel("Количество: " + product.getQuantity());
        qty.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (!inStock) {
            qty.setOpaque(true);
            qty.setBackground(Color.CYAN);
        }

        infoPanel.add(qty);

        // ===== СКИДКА =====
        JPanel discountPanel = new JPanel(new GridBagLayout());
        discountPanel.setBackground(bg);
        discountPanel.setPreferredSize(new Dimension(120, 100));

        JLabel discountLabel = new JLabel(discount + "%");
        discountLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        discountLabel.setForeground(Color.RED);

        discountPanel.add(discountLabel);

        // ===== СБОРКА =====
        card.add(photoPanel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(discountPanel, BorderLayout.EAST);

        return card;
    }

    private String safe(String text) {
        return text == null ? "" : text;
    }

    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}