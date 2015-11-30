//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.fenix.utils.about;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;

public class HyperLabel extends JLabel implements MouseListener {
	private URI href;

	public HyperLabel(URL url, String text) {
		super(text);
		if(url == null) {
			throw new NullPointerException("URL is null");
		} else {
			this.decorate();

			try {
				this.href = url.toURI();
			} catch (URISyntaxException var4) {
				throw new IllegalStateException(url.toString());
			}

			this.addMouseListener(this);
		}
	}

	public HyperLabel(URL url, Icon icon) {
		this(url, (String)null);
		this.setIcon(icon);
	}

	public void mouseClicked(MouseEvent event) {
		try {
			Desktop.getDesktop().browse(this.href);
		} catch (IOException var3) {
			throw new IllegalStateException(var3.getLocalizedMessage());
		}
	}

	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	public void mousePressed(MouseEvent event) {
	}

	public void mouseReleased(MouseEvent event) {
	}

	private void decorate() {
		this.setForeground(Color.BLUE);
		this.setCursor(Cursor.getPredefinedCursor(12));
		if(this.getText() != null) {
			this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLUE));
		}

	}
}
