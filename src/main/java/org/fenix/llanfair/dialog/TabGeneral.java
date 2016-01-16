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

	private JLabel windowSizeLabel;

	private JCheckBox windowAutoSize;

	private JTextField windowSize;

	private JLabel windowSizeUnitsText;

	// ----------------------------------------------------------- CONSTRUCTORS

	TabGeneral() {
		language = new JComboBox(Language.LANGUAGES);
		language.setRenderer(new LocaleListRenderer());
		language.setSelectedItem(Settings.language.get());
		language.addActionListener(this);

		alwaysOnTop = new JCheckBox("" + Language.setting_alwaysOnTop);
		alwaysOnTop.setSelected(Settings.alwaysOnTop.get());

		compare = new ButtonGroup();
		Compare setCmp = Settings.compareMethod.get();
		for (Compare method : Compare.values()) {
			JRadioButton radio = new JRadioButton("" + method);
			radio.setName(method.name());
			radio.setSelected(setCmp == method);
			radio.addActionListener(this);
			compare.add(radio);
		}

		accuracy        = new ButtonGroup();
		Accuracy setAcc = Settings.accuracy.get();
		for (Accuracy value : Accuracy.values()) {
			JRadioButton radio = new JRadioButton("" + value);
			radio.setName(value.name());
			radio.setSelected(setAcc == value);
			radio.addActionListener(this);
			accuracy.add(radio);
		}

		warnOnReset = new JCheckBox("" + Language.setting_warnOnReset);
		warnOnReset.setSelected(Settings.warnOnReset.get());
		warnOnReset.addActionListener(this);

		windowSizeLabel = new JLabel("Window Width");

		windowAutoSize = new JCheckBox("Autosize");
		windowAutoSize.setSelected(Settings.windowAutoSize.get());
		windowAutoSize.addActionListener(this);

		String windowWidthText = "";
		if (Settings.windowWidth.get() != null)
			windowWidthText = "" + Settings.windowWidth.get();
		windowSize = new JTextField(windowWidthText, 4);
		windowSize.setEnabled(!windowAutoSize.isSelected());
		windowSize.addActionListener(this);

		windowSizeUnitsText = new JLabel("pixels");

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
			Settings.language.set((Locale) language.getSelectedItem());
		} else if (source instanceof JRadioButton) {
			JRadioButton radio = (JRadioButton) source;
			try {
				Settings.compareMethod.set(
						Compare.valueOf(radio.getName())
				);
			} catch (Exception e) {
				Settings.accuracy.set(Accuracy.valueOf(radio.getName()));
			}
		} else if (source.equals(warnOnReset)) {
			Settings.warnOnReset.set(warnOnReset.isSelected());
		} else if (source.equals(windowAutoSize)) {
			windowSize.setEnabled(!windowAutoSize.isSelected());
		}
	}

	// -------------------------------------------------------------- INHERITED

	void doDelayedSettingChange() throws InvalidSettingException {
		Settings.alwaysOnTop.set(alwaysOnTop.isSelected());

		Settings.windowAutoSize.set(windowAutoSize.isSelected());

		if (!windowAutoSize.isSelected()) {
			int windowWidth;
			try {
				windowWidth = Integer.parseInt(windowSize.getText().trim());
			}
			catch (Exception ex) {
				throw new InvalidSettingException(this, windowSize, "Window Width must be a positive integer.");
			}

			Settings.windowWidth.set(windowWidth);
		}
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

		//add(languageText, GBC.grid(0, 2).anchor(GBC.LE).insets(10, 10));
		//add(language, GBC.grid(1, 2).fill(GBC.H));

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

		add(windowSizeLabel, GBC.grid(0, 5).anchor(GBC.LE).insets(5, 10));
		add(windowAutoSize, GBC.grid(1, 5).anchor(GBC.LS));
		JPanel windowSizeContainer = new JPanel();
		windowSizeContainer.add(windowSize);
		windowSizeContainer.add(windowSizeUnitsText);
		add(windowSizeContainer, GBC.grid(1, 6).anchor(GBC.LS));
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

			setIcon(Llanfair.getResources().getIcon("" + selected + ".png"));
			setText(Language.LOCALE_NAMES.get("" + selected));
			return this;
		}

	}

}
