package org.fenix.llanfair.dialog;

import org.fenix.llanfair.config.Settings;
import org.fenix.utils.gui.LinkedCheckBox;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author  Xavier "Xunkar" Sencert
 */
abstract class SettingsTab extends JPanel {

	protected Map<String, SCheckBox> checkBoxes;

	protected SettingsTab() {
		checkBoxes = new HashMap<String, SCheckBox>();
	}

	abstract void doDelayedSettingChange() throws InvalidSettingException;

	protected class SCheckBox extends LinkedCheckBox {

		private Settings.Property<Boolean> setting;

		SCheckBox(Settings.Property<Boolean> setting) {
			super();
			this.setting = setting;
			setText("" + setting);
			setSelected(setting.get());
		}

		@Override public void itemStateChanged(ItemEvent e) {
			super.itemStateChanged(e);
			setting.set(isSelected());
		}


	}
}
