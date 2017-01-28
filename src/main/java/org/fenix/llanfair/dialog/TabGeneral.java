package org.fenix.llanfair.dialog;

import org.fenix.llanfair.Language;
import org.fenix.llanfair.Llanfair;
import org.fenix.llanfair.config.Accuracy;
import org.fenix.llanfair.config.Compare;
import org.fenix.llanfair.config.Settings;
import org.fenix.utils.UserSettings;
import org.fenix.utils.gui.GBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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

	private JCheckBox useDefaultSplitsPath;

	private JTextField customSplitsPath;

	private JButton selectCustomSplitsPath;

	private ButtonGroup compare;

	private JLabel compareText;

	private ButtonGroup accuracy;

	private JLabel accuracyText;

	private JCheckBox warnOnReset;

	private JLabel windowSizeLabel;

	private JCheckBox windowUserResizable;

	private JTextField windowSize;

	private JLabel windowSizeUnitsText;

	private JTextField maxRecentFiles;

	private JLabel maxRecentFilesLabel;

	// ----------------------------------------------------------- CONSTRUCTORS

	TabGeneral() {
		language = new JComboBox(Language.LANGUAGES);
		language.setRenderer(new LocaleListRenderer());
		language.setSelectedItem(Settings.language.get());
		language.addActionListener(this);

		alwaysOnTop = new JCheckBox("" + Language.setting_alwaysOnTop);
		alwaysOnTop.setSelected(Settings.alwaysOnTop.get());

		useDefaultSplitsPath = new JCheckBox("" + Language.setting_useDefaultSplitsPath);
		useDefaultSplitsPath.setSelected(Settings.useDefaultSplitsPath.get());
		useDefaultSplitsPath.addActionListener(this);

		String path = UserSettings.getSplitsPath(null);
		if (path == null)
			path = "";

		customSplitsPath = new JTextField(path);
		customSplitsPath.setEnabled(!Settings.useDefaultSplitsPath.get());
		customSplitsPath.setColumns(30);

		selectCustomSplitsPath = new JButton("" + Language.SELECT_SPLITS_DIR);
		selectCustomSplitsPath.addActionListener(this);
		selectCustomSplitsPath.setEnabled(!Settings.useDefaultSplitsPath.get());

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

		windowSizeLabel = new JLabel("" + Language.WINDOW_SIZE);

		windowUserResizable = new JCheckBox("" + Language.setting_windowUserResizable);
		windowUserResizable.setSelected(Settings.windowUserResizable.get());
		windowUserResizable.addActionListener(this);

		String windowWidthText = "";
		if (Settings.windowWidth.get() != null)
			windowWidthText = "" + Settings.windowWidth.get();
		windowSize = new JTextField(windowWidthText, 4);
		windowSize.setEnabled(!windowUserResizable.isSelected());
		windowSize.addActionListener(this);

		maxRecentFiles = new JTextField("" + Settings.maxRecentFiles.get(), 4);
		maxRecentFiles.addActionListener(this);

		windowSizeUnitsText = new JLabel("" + Language.setting_windowWidth);
		maxRecentFilesLabel = new JLabel("" + Language.setting_maxRecentFiles);

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
		} else if (source.equals(windowUserResizable)) {
			windowSize.setEnabled(!windowUserResizable.isSelected());
		} else if (source.equals(useDefaultSplitsPath)) {
			Settings.useDefaultSplitsPath.set(useDefaultSplitsPath.isSelected());
			if (useDefaultSplitsPath.isEnabled())
				Settings.customSplitsPath.set(null);
			String path = UserSettings.getSplitsPath(null);
			if (path == null)
				path = "";
			customSplitsPath.setText(path);
			boolean enabled = !Settings.useDefaultSplitsPath.get();
			customSplitsPath.setEnabled(enabled);
			selectCustomSplitsPath.setEnabled(enabled);
		} else if (source.equals(selectCustomSplitsPath)) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int action = chooser.showOpenDialog(this);
			if (action == JFileChooser.APPROVE_OPTION) {
				customSplitsPath.setText(chooser.getSelectedFile().toString());
			}
		}
	}

	// -------------------------------------------------------------- INHERITED

	void doDelayedSettingChange() throws InvalidSettingException {
		Settings.alwaysOnTop.set(alwaysOnTop.isSelected());

		Settings.windowUserResizable.set(windowUserResizable.isSelected());

		if (!windowUserResizable.isSelected()) {
			int windowWidth;
			try {
				windowWidth = Integer.parseInt(windowSize.getText().trim());
			}
			catch (Exception ex) {
				throw new InvalidSettingException(this, windowSize, "" + Language.error_window_width);
			}

			Settings.windowWidth.set(windowWidth);
		}

		int numRecentFiles;
		try {
			numRecentFiles = Integer.parseInt(maxRecentFiles.getText().trim());
		}
		catch (Exception ex) {
			throw new InvalidSettingException(this, maxRecentFiles, "" + Language.error_max_recent_files);
		}

		Settings.maxRecentFiles.set(numRecentFiles);

		if (!Settings.useDefaultSplitsPath.get()) {
			String path = customSplitsPath.getText().trim();
			if (!new File(path).exists()) {
				throw new InvalidSettingException(this, customSplitsPath, "" + Language.error_splits_path);
			}
			Settings.customSplitsPath.set(path);
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
				alwaysOnTopText, GBC.grid(0, 0).anchor(GBC.LINE_END).insets(5, 10)
		);
		add(alwaysOnTop, GBC.grid(1, 0).anchor(GBC.LINE_START));
		add(warnOnReset, GBC.grid(1, 1).anchor(GBC.LINE_START));

		JPanel panelSplitsPath = new JPanel(new GridBagLayout()); {
			panelSplitsPath.add(
				useDefaultSplitsPath,
				GBC.grid(0, 0, 2, 1).anchor(GBC.LINE_START)
			);
			panelSplitsPath.add(
				customSplitsPath,
			    GBC.grid(0, 1).anchor(GBC.LINE_START).insets(0, 5)
			);
			panelSplitsPath.add(
				selectCustomSplitsPath,
			    GBC.grid(1, 1).anchor(GBC.LINE_START)
			);
		};
		add(panelSplitsPath, GBC.grid(1, 2).anchor(GBC.LINE_START));

		//add(languageText, GBC.grid(0, 2).anchor(GBC.LINE_END).insets(10, 10));
		//add(language, GBC.grid(1, 2).fill(GBC.HORIZONTAL));

		JPanel comparePanel = new JPanel(new GridLayout(0, 1)); {
			Enumeration<AbstractButton> buttons = compare.getElements();
			while (buttons.hasMoreElements()) {
				comparePanel.add(buttons.nextElement());
			}
		}
		add(compareText, GBC.grid(0, 3).anchor(GBC.FIRST_LINE_END).insets(14, 10));
		add(comparePanel, GBC.grid(1, 3).fill(GBC.HORIZONTAL).insets(10, 0, 0, 0));

		JPanel accuracyPanel = new JPanel(new GridLayout(0, 1)); {
			Enumeration<AbstractButton> buttons = accuracy.getElements();
			while (buttons.hasMoreElements()) {
				accuracyPanel.add(buttons.nextElement());
			}
		}
		add(accuracyText, GBC.grid(0, 4).anchor(GBC.FIRST_LINE_END).insets(14, 10));
		add(accuracyPanel, GBC.grid(1, 4).fill(GBC.HORIZONTAL).insets(10, 0));

		add(windowSizeLabel, GBC.grid(0, 5).anchor(GBC.LINE_END).insets(5, 10));
		add(windowUserResizable, GBC.grid(1, 5).anchor(GBC.LINE_START));
		JPanel windowSizeContainer = new JPanel();
		windowSizeContainer.add(windowSize);
		windowSizeContainer.add(windowSizeUnitsText);
		add(windowSizeContainer, GBC.grid(1, 6).anchor(GBC.LINE_START));

		add(maxRecentFilesLabel, GBC.grid(0, 7).anchor(GBC.LINE_END).insets(5, 10));
		add(maxRecentFiles, GBC.grid(1, 7).anchor(GBC.LINE_START).insets(0, 5));

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
