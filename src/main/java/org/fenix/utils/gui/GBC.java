//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.fenix.utils.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GBC extends GridBagConstraints {
	public GBC() {
	}

	public static GBC grid(int x, int y, int w, int h) {
		GBC gbc = new GBC();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		return gbc;
	}

	public static GBC grid(int x, int y) {
		return grid(x, y, 1, 1);
	}

	public GBC weight(double x, double y) {
		this.weightx = x;
		this.weighty = y;
		return this;
	}

	public GBC anchor(int anchor) {
		this.anchor = anchor;
		return this;
	}

	public GBC fill(int fill) {
		this.fill = fill;
		return this;
	}

	public GBC insets(int top, int left, int bottom, int right) {
		this.insets = new Insets(top, left, bottom, right);
		return this;
	}

	public GBC insets(int y, int x) {
		return this.insets(y, x, y, x);
	}

	public GBC padding(int x, int y) {
		this.ipadx = x;
		this.ipady = y;
		return this;
	}
}
