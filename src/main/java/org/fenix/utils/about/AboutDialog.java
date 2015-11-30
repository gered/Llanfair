//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.fenix.utils.about;

import org.fenix.utils.gui.GBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class AboutDialog extends JDialog implements ActionListener {
	private JLabel icon = new JLabel();
	private JLabel message;
	private org.fenix.utils.about.HyperLabel website;
	private org.fenix.utils.about.HyperLabel donate;
	private JButton okButton;

	public AboutDialog(Window owner, String title) {
		super(owner, title);
		this.icon.setIcon((Icon)UIManager.get("OptionPane.informationIcon"));
		this.okButton = new JButton("OK");
		this.okButton.addActionListener(this);
		this.donate = null;
		this.website = null;
		this.message = null;
		this.setResizable(false);
	}

	public void display() {
		this.setLayout(new GridBagLayout());
		this.add(this.icon, GBC.grid(0, 0).insets(20, 20).anchor(19));
		if(this.message != null) {
			this.add(this.message, GBC.grid(1, 0).insets(10, 0, 0, 15));
		}

		if(this.website != null || this.donate != null) {
			this.add(new JLabel(), GBC.grid(0, 1, 2, 1).insets(5, 0));
		}

		if(this.website != null) {
			this.add(this.website, GBC.grid(0, 2, 2, 1).insets(5, 0).anchor(10));
		}

		if(this.donate != null) {
			this.add(this.donate, GBC.grid(0, 3, 2, 1).insets(5, 0).anchor(10));
		}

		this.add(this.okButton, GBC.grid(0, 4, 2, 1).insets(10, 0));
		this.pack();
		this.setLocationRelativeTo(this.getOwner());
		this.setVisible(true);
	}

	public void setMessage(String message) {
		this.message = new JLabel(message);
	}

	public void setDonateLink(URL url, Icon icon) {
		if(url == null) {
			throw new NullPointerException("Donate URL is null");
		} else {
			this.donate = new org.fenix.utils.about.HyperLabel(url, icon);
		}
	}

	public void setWebsite(URL url) {
		this.setWebsite(url, null);
	}

	public void setWebsite(URL url, String text) {
		if(url == null) {
			throw new NullPointerException("Website URL is null");
		} else {
			if(text == null || text.equals("")) {
				text = url.toString();
			}

			this.website = new org.fenix.utils.about.HyperLabel(url, text);
		}
	}

	public void actionPerformed(ActionEvent event) {
		this.dispose();
	}
}
