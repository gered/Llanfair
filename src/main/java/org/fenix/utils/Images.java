//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.fenix.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Images {
	public Images() {
	}

	public static ImageIcon rescale(ImageIcon icon, int size) {
		if(icon == null) {
			throw new NullPointerException("Icon is null");
		} else if(size <= 0) {
			throw new IllegalArgumentException("Illegal size: " + size);
		} else {
			int height = icon.getIconHeight();
			int width = icon.getIconWidth();
			double scale;
			BufferedImage buffer;
			if(height > width) {
				scale = (double)size / (double)height;
				buffer = new BufferedImage((int)((double)width * scale), size, 2);
			} else {
				scale = (double)size / (double)width;
				buffer = new BufferedImage(size, (int)((double)height * scale), 2);
			}

			Graphics2D g2 = buffer.createGraphics();
			if(scale < 1.0D) {
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			} else {
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			}

			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.scale(scale, scale);
			icon.paintIcon(null, g2, 0, 0);
			g2.dispose();
			return new ImageIcon(buffer);
		}
	}
}
