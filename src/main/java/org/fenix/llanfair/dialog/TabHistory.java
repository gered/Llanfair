package org.fenix.llanfair.dialog;

import org.fenix.llanfair.Language;
import org.fenix.llanfair.Segment;
import org.fenix.llanfair.config.Merge;
import org.fenix.llanfair.config.Settings;
import org.fenix.utils.gui.GBC;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class TabHistory extends SettingsTab implements ActionListener, ChangeListener {

	// ------------------------------------------------------------- ATTRIBUTES

	/**
	 * Text field allowing the user to edit the height of the history. The
	 * height is provided as a number of segments to display.
	 */
	private JTextField rows;

	/**
	 * Label displaying the name of the text field {@code height}.
	 */
	private JLabel heightText;

	/**
	 * Label displaying the unit of the value of the text field {@code height}.
	 */
	private JLabel heightUnit;

	/**
	 * Combox box displaying the available merging methods, determining which,
	 * if any, column should be merged.
	 */
	private JComboBox merge;

	/**
	 * Check box determining wether or not the history should display the
	 * delta column.
	 */
	private JCheckBox deltas;

	/**
	 * Check box determining wether or not the history should display the
	 * live times column.
	 */
	private JCheckBox lives;

	private JCheckBox blankRows;

	private JCheckBox icons;

	private JComboBox iconSize;

	private JCheckBox showLast;

	private JSpinner offset;

	private JLabel offsetHelper;

	private JCheckBox twoLines;

	private JCheckBox tabular;

	private JComboBox nameFont;

	private JSpinner nameSize;

	private JComboBox timeFont;

	private JSpinner timeSize;

	// ----------------------------------------------------------- CONSTRUCTORS

	/**
	 * Creates the "History" settings tab. Only called by {@link EditSettings}.
	 */
	TabHistory() {
		rows = new JTextField();
		rows.setHorizontalAlignment(JTextField.TRAILING);
		rows.setText("" + Settings.historyRowCount.get());

		deltas = new JCheckBox("" + Language.setting_history_deltas);
		deltas.setSelected(Settings.historyDeltas.get());
		deltas.addActionListener(this);

		lives = new JCheckBox("" + Language.setting_history_liveTimes);
		lives.setSelected(Settings.historyLiveTimes.get());
		lives.addActionListener(this);

		twoLines = new JCheckBox("" + Language.setting_history_multiline);
		twoLines.setSelected(Settings.historyMultiline.get());
		twoLines.addActionListener(this);

		blankRows = new JCheckBox("" + Language.setting_history_blankRows);
		blankRows.setSelected(Settings.historyBlankRows.get());
		blankRows.addActionListener(this);

		icons = new JCheckBox("" + Language.setting_history_icons);
		icons.setSelected(Settings.historyIcons.get());
		icons.addActionListener(this);

		tabular = new JCheckBox("" + Language.setting_history_tabular);
		tabular.setSelected(Settings.historyTabular.get());
		tabular.addActionListener(this);

		showLast = new JCheckBox("" + Language.setting_history_alwaysShowLast);
		showLast.setSelected(Settings.historyAlwaysShowLast.get());
		showLast.addActionListener(this);

		merge = new JComboBox(Merge.values());
		merge.setSelectedItem(Settings.historyMerge.get());
		merge.addActionListener(this);

		iconSize = new JComboBox(Segment.ICON_SIZES);
		iconSize.setSelectedItem(Settings.historyIconSize.get());
		iconSize.addActionListener(this);

		offset = new JSpinner(new SpinnerNumberModel(
				(int) Settings.historyOffset.get(), -5, 5, 1)
		);
		offset.addChangeListener(this);

		offsetHelper = new JLabel("<html>[<a href=''>?</a>]</html>");
		offsetHelper.setToolTipText(
				"<html><div width=200>" + Language.TT_HS_OFFSET + "</div></html>"
		);

		GraphicsEnvironment gEnv = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		String mainFont = Settings.historySegmentFont.get().getName();
		nameFont = new JComboBox(gEnv.getAvailableFontFamilyNames());
		nameFont.setSelectedItem(mainFont);
		nameFont.setPreferredSize(new Dimension(130, 22));
		nameFont.addActionListener(this);

		String font = Settings.historyTimeFont.get().getName();
		timeFont = new JComboBox(gEnv.getAvailableFontFamilyNames());
		timeFont.setSelectedItem(font);
		timeFont.setPreferredSize(new Dimension(130, 22));
		timeFont.addActionListener(this);

		nameSize = new JSpinner(new SpinnerNumberModel(
				Settings.historySegmentFont.get().getSize(), 8, 240, 1)
		);
		nameSize.addChangeListener(this);

		timeSize = new JSpinner(new SpinnerNumberModel(
				Settings.historyTimeFont.get().getSize(), 8, 240, 1)
		);
		timeSize.addChangeListener(this);

		place();
	}

	// -------------------------------------------------------------- CALLBACKS

	/**
	 * Update the settings with the user input when he validates.
	 */
	@Override public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source.equals(merge)) {
			Merge value = (Merge) merge.getSelectedItem();
			Settings.historyMerge.set(value);
			if (value == Merge.DELTA) {
				Settings.historyDeltas.set(false);
				deltas.setSelected(false);
			} else if (value == Merge.LIVE) {
				Settings.historyLiveTimes.set(false);
				lives.setSelected(false);
			}
		} else if (source.equals(deltas)) {
			Settings.historyDeltas.set(deltas.isSelected());
		} else if (source.equals(lives)) {
			Settings.historyLiveTimes.set(lives.isSelected());
		} else if (source.equals(blankRows)) {
			Settings.historyBlankRows.set(blankRows.isSelected());
		} else if (source.equals(icons)) {
			Settings.historyIcons.set(icons.isSelected());
		} else if (source.equals(showLast)) {
			Settings.historyAlwaysShowLast.set(showLast.isSelected());
		} else if (source.equals(twoLines)) {
			Settings.historyMultiline.set(twoLines.isSelected());
		} else if (source.equals(tabular)) {
			Settings.historyTabular.set(tabular.isSelected());
		} else if (source.equals(iconSize)) {
			Settings.historyIconSize.set(
					(Integer) iconSize.getSelectedItem()
			);
		} else if (source.equals(nameFont)) {
			String fontName = nameFont.getSelectedItem().toString();
			Font   font     = Font.decode(fontName).deriveFont(
					(float) Settings.historySegmentFont.get().getSize()
			);
			Settings.historySegmentFont.set(font);
		} else if (source.equals(timeFont)) {
			String fontName = timeFont.getSelectedItem().toString();
			Font   font     = Font.decode(fontName).deriveFont(
					(float) Settings.historyTimeFont.get().getSize()
			);
			Settings.historyTimeFont.set(font);
		}
	}

	public void stateChanged(ChangeEvent event) {
		Object source = event.getSource();
		if (source.equals(offset)) {
			int size = (Integer) offset.getValue();
			Settings.historyOffset.set(size);
		} else if (source.equals(nameSize)) {
			int size = (Integer) nameSize.getValue();
			Settings.historySegmentFont.set(
					Settings.historySegmentFont.get().deriveFont((float) size)
			);
		} else if (source.equals(timeSize)) {
			int size = (Integer) timeSize.getValue();
			Settings.historyTimeFont.set(
					Settings.historyTimeFont.get().deriveFont((float) size)
			);
		}
	}

	// -------------------------------------------------------------- INHERITED

	@Override void doDelayedSettingChange() {
		int input = 0;
		try {
			input = Integer.parseInt(rows.getText());
		} catch (Exception e) {
			//$FALL-THROUGH$
		} finally {
			if (input <= 0) {
				input = 0;
				rows.setText("0");
			}
			Settings.historyRowCount.set(input);
		}
	}

	/**
	 * Returns the localized name of this tab.
	 */
	@Override public String toString() {
		return "" + Language.HISTORY;
	}

	// -------------------------------------------------------------- UTILITIES

	/**
	 * Places all sub-components within this panel.
	 */
	private void place() {
		setLayout(new GridBagLayout());
		// Set Components Orientation
		rows.setHorizontalAlignment(JLabel.CENTER);
		twoLines.setHorizontalTextPosition(JLabel.LEADING);
		blankRows.setHorizontalTextPosition(JLabel.LEADING);
		showLast.setHorizontalTextPosition(JLabel.LEADING);
		// Display
		JPanel display = new JPanel(new GridBagLayout()); {
			display.add(icons , GBC.grid(0, 0).anchor(GBC.LS));
			display.add(lives , GBC.grid(0, 1).anchor(GBC.LS));
			display.add(deltas, GBC.grid(0, 2).anchor(GBC.LS));
//            display.add(tabular, GBC.grid(0, 3).anchor(GBC.LS));
			display.add(merge , GBC.grid(0, 4).anchor(GBC.LS));
			display.setBorder(
					BorderFactory.createTitledBorder("" + Language.PN_DISPLAY)
			);
		}
		// Dimension
		JPanel dimension = new JPanel(new GridBagLayout()); {
			dimension.add(
					new JLabel("" + Language.setting_history_iconSize),
					GBC.grid(0, 0).anchor(GBC.LE)
			);
			dimension.add(iconSize, GBC.grid(1, 0).anchor(GBC.LE).insets(2, 3));
			dimension.add(
					new JLabel("" + Language.setting_history_rowCount),
					GBC.grid(0, 1).anchor(GBC.LE)
			);
			dimension.add(
					rows, GBC.grid(1, 1).anchor(GBC.LE).fill(GBC.H).insets(2, 3)
			);
			dimension.add(blankRows, GBC.grid(0, 2, 2, 1).anchor(GBC.LE));
			dimension.add(
					twoLines , GBC.grid(0, 3, 2, 1).anchor(GBC.LE).insets(0, 1)
			);
			dimension.setBorder(
					BorderFactory.createTitledBorder("" + Language.PN_DIMENSION)
			);
		}
		// Fonts
		JPanel fonts = new JPanel(new GridBagLayout()); {
			fonts.add(
					new JLabel("" + Language.setting_history_segmentFont),
					GBC.grid(0, 0).anchor(GBC.LE)
			);
			fonts.add(nameFont, GBC.grid(1, 0).anchor(GBC.LS).insets(5, 5));
			fonts.add(nameSize, GBC.grid(2, 0).anchor(GBC.LS));
			fonts.add(
					new JLabel("" + Language.setting_history_timeFont),
					GBC.grid(0, 1).anchor(GBC.LE)
			);
			fonts.add(timeFont, GBC.grid(1, 1).anchor(GBC.LS).insets(5, 5));
			fonts.add(timeSize, GBC.grid(2, 1).anchor(GBC.LS));
			fonts.setBorder(
					BorderFactory.createTitledBorder("" + Language.PN_FONTS)
			);
		}
		// Scrolling
		JPanel scrolling = new JPanel(new GridBagLayout()); {
			scrolling.add(
					offsetHelper, GBC.grid(0, 0).anchor(GBC.LE).insets(0, 8)
			);
			scrolling.add(
					new JLabel("" + Language.setting_history_offset),
					GBC.grid(1, 0).anchor(GBC.LE)
			);
			scrolling.add(offset  , GBC.grid(2, 0).anchor(GBC.LE));
			scrolling.add(showLast, GBC.grid(0, 1, 3, 1).anchor(GBC.LE));
			scrolling.setBorder(
					BorderFactory.createTitledBorder("" + Language.PN_SCROLLING)
			);
		}
		add(fonts    , GBC.grid(0, 0).fill(GBC.B).padding(10, 10));
		add(dimension, GBC.grid(1, 0).fill(GBC.B).padding(10, 10));
		add(display  , GBC.grid(0, 1).fill(GBC.B).padding(10, 10));
		add(scrolling, GBC.grid(1, 1).fill(GBC.B).padding(10, 10));
	}

}
