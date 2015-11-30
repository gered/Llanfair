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
		SCB_SETTINGS.add(Settings.FOO_SHOW);
		SCB_SETTINGS.add(Settings.FOO_SPLT);
		SCB_SETTINGS.add(Settings.FOO_DLBL);
		SCB_SETTINGS.add(Settings.FOO_BEST);
		SCB_SETTINGS.add(Settings.FOO_LINE);
		SCB_SETTINGS.add(Settings.FOO_VERB);
		SCB_SETTINGS.add(Settings.COR_NAME);
		SCB_SETTINGS.add(Settings.COR_SPLT);
		SCB_SETTINGS.add(Settings.COR_SEGM);
		SCB_SETTINGS.add(Settings.COR_BEST);
		SCB_SETTINGS.add(Settings.HDR_GOAL);
		SCB_SETTINGS.add(Settings.GPH_SHOW);
		SCB_SETTINGS.add(Settings.COR_STMR);
		SCB_SETTINGS.add(Settings.COR_ICON);
		SCB_SETTINGS.add(Settings.HDR_TTLE);
	}

	private JComboBox iconSizes;

	private JComboBox timerFont;

	private JSpinner timerSize;

	private JComboBox timerSegFont;

	private JSpinner timerSegSize;

	private JCheckBox timerSameFont;

	TabComponents() {
		super();

		for (Settings.Property<Boolean> setting : SCB_SETTINGS) {
			checkBoxes.put(setting.getKey(), new SCheckBox(setting));
		}
		// Checkboxes side effects
		checkBoxes.get(Settings.FOO_SPLT.getKey()).deactivates(
				checkBoxes.get(Settings.FOO_BEST.getKey())
		);
		// Checkboxes requirements
		checkBoxes.get(Settings.FOO_BEST.getKey()).requires(
				checkBoxes.get(Settings.FOO_SPLT.getKey()), false
		);
		checkBoxes.get(Settings.FOO_LINE.getKey()).requires(
				checkBoxes.get(Settings.FOO_BEST.getKey()), true
		);

		iconSizes     = new JComboBox(Segment.ICON_SIZES);
		iconSizes.setSelectedItem(Settings.COR_ICSZ.get());
		iconSizes.addActionListener(this);

		GraphicsEnvironment gEnv = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		String mainFont = Settings.COR_TFNT.get().getName();
		timerFont = new JComboBox(gEnv.getAvailableFontFamilyNames());
		timerFont.setSelectedItem(mainFont);
		timerFont.setPreferredSize(new Dimension(130, 22));
		timerFont.addActionListener(this);

		String segFont = Settings.COR_SFNT.get().getName();
		timerSegFont = new JComboBox(gEnv.getAvailableFontFamilyNames());
		timerSegFont.setSelectedItem(segFont);
		timerSegFont.setPreferredSize(new Dimension(130, 22));
		timerSegFont.addActionListener(this);

		timerSize = new JSpinner(new SpinnerNumberModel(
				Settings.COR_TFNT.get().getSize(), 8, 240, 1)
		);
		timerSize.addChangeListener(this);

		timerSegSize = new JSpinner(new SpinnerNumberModel(
				Settings.COR_SFNT.get().getSize(), 8, 240, 1)
		);
		timerSegSize.addChangeListener(this);

		timerSameFont = new JCheckBox("" + Language.USE_MAIN_FONT);
		timerSameFont.setSelected(segFont.equals(mainFont));
		timerSegFont.setEnabled(!timerSameFont.isSelected());
		timerSameFont.addActionListener(this);

		place();
	}

	// -------------------------------------------------------------- CALLBACKS

	@Override public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source.equals(iconSizes)) {
			Settings.COR_ICSZ.set((Integer) iconSizes.getSelectedItem());
		} else if (source.equals(timerFont)) {
			String fontName = timerFont.getSelectedItem().toString();
			Font   font     = Font.decode(fontName).deriveFont(
					(float) Settings.COR_TFNT.get().getSize()
			);
			Settings.COR_TFNT.set(font);
			if (timerSameFont.isSelected()) {
				timerSegFont.setSelectedItem(fontName);
			}
		} else if (source.equals(timerSegFont)) {
			String fontName = timerSegFont.getSelectedItem().toString();
			Font   font     = Font.decode(fontName).deriveFont(
					(float) Settings.COR_SFNT.get().getSize()
			);
			Settings.COR_SFNT.set(font);
		} else if (source.equals(timerSameFont)) {
			timerSegFont.setEnabled(!timerSameFont.isSelected());
			if (timerSameFont.isSelected()) {
				int size = (Integer) timerSegSize.getValue();
				Settings.COR_SFNT.set(
						Settings.COR_TFNT.get().deriveFont((float) size)
				);
			}
		}
	}

	@Override public void stateChanged(ChangeEvent event) {
		Object source = event.getSource();
		if (source.equals(timerSize)) {
			int size = (Integer) timerSize.getValue();
			Settings.COR_TFNT.set(
					Settings.COR_TFNT.get().deriveFont((float) size)
			);
		} else if (source.equals(timerSegSize)) {
			int size = (Integer) timerSegSize.getValue();
			Settings.COR_SFNT.set(
					Settings.COR_SFNT.get().deriveFont((float) size)
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
			fontPanel.setBorder(
					BorderFactory.createTitledBorder("" + Language.PN_FONTS)
			);
		}
		JPanel timerPanel = new JPanel(new GridBagLayout()); {
			timerPanel.add(
					new JLabel("" + Language.setting_core_iconSize),
					GBC.grid(0, 1).anchor(GBC.LE).insets(5, 5)
			);
			timerPanel.add(iconSizes, GBC.grid(1, 1).insets(5, 5));
			timerPanel.add(checkBoxes.get(Settings.COR_ICON.getKey()), GBC.grid(0, 2, 2, 1).anchor(GBC.LS));
			timerPanel.add(checkBoxes.get(Settings.COR_NAME.getKey()), GBC.grid(0, 3, 2, 1).anchor(GBC.LS));
			timerPanel.add(checkBoxes.get(Settings.COR_SPLT.getKey()), GBC.grid(0, 4, 2, 1).anchor(GBC.LS));
			timerPanel.add(checkBoxes.get(Settings.COR_SEGM.getKey()), GBC.grid(0, 5, 2, 1).anchor(GBC.LS));
			timerPanel.add(checkBoxes.get(Settings.COR_BEST.getKey()), GBC.grid(0, 6, 2, 1).anchor(GBC.LS));
			timerPanel.add(checkBoxes.get(Settings.COR_STMR.getKey()), GBC.grid(0, 7, 2, 1).anchor(GBC.LS));
			timerPanel.setBorder(
					BorderFactory.createTitledBorder("" + Language.TIMER)
			);
		}
		JPanel footerPanel = new JPanel(new GridBagLayout()); {
			footerPanel.add(checkBoxes.get(Settings.FOO_SHOW.getKey()), GBC.grid(0, 0).anchor(GBC.LS));
			footerPanel.add(checkBoxes.get(Settings.FOO_SPLT.getKey()), GBC.grid(0, 1).anchor(GBC.LS));
			footerPanel.add(checkBoxes.get(Settings.FOO_VERB.getKey()), GBC.grid(0, 2).anchor(GBC.LS));
			footerPanel.add(checkBoxes.get(Settings.FOO_DLBL.getKey()), GBC.grid(1, 0).anchor(GBC.LS));
			footerPanel.add(checkBoxes.get(Settings.FOO_BEST.getKey()), GBC.grid(1, 1).anchor(GBC.LS));
			footerPanel.add(checkBoxes.get(Settings.FOO_LINE.getKey()), GBC.grid(1, 2).anchor(GBC.LS));
			footerPanel.setBorder(
					BorderFactory.createTitledBorder("" + Language.FOOTER)
			);
		}
		JPanel miscPanel = new JPanel(new GridBagLayout()); {
			miscPanel.add(checkBoxes.get(Settings.HDR_TTLE.getKey()), GBC.grid(0, 0).anchor(GBC.LS));
			miscPanel.add(checkBoxes.get(Settings.HDR_GOAL.getKey()), GBC.grid(0, 1).anchor(GBC.LS));
			miscPanel.add(checkBoxes.get(Settings.GPH_SHOW.getKey()), GBC.grid(0, 2).anchor(GBC.LS));
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
