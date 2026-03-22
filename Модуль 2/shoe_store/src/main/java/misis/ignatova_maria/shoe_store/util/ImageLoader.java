package misis.ignatova_maria.shoe_store.util;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ImageLoader {

    private static final String IMAGES_PATH = "/static/images/";
    private static final String PRODUCTS_PATH = IMAGES_PATH + "products/";

    public static ImageIcon loadImage(String fileName, int maxWidth, int maxHeight) {
        String path = (fileName != null && !fileName.isEmpty())
                ? PRODUCTS_PATH + fileName
                : IMAGES_PATH + "picture.png";

        URL imgUrl = ImageLoader.class.getResource(path);

        if (imgUrl == null) {
            return loadPlaceholder(maxWidth, maxHeight);
        }

        return scaleIcon(imgUrl, maxWidth, maxHeight);
    }

    public static ImageIcon loadLogo() {

        String[] paths = {
                "/static/images/Icon.ico",
                "/static/images/Icon.png",
                "/static/images/Icon.JPG"
        };

        for (String path : paths) {
            URL url = ImageLoader.class.getResource(path);
            if (url != null) {
                return scaleIcon(url, 120, 60);
            }
        }

        return null;
    }

    public static ImageIcon loadIcon() {

        String path = "/static/images/Icon.JPG";
        URL url = ImageLoader.class.getResource(path);
        if (url != null) {
            return scaleIcon(url, 32, 32);

        }

        return new ImageIcon();
    }

    private static ImageIcon loadPlaceholder(int width, int height) {
        String path = IMAGES_PATH + "picture.png";
        URL url = ImageLoader.class.getResource(path);

        if (url != null) {
            return scaleIcon(url, width, height);
        }

        return new ImageIcon();
    }

    private static ImageIcon scaleIcon(URL url, int maxWidth, int maxHeight) {
        ImageIcon original = new ImageIcon(url);

        int originalWidth = original.getIconWidth();
        int originalHeight = original.getIconHeight();

        if (originalWidth <= 0 || originalHeight <= 0) {
            return original;
        }

        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double scale = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);

        if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
            return original;
        }

        Image scaled = original.getImage()
                .getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

        return new ImageIcon(scaled);
    }
}