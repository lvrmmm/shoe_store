package misis.ignatova_maria.shoe_store.gui.renderer;

import misis.ignatova_maria.shoe_store.util.ImageLoader;
import javax.swing.table.DefaultTableCellRenderer;


public class ImageCellRenderer extends DefaultTableCellRenderer {

    private final int width;
    private final int height;

    public ImageCellRenderer(int width, int height) {
        this.width = width;
        this.height = height;
        setHorizontalAlignment(CENTER);
    }

    @Override
    protected void setValue(Object value) {
        String fileName = (String) value;
        setIcon(ImageLoader.loadImage(fileName, width, height));
        setText(null);
    }
}