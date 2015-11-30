package org.fenix.llanfair.dialog;

import org.fenix.llanfair.Language;
import org.fenix.llanfair.Llanfair;
import org.fenix.llanfair.config.Accuracy;
import org.fenix.llanfair.config.Compare;
import org.fenix.llanfair.config.Settings;
import org.fenix.utils.gui.GBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Locale;

/**
 *
 * @author  Xavier "Xunkar" Sencert
 */
public class TabGeneral extends SettingsTab implements ActionListener {

	// ------------------------------------------------------------- ATTRIBUTES

	private JComboBox language;

	private JLabel languageText;

	private JCheckBox alwaysOnTop;

	private JLabel alwaysOnTopText;

	private ButtonGroup compare;

	private JLabel compareText;

	private ButtonGroup accuracy;

	private JLabel accuracyText;

	private JCheckBox warnOnReset;

	// ----------------------------------------------------------- CONSTRUCTORS

	TabGeneral() {
		language = new JComboBox(Language.LANGUAGES);
		language.setRenderer(new LocaleListRenderer());
		language.setSelectedItem(Settings.GNR_LANG.get());
		language.addActionListener(this);

		alwaysOnTop = new JCheckBox("" + Language.setting_alwaysOnTop);
		alwaysOnTop.setSelected(Settings.GNR_ATOP.get());

		compare = new ButtonGroup();
		Compare setCmp = Settings.GNR_COMP.get();
		for (Compare method : Compare.values()) {
			JRadioButton radio = new JRadioButton("" + method);
			radio.setName(method.name());
			radio.setSelected(setCmp == method);
			radio.addActionListener(this);
			compare.add(radio);
		}

		accuracy        = new ButtonGroup();
		Accuracy setAcc = Settings.GNR_ACCY.get();
		for (Accuracy value : Accuracy.values()) {
			JRadioButton radio = new JRadioButton("" + value);
			radio.setName(value.name());
			radio.setSelected(setAcc == value);
			radio.addActionListener(this);
			accuracy.add(radio);
		}

		warnOnReset = new JCheckBox("" + Language.setting_warnOnReset);
		warnOnReset.setSelected(Settings.GNR_WARN.get());
		warnOnReset.addActionListener(this);

		languageText    = new JLabel("" + Language.setting_language);
		alwaysOnTopText = new JLabel("" + Language.APPLICATION);
		compareText     = new JLabel("" + Language.COMPARE_METHOD);
		accuracyText    = new JLabel("" + Language.ACCURACY);

		place();
	}

	// -------------------------------------------------------------- CALLBACKS

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source.equals(language)) {
			Settings.GNR_LANG.set((Locale) language.getSelectedItem());
		} else if (source instanceof JRadioButton) {
			JRadioButton radio = (JRadioButton) source;
			try {
				Settings.GNR_COMP.set(
						Compare.valueOf(radio.getName())
				);
			} catch (Exception e) {
				Settings.GNR_ACCY.set(Accuracy.valueOf(radio.getName()));
			}
		} else if (source.equals(warnOnReset)) {
			Settings.GNR_WARN.set(warnOnReset.isSelected());
		}
	}

	// -------------------------------------------------------------- INHERITED

	void doDelayedSettingChange() {
		Settings.GNR_ATOP.set(alwaysOnTop.isSelected());
	}

	/**
	 * Returns the localized name of this tab.
	 */
	@Override public String toString() {
		return "" + Language.GENERAL;
	}

	// -------------------------------------------------------------- UTILITIES

	/**
	 * Places all sub-components within this panel.
	 */
	private void place() {
		setLayout(new GridBagLayout());

		add(
				alwaysOnTopText, GBC.grid(0, 0).anchor(GBC.LE).insets(5, 10)
		);
		add(alwaysOnTop, GBC.grid(1, 0).anchor(GBC.LS));
		add(warnOnReset, GBC.grid(1, 1).anchor(GBC.LS));

		add(languageText, GBC.grid(0, 2).anchor(GBC.LE).insets(10, 10));
		add(language, GBC.grid(1, 2).fill(GBC.H));

		JPanel comparePanel = new JPanel(new GridLayout(0, 1)); {
			Enumeration<AbstractButton> buttons = compare.getElements();
			while (buttons.hasMoreElements()) {
				comparePanel.add(buttons.nextElement());
			}
		}
		add(compareText, GBC.grid(0, 3).anchor(GBC.FLE).insets(14, 10));
		add(comparePanel, GBC.grid(1, 3).fill(GBC.H).insets(10, 0, 0, 0));

		JPanel accuracyPanel = new JPanel(new GridLayout(0, 1)); {
			Enumeration<AbstractButton> buttons = accuracy.getElements();
			while (buttons.hasMoreElements()) {
				accuracyPanel.add(buttons.nextElement());
			}
		}
		add(accuracyText, GBC.grid(0, 4).anchor(GBC.FLE).insets(14, 10));
		add(accuracyPanel, GBC.grid(1, 4).fill(GBC.H).insets(10, 0));
	}

	// --------------------------------------------------------- INTERNAL TYPES

	class LocaleListRenderer extends DefaultListCellRenderer {

		public LocaleListRenderer() {
			setVerticalAlignment(CENTER);
		}

		@Override public Component getListCellRendererComponent(
				JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus
		) {
			super.getListCellRendererComponent(
					list, value, index, isSelected, cellHasFocus
			);
			Locale selected = (Locale) value;

			setIcon(Llanfair.getResources().getIcon("" + selected));
			setText(Language.LOCALE_NAMES.get("" + selected));
			return this;
		}

	}

}
