package org.fenix.llanfair.dialog;

import org.fenix.llanfair.Language;
import org.fenix.llanfair.Segment;
import org.fenix.llanfair.config.Settings;
import org.fenix.utils.gui.GBC;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author  Xavier "Xunkar" Sencert
 */
public class TabComponents extends SettingsTab 
	implements ActionListener, ChangeListener {

	private static final List<Settings.Property<Boolean>> SCB_SETTINGS
			= new ArrayList<Settings.Property<Boolean>>();
	static {
		SCB_SETTINGS.add(Settings.footerDisplay);
		SCB_SETTINGS.add(Settings.footerUseSplitData);
		SCB_SETTINGS.add(Settings.footerShowDeltaLabels);
		SCB_SETTINGS.add(Settings.footerShowBestTime);
		SCB_SETTINGS.add(Settings.footerMultiline);
		SCB_SETTINGS.add(Settings.footerVerbose);
		SCB_SETTINGS.add(Settings.coreShowSegmentName);
		SCB_SETTINGS.add(Settings.coreShowSplitTime);
		SCB_SETTINGS.add(Settings.coreShowSegmentTime);
		SCB_SETTINGS.add(Settings.coreShowBestTime);
		SCB_SETTINGS.add(Settings.headerShowTitle);
		SCB_SETTINGS.add(Settings.graphDisplay);
		SCB_SETTINGS.add(Settings.coreShowSegmentTimer);
		SCB_SETTINGS.add(Settings.coreShowIcons);
		SCB_SETTINGS.add(Settings.headerShowSubtitle);
		SCB_SETTINGS.add(Settings.headerShowAttempts);
	}

	private JComboBox iconSizes;

	private JComboBox timerFont;

	private JSpinner timerSize;

	private JComboBox timerSegFont;

	private JSpinner timerSegSize;

	private JCheckBox timerSameFont;

	private JComboBox headerTitleFont;

	private JSpinner headerTitleSize;

	private JComboBox headerSubTitleFont;

	private JSpinner headerSubTitleSize;

	private JComboBox coreFont;

	private JSpinner coreFontSize;

	private JComboBox otherTimeFont;

	private JSpinner otherTimeSize;

	TabComponents() {
		super();

		for (Settings.Property<Boolean> setting : SCB_SETTINGS) {
			checkBoxes.put(setting.getKey(), new SCheckBox(setting));
		}
		// Checkboxes side effects
		checkBoxes.get(Settings.footerUseSplitData.getKey()).deactivates(
				checkBoxes.get(Settings.footerShowBestTime.getKey())
		);
		// Checkboxes requirements
		checkBoxes.get(Settings.footerShowBestTime.getKey()).requires(
				checkBoxes.get(Settings.footerUseSplitData.getKey()), false
		);
		checkBoxes.get(Settings.footerMultiline.getKey()).requires(
				checkBoxes.get(Settings.footerShowBestTime.getKey()), true
		);

		iconSizes     = new JComboBox(Segment.ICON_SIZES);
		iconSizes.setSelectedItem(Settings.coreIconSize.get());
		iconSizes.addActionListener(this);

		GraphicsEnvironment gEnv = GraphicsEnvironment
				.getLocalGraphicsEnvironment();

		// main timer font family and size selector
		String timerFontName = Settings.coreTimerFont.get().getName();
		timerFont = new JComboBox(gEnv.getAvailableFontFamilyNames());
		timerFont.setSelectedItem(timerFontName);
		timerFont.setPreferredSize(new Dimension(130, 22));
		timerFont.addActionListener(this);

		timerSize = new JSpinner(new SpinnerNumberModel(
			Settings.coreTimerFont.get().getSize(), 8, 240, 1)
		);
		timerSize.addChangeListener(this);

		// segment timer font family and size selector
		String timerSegFontName = Settings.coreSegmentTimerFont.get().getName();
		timerSegFont = new JComboBox(gEnv.getAvailableFontFamilyNames());
		timerSegFont.setSelectedItem(timerSegFontName);
		timerSegFont.setPreferredSize(new Dimension(130, 22));
		timerSegFont.addActionListener(this);

		timerSegSize = new JSpinner(new SpinnerNumberModel(
				Settings.coreSegmentTimerFont.get().getSize(), 8, 240, 1)
		);
		timerSegSize.addChangeListener(this);

		// header title font family and size selector
		String headerTitleFontName = Settings.headerTitleFont.get().getName();
		headerTitleFont = new JComboBox(gEnv.getAvailableFontFamilyNames());
		headerTitleFont.setSelectedItem(headerTitleFontName);
		headerTitleFont.setPreferredSize(new Dimension(130, 22));
		headerTitleFont.addActionListener(this);

		headerTitleSize = new JSpinner(new SpinnerNumberModel(
			Settings.headerTitleFont.get().getSize(), 8, 240, 1)
		);
		headerTitleSize.addChangeListener(this);

		// header sub-title font family and size selector
		String headerSubTitleFontName = Settings.headerSubTitleFont.get().getName();
		headerSubTitleFont = new JComboBox(gEnv.getAvailableFontFamilyNames());
		headerSubTitleFont.setSelectedItem(headerSubTitleFontName);
		headerSubTitleFont.setPreferredSize(new Dimension(130, 22));
		headerSubTitleFont.addActionListener(this);

		headerSubTitleSize = new JSpinner(new SpinnerNumberModel(
			Settings.headerSubTitleFont.get().getSize(), 8, 240, 1)
		);
		headerSubTitleSize.addChangeListener(this);

		// other/general font family and size selector
		String coreFontName = Settings.coreFont.get().getName();
		coreFont = new JComboBox(gEnv.getAvailableFontFamilyNames());
		coreFont.setSelectedItem(coreFontName);
		coreFont.setPreferredSize(new Dimension(130, 22));
		coreFont.addActionListener(this);

		coreFontSize = new JSpinner(new SpinnerNumberModel(
			Settings.coreFont.get().getSize(), 8, 240, 1)
		);
		coreFontSize.addChangeListener(this);

		// other timer font family and size selector
		String otherTimeFontName = Settings.coreOtherTimeFont.get().getName();
		otherTimeFont = new JComboBox(gEnv.getAvailableFontFamilyNames());
		otherTimeFont.setSelectedItem(otherTimeFontName);
		otherTimeFont.setPreferredSize(new Dimension(130, 22));
		otherTimeFont.addActionListener(this);

		otherTimeSize = new JSpinner(new SpinnerNumberModel(
			Settings.coreOtherTimeFont.get().getSize(), 8, 240, 1)
		);
		otherTimeSize.addChangeListener(this);


		// checkbox for linking the main timer and segment timer font family's
		timerSameFont = new JCheckBox("" + Language.USE_MAIN_FONT);
		timerSameFont.setSelected(timerSegFontName.equals(timerFontName));
		timerSegFont.setEnabled(!timerSameFont.isSelected());
		timerSameFont.addActionListener(this);

		place();
	}

	// -------------------------------------------------------------- CALLBACKS

	@Override public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source.equals(iconSizes)) {
			Settings.coreIconSize.set((Integer) iconSizes.getSelectedItem());

		} else if (source.equals(timerFont)) {
			String fontName = timerFont.getSelectedItem().toString();
			Font   font     = Font.decode(fontName).deriveFont(
					(float) Settings.coreTimerFont.get().getSize()
			);
			Settings.coreTimerFont.set(font);
			if (timerSameFont.isSelected()) {
				timerSegFont.setSelectedItem(fontName);
			}

		} else if (source.equals(timerSegFont)) {
			String fontName = timerSegFont.getSelectedItem().toString();
			Font font = Font.decode(fontName).deriveFont(
				(float) Settings.coreSegmentTimerFont.get().getSize()
			);
			Settings.coreSegmentTimerFont.set(font);

		} else if (source.equals(headerTitleFont)) {
			String fontName = headerTitleFont.getSelectedItem().toString();
			Font font = Font.decode(fontName).deriveFont(
				(float) Settings.headerTitleFont.get().getSize()
			);
			Settings.headerTitleFont.set(font);

		} else if (source.equals(headerSubTitleFont)) {
			String fontName = headerSubTitleFont.getSelectedItem().toString();
			Font font = Font.decode(fontName).deriveFont(
				(float) Settings.headerSubTitleFont.get().getSize()
			);
			Settings.headerSubTitleFont.set(font);

		} else if (source.equals(coreFont)) {
			String fontName = coreFont.getSelectedItem().toString();
			Font font = Font.decode(fontName).deriveFont(
				(float) Settings.coreFont.get().getSize()
			);
			Settings.coreFont.set(font);

		} else if (source.equals(otherTimeFont)) {
			String fontName = otherTimeFont.getSelectedItem().toString();
			Font font = Font.decode(fontName).deriveFont(
				(float) Settings.coreOtherTimeFont.get().getSize()
			);
			Settings.coreOtherTimeFont.set(font);

		} else if (source.equals(timerSameFont)) {
			timerSegFont.setEnabled(!timerSameFont.isSelected());
			if (timerSameFont.isSelected()) {
				int size = (Integer) timerSegSize.getValue();
				Settings.coreSegmentTimerFont.set(
						Settings.coreTimerFont.get().deriveFont((float) size)
				);
			}
		}
	}

	@Override public void stateChanged(ChangeEvent event) {
		Object source = event.getSource();
		if (source.equals(timerSize)) {
			int size = (Integer) timerSize.getValue();
			Settings.coreTimerFont.set(
					Settings.coreTimerFont.get().deriveFont((float) size)
			);
		} else if (source.equals(timerSegSize)) {
			int size = (Integer) timerSegSize.getValue();
			Settings.coreSegmentTimerFont.set(
					Settings.coreSegmentTimerFont.get().deriveFont((float) size)
			);
		} else if (source.equals(headerTitleSize)) {
			int size = (Integer) headerTitleSize.getValue();
			Settings.headerTitleFont.set(
				Settings.headerTitleFont.get().deriveFont((float) size)
			);

		} else if (source.equals(headerSubTitleSize)) {
			int size = (Integer) headerSubTitleSize.getValue();
			Settings.headerSubTitleFont.set(
				Settings.headerSubTitleFont.get().deriveFont((float) size)
			);

		} else if (source.equals(coreFontSize)) {
			int size = (Integer) coreFontSize.getValue();
			Settings.coreFont.set(
				Settings.coreFont.get().deriveFont((float) size)
			);

		} else if (source.equals(otherTimeSize)) {
			int size = (Integer) otherTimeSize.getValue();
			Settings.coreOtherTimeFont.set(
				Settings.coreOtherTimeFont.get().deriveFont((float) size)
			);

		}
	}

	// -------------------------------------------------------------- INHERITED

	@Override void doDelayedSettingChange() {}

	@Override public String toString() {
		return "" + Language.COMPONENTS;
	}

	// -------------------------------------------------------------- UTILITIES

	private void place() {
		setLayout(new GridBagLayout());

		JPanel fontPanel = new JPanel(new GridBagLayout()); {
			fontPanel.setBorder(
				BorderFactory.createTitledBorder("" + Language.PN_FONTS)
			);

			fontPanel.add(
					new JLabel("" + Language.setting_core_timerFont),
					GBC.grid(0, 0).anchor(GBC.LS).insets(0, 5)
			);
			fontPanel.add(timerFont, GBC.grid(1, 0));
			fontPanel.add(timerSize, GBC.grid(2, 0).insets(0, 5));

			fontPanel.add(
					new JLabel("" + Language.setting_core_segmentTimerFont),
					GBC.grid(0, 1).anchor(GBC.LS).insets(3, 5)
			);
			fontPanel.add(
					timerSameFont,
					GBC.grid(1, 1, 2, 1).anchor(GBC.LS).insets(3, 0)
			);
			fontPanel.add(timerSegFont, GBC.grid(1, 2));
			fontPanel.add(timerSegSize, GBC.grid(2, 2).insets(0, 5));

			fontPanel.add(
				new JLabel("" + Language.setting_header_titleFont),
				GBC.grid(0, 3).anchor(GBC.LS).insets(0, 5)
			);
			fontPanel.add(headerTitleFont, GBC.grid(1, 3));
			fontPanel.add(headerTitleSize, GBC.grid(2, 3).insets(0, 5));

			fontPanel.add(
				new JLabel("" + Language.setting_header_subTitleFont),
				GBC.grid(0, 4).anchor(GBC.LS).insets(0, 5)
			);
			fontPanel.add(headerSubTitleFont, GBC.grid(1, 4));
			fontPanel.add(headerSubTitleSize, GBC.grid(2, 4).insets(0, 5));

			fontPanel.add(
				new JLabel("" + Language.setting_core_font),
				GBC.grid(0, 5).anchor(GBC.LS).insets(0, 5)
			);
			fontPanel.add(coreFont, GBC.grid(1, 5));
			fontPanel.add(coreFontSize, GBC.grid(2, 5).insets(0, 5));

			fontPanel.add(
				new JLabel("" + Language.setting_core_otherTimeFont),
				GBC.grid(0, 6).anchor(GBC.LS).insets(0, 5)
			);
			fontPanel.add(otherTimeFont, GBC.grid(1, 6));
			fontPanel.add(otherTimeSize, GBC.grid(2, 6).insets(0, 5));
		}
		JPanel timerPanel = new JPanel(new GridBagLayout()); {
			timerPanel.add(
					new JLabel("" + Language.setting_core_iconSize),
					GBC.grid(0, 1).anchor(GBC.LE).insets(5, 5)
			);
			timerPanel.add(iconSizes, GBC.grid(1, 1).insets(5, 5));
			timerPanel.add(checkBoxes.get(Settings.coreShowIcons.getKey()), GBC.grid(0, 2, 2, 1).anchor(GBC.LS));
			timerPanel.add(checkBoxes.get(Settings.coreShowSegmentName.getKey()), GBC.grid(0, 3, 2, 1).anchor(GBC.LS));
			timerPanel.add(checkBoxes.get(Settings.coreShowSplitTime.getKey()), GBC.grid(0, 4, 2, 1).anchor(GBC.LS));
			timerPanel.add(checkBoxes.get(Settings.coreShowSegmentTime.getKey()), GBC.grid(0, 5, 2, 1).anchor(GBC.LS));
			timerPanel.add(checkBoxes.get(Settings.coreShowBestTime.getKey()), GBC.grid(0, 6, 2, 1).anchor(GBC.LS));
			timerPanel.add(checkBoxes.get(Settings.coreShowSegmentTimer.getKey()), GBC.grid(0, 7, 2, 1).anchor(GBC.LS));
			timerPanel.setBorder(
					BorderFactory.createTitledBorder("" + Language.TIMER)
			);
		}
		JPanel footerPanel = new JPanel(new GridBagLayout()); {
			footerPanel.add(checkBoxes.get(Settings.footerDisplay.getKey()), GBC.grid(0, 0).anchor(GBC.LS));
			footerPanel.add(checkBoxes.get(Settings.footerUseSplitData.getKey()), GBC.grid(0, 1).anchor(GBC.LS));
			footerPanel.add(checkBoxes.get(Settings.footerVerbose.getKey()), GBC.grid(0, 2).anchor(GBC.LS));
			footerPanel.add(checkBoxes.get(Settings.footerShowDeltaLabels.getKey()), GBC.grid(1, 0).anchor(GBC.LS));
			footerPanel.add(checkBoxes.get(Settings.footerShowBestTime.getKey()), GBC.grid(1, 1).anchor(GBC.LS));
			footerPanel.add(checkBoxes.get(Settings.footerMultiline.getKey()), GBC.grid(1, 2).anchor(GBC.LS));
			footerPanel.setBorder(
					BorderFactory.createTitledBorder("" + Language.FOOTER)
			);
		}
		JPanel miscPanel = new JPanel(new GridBagLayout()); {
			miscPanel.add(checkBoxes.get(Settings.headerShowSubtitle.getKey()), GBC.grid(0, 0).anchor(GBC.LS));
			miscPanel.add(checkBoxes.get(Settings.headerShowTitle.getKey()), GBC.grid(0, 1).anchor(GBC.LS));
			miscPanel.add(checkBoxes.get(Settings.graphDisplay.getKey()), GBC.grid(0, 2).anchor(GBC.LS));
			miscPanel.add(checkBoxes.get(Settings.headerShowAttempts.getKey()), GBC.grid(0, 3).anchor(GBC.LS));
			miscPanel.setBorder(
					BorderFactory.createTitledBorder("" + Language.MISC)
			);
		}
		add(fontPanel, GBC.grid(0, 0, 2, 1).fill(GBC.B).anchor(GBC.FLS));
		add(timerPanel, GBC.grid(2, 0, 1, 2).fill(GBC.B).anchor(GBC.FLS));
		add(footerPanel, GBC.grid(0, 1).fill(GBC.B).anchor(GBC.FLS));
		add(miscPanel, GBC.grid(1, 1).fill(GBC.B).anchor(GBC.FLS));
	}

}
