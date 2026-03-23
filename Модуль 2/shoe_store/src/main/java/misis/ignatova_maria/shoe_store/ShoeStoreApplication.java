package misis.ignatova_maria.shoe_store;

import misis.ignatova_maria.shoe_store.gui.LoginFrame;
import misis.ignatova_maria.shoe_store.util.ImageLoader;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class ShoeStoreApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ShoeStoreApplication.class)
                .headless(false)
                .run(args);

        // Запускаем Swing UI в потоке обработки событий
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });

        setDockIcon();
    }

    private static void setDockIcon() {

        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                ImageIcon icon = ImageLoader.loadIcon();
                if (icon != null && icon.getImage() != null) {
                    taskbar.setIconImage(icon.getImage());
                    System.out.println("Dock icon set successfully");
                } else {
                    System.err.println("Failed to load dock icon image");
                }
            } else {
                System.err.println("Taskbar icon feature not supported");
            }
        } else {
            System.err.println("Taskbar not supported on this platform");
        }
    }

}