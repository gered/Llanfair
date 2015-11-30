package org.fenix.llanfair.dialog;

import org.fenix.llanfair.Llanfair;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * LlanfairDialog
 *
 * @author Xavier Sencert
 * @date   24 juil. 2012
 */
public class LlanfairDialog extends JDialog {

	// ATTRIBUTS

	public void display(boolean lockNativeInputs, final Llanfair llanfair) {
		if (lockNativeInputs) {
			llanfair.setIgnoreNativeInputs(true);
			addWindowListener(new WindowAdapter() {
				@Override public void windowClosed(WindowEvent e) {
					llanfair.setIgnoreNativeInputs(false);
				}
			});
		}
		setAlwaysOnTop(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		pack();
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}

}
