//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.fenix.utils.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GBC extends GridBagConstraints {
	public static final int LS = 21;
	public static final int LE = 22;
	public static final int PS = 19;
	public static final int PE = 20;
	public static final int BL = 512;
	public static final int BT = 768;
	public static final int FLS = 23;
	public static final int FLE = 24;
	public static final int LLS = 25;
	public static final int LLE = 26;
	public static final int H = 2;
	public static final int V = 3;
	public static final int C = 10;
	public static final int B = 1;

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
