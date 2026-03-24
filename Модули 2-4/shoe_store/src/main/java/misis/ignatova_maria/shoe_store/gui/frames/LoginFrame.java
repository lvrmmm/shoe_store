package misis.ignatova_maria.shoe_store.gui.frames;

import java.awt.*;

import javax.swing.*;

import misis.ignatova_maria.shoe_store.entity.Role;
import misis.ignatova_maria.shoe_store.entity.User;
import misis.ignatova_maria.shoe_store.service.UserService;
import misis.ignatova_maria.shoe_store.util.ImageLoader;
import misis.ignatova_maria.shoe_store.util.SpringContext;

public class LoginFrame extends JFrame {

	private final UserService userService;
	private JTextField loginField;
	private JPasswordField passwordField;

	public LoginFrame() {
		this.userService = SpringContext.getBean(UserService.class);
		initUI();
	}

	private void initUI() {
		setTitle("Вход в систему");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 300);
		setLocationRelativeTo(null);
		setIconImage(ImageLoader.loadIcon().getImage());

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(Color.WHITE);

		// Логотип
		JPanel logoPanel = new JPanel();
		logoPanel.setBackground(Color.WHITE);
		JLabel logoLabel = new JLabel(ImageLoader.loadLogo());
		logoPanel.add(logoLabel);

		// Поля ввода
		JPanel inputPanel = new JPanel(new GridBagLayout());
		inputPanel.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		inputPanel.add(new JLabel("Логин:"), gbc);
		gbc.gridx = 1;
		loginField = new JTextField(15);
		inputPanel.add(loginField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		inputPanel.add(new JLabel("Пароль:"), gbc);
		gbc.gridx = 1;
		passwordField = new JPasswordField(15);
		inputPanel.add(passwordField, gbc);

		// Кнопки
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.WHITE);
		JButton loginButton = new JButton("Войти");
		loginButton.setBackground(Color.decode("#00FA9A"));
		JButton guestButton = new JButton("Войти как гость");
		guestButton.setBackground(Color.decode("#00FA9A"));
		buttonPanel.add(loginButton);
		buttonPanel.add(guestButton);

		mainPanel.add(logoPanel, BorderLayout.NORTH);
		mainPanel.add(inputPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		add(mainPanel);

		loginButton.addActionListener(e -> onLogin());
		guestButton.addActionListener(e -> onGuestLogin());

		getRootPane().setDefaultButton(loginButton);
	}

	private void onLogin() {
		String login = loginField.getText().trim();
		String password = new String(passwordField.getPassword());

		if (login.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Введите логин и пароль", "Ошибка", JOptionPane.ERROR_MESSAGE);
			return;
		}

		userService.authenticate(login, password).ifPresentOrElse(user -> {
			dispose();
			new ProductListFrame(user).setVisible(true);
		}, () -> JOptionPane.showMessageDialog(this, "Неверный логин или пароль", "Ошибка", JOptionPane.ERROR_MESSAGE));
	}

	private void onGuestLogin() {
		User guest = new User();
		guest.setId(0);
		guest.setFullName("Гость");
		guest.setLogin("");
		guest.setPassword("");

		Role guestRole = new Role();
		guestRole.setId(0);
		guestRole.setName("Гость");
		guest.setRole(guestRole);

		dispose();
		new ProductListFrame(guest).setVisible(true);
	}
}
