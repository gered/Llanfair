//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.fenix.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class Resources {
	private String path;
	private ResourceBundle language;
	private MessageFormat formatter;

	public Resources() {
		this.path = "";
		this.formatter = new MessageFormat("");
		this.defaultLocaleChanged();
	}

	public Resources(String path) {
		if(path != null && !path.equals("")) {
			if (!path.endsWith("/"))
				path += "/";

			this.path = path;
			this.formatter = new MessageFormat("");
			this.defaultLocaleChanged();
		} else {
			throw new NullPointerException("Unspecified resource path");
		}
	}

	public String getString(String key) {
		return !this.language.keySet().contains(key)?null:this.language.getString(key);
	}

	public String getString(String key, Object[] parameters) {
		String string = this.getString(key);
		this.formatter.applyPattern(string);
		return this.formatter.format(parameters);
	}

	public Icon getIcon(String path) {
		String fullPath = this.path + "img/" + path;
		URL url = ClassLoader.getSystemResource(fullPath);

		try {
			return new ImageIcon(ImageIO.read(url));
		} catch (Exception var5) {
			throw new IllegalArgumentException("Cannot load: " + fullPath);
		}
	}

	public Image getImage(String path) {
		String fullPath = this.path + "img/" + path;
		URL url = ClassLoader.getSystemResource(fullPath);

		try {
			return Toolkit.getDefaultToolkit().createImage(url);
		} catch (Exception var5) {
			throw new IllegalArgumentException("Cannot load: " + fullPath);
		}
	}

	public InputStream getStream(String path) {
		return ClassLoader.getSystemResourceAsStream(this.path + path);
	}

	public ResourceBundle getBundle(String path) {
		return ResourceBundle.getBundle(this.path + path);
	}

	public final void defaultLocaleChanged() {
		String baseName;
		if (this.path.equals(""))
			baseName = "language";
		else
			baseName = this.path + "language";

		try {
			this.language = ResourceBundle.getBundle(baseName);
		} catch (Exception var2) {
			this.language = ResourceBundle.getBundle(baseName, Locale.ENGLISH);
		}

		this.formatter.setLocale(Locale.getDefault());
	}
}
