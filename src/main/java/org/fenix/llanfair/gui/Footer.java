package org.fenix.llanfair.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fenix.llanfair.Language;
import org.fenix.llanfair.Run;
import org.fenix.llanfair.Segment;
import org.fenix.llanfair.config.Settings;
import org.fenix.llanfair.Time;
import org.fenix.llanfair.Run.State;
import org.fenix.utils.gui.GBC;
import org.fenix.utils.locale.LocaleEvent;

/**
 * A simple pane displaying the bare minimum of information concerning the
 * previous segment of a run, namely the cumulative gain/loss on the run
 * (the delta between the previous segment's live and registered split times)
 * and the segment time of the previous segment.
 *
 * @author  Xavier "Xunkar" Sencert
 */
class Footer extends JPanel {

	private static final int ALL = 0xff;
	private static final int TIME = 0x01;
	private static final int DELTA = 0x02;
	private static final int TEXT = 0x04;
	private static final int BEST = 0x08;
	private static final int VERBOSE = 0x10;

	private static final int INSET = 3;


	private Run run;
	private Time tmDlta;

	private JLabel labelPrev;   // P.Se:
	private JLabel liveL;       // Left-hand Live Time
	private JLabel liveR;       // Right-hand Live Time
	private JLabel time;        // Segment Set Time

	private JLabel labelDelta;  // Delta:
	private JLabel labelLive;   // Live:
	private JLabel delta;       // Delta Segment Set/Live Time

	private JLabel labelBest;   // P.Be:
	private JLabel best;        // Segment Best Time
	private JLabel inlineBest;  // Segment Best Time (inline)

	private JLabel labelDeltaBest;  // B.Delta:
	private JLabel deltaBest;       // Delta Segment Best/Live Time
	private JLabel inlineDeltaBest; // Delta Segment Best/Live Time (inline)

	private JPanel panelBest;       // labelBest + best
	private JPanel panelDeltaBest;  // labelDeltaBest + deltaBest

	private boolean resize;
	private Dimension preferredSize;

	/**
	 * Creates a default panel displaying informations for the given run.
	 *
	 * @param   run - the run to represent.
	 */
	Footer(Run run) {
		time = new JLabel();
		liveL = new JLabel();
		liveR = new JLabel();
		delta = new JLabel();
		best = new JLabel();
		deltaBest = new JLabel();
		inlineBest = new JLabel();
		inlineDeltaBest = new JLabel();

		labelLive = new JLabel();
		labelPrev = new JLabel();
		labelBest = new JLabel();
		labelDelta = new JLabel();
		labelDeltaBest = new JLabel();

		preferredSize = null;
		resize = false;

		setRun(run);
		setOpaque(false);

		placeComponents();
		updateValues(TEXT);
		updateColors(ALL);
		updateVisibility(ALL);
		forceResize();
	}

	@Override public Dimension getPreferredSize() {
		Graphics graphics = getGraphics();
		if (resize && (graphics != null)) {
			FontMetrics metrics = graphics.getFontMetrics();

			int timeW;
			int timeH = metrics.getHeight();
			int smtmW;
			if (run.getRowCount() > 0) {
				Time segmentTime = run.getSegment(0).getTime(Segment.RUN);
				Time tenthTime = new Time(segmentTime.getMilliseconds() / 10L);
				timeW = metrics.stringWidth("" + segmentTime);
				smtmW = metrics.stringWidth("" + tenthTime);
			} else {
				timeW = metrics.stringWidth("" + Time.ZERO);
				smtmW = timeW;
			}
 
			int liveW = metrics.stringWidth("" + Language.LB_FT_LIVE);
			int prevW = metrics.stringWidth("" + Language.LB_FT_SEGMENT);
			int bestW = metrics.stringWidth("" + Language.LB_FT_BEST);
			int dltaW = metrics.stringWidth("" + Language.LB_FT_DELTA);
			int dltbW = metrics.stringWidth("" + Language.LB_FT_DELTA_BEST);

			boolean ftBest = Settings.footerShowBestTime.get();
			boolean ftLabels = Settings.footerShowDeltaLabels.get();
			boolean ftVerbose = Settings.footerVerbose.get();
			boolean ftTwoLines = Settings.footerMultiline.get();

			int height = timeH;
			int width  = prevW + timeW + smtmW + INSET * 2;
			if (ftLabels) {
				width += dltaW;
			}
			if (ftVerbose) {
				width += timeW + liveW - (ftLabels ? 0 : dltaW)
						+ metrics.stringWidth(" []");
			}
			if (ftBest) {
				if (ftTwoLines) {
					height *= 2;
					int breakW = bestW + timeW + smtmW + (ftLabels ? dltbW : 0);
					width = Math.max(width, breakW);
				} else {
					width += timeW + smtmW + metrics.stringWidth("| ");
				}
				if (ftVerbose) {
					width += 5;
				}
			}
			preferredSize = new Dimension(width, height);
			setMinimumSize(new Dimension(50, height));
			resize = false;
		}
		return (preferredSize == null ? getMinimumSize() : preferredSize);
	}



	/**
	 * Sets the run to represent. All components are resetted to their initial
	 * state.
	 *
	 * @param   run - the new run to represent.
	 */
	final void setRun(Run run) {
		this.run = run;
		updateValues(ALL & ~TEXT);
	}

	// -------------------------------------------------------------- CALLBACKS

	/**
	 * Callback invoked by the parent when the run or the application's
	 * settings have seen one of their properties updated.
	 *
	 * @param   event   - the event describing the update.
	 */
	void processPropertyChangeEvent(PropertyChangeEvent event) {
		String property = event.getPropertyName();

		if (Run.CURRENT_SEGMENT_PROPERTY.equals(property)) {
			updateValues(ALL & ~TEXT);
			updateColors(TIME | DELTA);
			updateVisibility(ALL);
		} else if (Settings.colorTimeLost.equals(property)
				|| Settings.colorTimeGained.equals(property)) {
			updateColors(DELTA);

		} else if (Settings.colorTime.equals(property)
				|| Settings.colorNewRecord.equals(property)) {
			updateColors(TIME | DELTA);

		} else if (Settings.colorForeground.equals(property)) {
			updateColors(TEXT);

		} else if (Settings.accuracy.equals(property)
				|| Settings.compareMethod.equals(property)) {
			updateValues(ALL & ~TEXT);
			forceResize();

		} else if (Run.STATE_PROPERTY.equals(property)) {
			if (run.getState() == State.NULL || run.getState() == State.READY) {
				updateValues(ALL & ~TEXT);
			}
			updateVisibility(ALL);
		} else if (Settings.footerUseSplitData.equals(property)) {
			updateValues(ALL);
		} else if (Settings.footerShowBestTime.equals(property)
				|| Settings.footerMultiline.equals(property)) {

			updateVisibility(BEST);
			forceResize();
		} else if (Settings.footerShowDeltaLabels.equals(property)) {
			updateVisibility(TEXT);
			forceResize();
		} else if (Settings.footerVerbose.equals(property)) {
			updateValues(DELTA);
			updateVisibility(VERBOSE);
			forceResize();
		}
	}

	private void forceResize() {
		resize = true;
		revalidate();
	}

	/**
	 * Callback invoked by the parent when default local for this instance of
	 * the JVM has changed.
	 *
	 * @param   event   - the event describing the update.
	 */
	void processLocaleEvent(LocaleEvent event) {
		updateValues(TEXT);
	}

	// -------------------------------------------------------------- UTILITIES

	/**
	 * Places the sub-components within this component.
	 */
	private void placeComponents() {
		setLayout(new GridBagLayout());

		JPanel timePanel = new JPanel(new GridBagLayout()); {
			timePanel.add(
					labelPrev,
					GBC.grid(0, 0).anchor(GBC.LS).insets(0, 0, 0, INSET)
			);
			timePanel.add(liveL, GBC.grid(1, 0).anchor(GBC.LS));
			timePanel.add(time, GBC.grid(2, 0).anchor(GBC.LS));
			timePanel.add(
					inlineBest,
					GBC.grid(3, 0).anchor(GBC.LS).insets(0, INSET, 0, 0)
			);
			timePanel.setOpaque(false);
		}
		JPanel deltaPanel = new JPanel(new GridBagLayout()); {
			deltaPanel.add(
					labelDelta,
					GBC.grid(0, 0).anchor(GBC.LE).insets(0, 0, 0, INSET)
			);
			deltaPanel.add(
					labelLive,
					GBC.grid(1, 0).anchor(GBC.LE).insets(0, 0, 0, INSET)
			);
			deltaPanel.add(
					liveR, GBC.grid(2, 0).anchor(GBC.LE).insets(0, 0, 0, INSET)
			);
			deltaPanel.add(delta, GBC.grid(3, 0).anchor(GBC.LE));
			deltaPanel.add(
					inlineDeltaBest,
					GBC.grid(4, 0).anchor(GBC.LE).insets(0, INSET, 0, 0)
			);
			deltaPanel.setOpaque(false);
		}
		panelBest = new JPanel(new GridBagLayout()); {
			panelBest.add(
					labelBest,
					GBC.grid(0, 0).anchor(GBC.LS).insets(0, 0, 0, INSET)
			);
			panelBest.add(best, GBC.grid(1, 0).anchor(GBC.LS));
			panelBest.setOpaque(false);
		}
		panelDeltaBest = new JPanel(new GridBagLayout()); {
			panelDeltaBest.add(
					labelDeltaBest,
					GBC.grid(0, 0).anchor(GBC.LE).insets(0, 0, 0, INSET)
			);
			panelDeltaBest.add(deltaBest, GBC.grid(1, 0).anchor(GBC.LE));
			panelDeltaBest.setOpaque(false);
		}
		add(timePanel, GBC.grid(0, 0).anchor(GBC.LS).weight(0.5, 0.0));
		add(deltaPanel, GBC.grid(1, 0).anchor(GBC.LE).weight(0.5, 0.0));
		add(panelBest, GBC.grid(0, 1).anchor(GBC.LS).weight(0.5, 0.0));
		add(panelDeltaBest, GBC.grid(1, 1).anchor(GBC.LE).weight(0.5, 0.0));
	}

	private void updateVisibility(int identifier) {
		if ((identifier & BEST) == BEST) {
			boolean ftTwoLines = Settings.footerMultiline.get();
			boolean ftBest = Settings.footerShowBestTime.get();
			panelBest.setVisible(ftTwoLines);
			panelDeltaBest.setVisible(ftTwoLines);
			inlineBest.setVisible(!ftTwoLines && ftBest);
			inlineDeltaBest.setVisible(!ftTwoLines && ftBest);
		}
		if ((identifier & TEXT) == TEXT) {
			boolean ftLabels = Settings.footerShowDeltaLabels.get();
			boolean ftVerbose = Settings.footerVerbose.get();
			labelLive.setVisible(ftLabels && ftVerbose);
			labelDelta.setVisible(ftLabels && !ftVerbose);
			labelDeltaBest.setVisible(ftLabels);
		}
		if ((identifier & VERBOSE) == VERBOSE) {
			boolean ftVerbose = Settings.footerVerbose.get();
			boolean ftLabels = Settings.footerShowDeltaLabels.get();
			labelLive.setVisible(ftVerbose && ftLabels);
			labelDelta.setVisible(!ftVerbose && ftLabels);
			time.setVisible(ftVerbose);
			liveL.setVisible(!ftVerbose);
			liveR.setVisible(ftVerbose);
		}
	}

	/**
	 * Updates the colors of the group of components specified by the
	 * identifier.
	 *
	 * @param   identifier  - one of the constant update identifier.
	 */
	private void updateColors(int identifier) {
		if ((identifier & TIME) == TIME) {
			Color colorTM = Settings.colorTime.get();
			Color colorNR = Settings.colorNewRecord.get();
			if (run.hasPreviousSegment() && run.isBestSegment(run.getPrevious())) {
				liveL.setForeground(colorNR);
				liveR.setForeground(colorNR);
			} else {
				liveL.setForeground(colorTM);
				liveR.setForeground(colorTM);
			}
			time.setForeground(colorTM);
			best.setForeground(colorTM);
			inlineBest.setForeground(colorTM);
		}
		if ((identifier & DELTA) == DELTA) {
			if (run.hasPreviousSegment()) {
				Color colorTM = Settings.colorTime.get();
				deltaBest.setForeground(colorTM);
				inlineDeltaBest.setForeground(colorTM);
				if (delta.getText().equals("--")) {
					delta.setForeground(colorTM);
				} else if (run.isBestSegment(run.getPrevious())){
					Color colorNR = Settings.colorNewRecord.get();
					delta.setForeground(colorNR);
					deltaBest.setForeground(colorNR);
					inlineDeltaBest.setForeground(colorNR);
				} else {
					int compare = tmDlta.compareTo(Time.ZERO);
					if (compare > 0) {
						delta.setForeground(Settings.colorTimeLost.get());
					} else {
						delta.setForeground(Settings.colorTimeGained.get());
					}
				}
			}
		}
		if ((identifier & TEXT) == TEXT) {
			Color color = Settings.colorForeground.get();
			labelPrev.setForeground(color);
			labelDelta.setForeground(color);
			labelLive.setForeground(color);
			labelBest.setForeground(color);
			labelDeltaBest.setForeground(color);
		}
	}

	/**
	 * Updates the values of the group of components specified by the
	 * identifier.
	 *
	 * @param   identifier  - one of the constant update identifier.
	 */
	private void updateValues(int identifier) {
		boolean useSplit    = Settings.footerUseSplitData.get();
		boolean hasPrevious = run.hasPreviousSegment();
		int     pIndex      = run.getPrevious();
		Segment pSegment    = null;
		Time    live;
  
		if (hasPrevious) {
			pSegment = run.getSegment(pIndex);
		}
		if ((identifier & TIME) == TIME) {
			Time    set;
			if (hasPrevious) {
				if (useSplit) {
					live  = run.getTime(pIndex, Segment.LIVE);
					set   = run.getTime(pIndex, Segment.SET);
				} else {
					live  = pSegment.getTime(Segment.LIVE);
					set   = pSegment.getTime(Segment.SET);
				}
				time.setText("" + (set == null ? "--" : set));
				liveL.setText("" + (live == null ? "--" : live));
				liveR.setText(liveL.getText());
				Time bTime = pSegment.getTime(Segment.BEST);
				inlineBest.setText("| " + (bTime == null ? "--" : bTime));
				best.setText("" + (bTime == null ? "--" : bTime));
			} else {
				time.setText("");
				liveL.setText("");
				liveR.setText("");
				best.setText("");
				inlineBest.setText("");
			}
		}
		if ((identifier & DELTA) == DELTA) {
			if (hasPrevious) {
				if (useSplit) {
					tmDlta = run.getTime(pIndex, Segment.DELTA);
					live      = run.getTime(pIndex, Segment.LIVE);

					if (tmDlta == null || live == null) {
						delta.setText("--");
					} else {
						delta.setText(tmDlta.toString(true));
					}
				} else {
					tmDlta = pSegment.getTime(Segment.DELTA);
					live      = pSegment.getTime(Segment.LIVE);
					Time set  = pSegment.getTime(Segment.SET);

					Time dBst = pSegment.getTime(Segment.DELTA_BEST);
					inlineDeltaBest.setText("| " + (dBst == null ? "--" : dBst.toString(true)));
					deltaBest.setText("" + (dBst == null ? "--" : dBst.toString(true)));

					if (set != null && pIndex > 1) {
						set = set.clone();
						for (int i = pIndex - 1; i >= 0; i--) {
							Segment pSeg = run.getSegment(i);
							Time    ante = pSeg.getTime(Segment.LIVE);
							if (ante == null) {
								set.add(pSeg.getTime(Segment.SET));
							} else {
								break;
							}
						}
						tmDlta = Time.getDelta(live, set);
					}
					if (tmDlta == null || live == null) {
						delta.setText("--");
						inlineDeltaBest.setText("| --");
						deltaBest.setText("--");
					} else {
						delta.setText(tmDlta.toString(true));
					}
					if (pIndex > 0) {
						Time sTime = run.getSegment(pIndex - 1)
														.getTime(Segment.SET);
						if (sTime == null) {
							delta.setText("--");
						}
					}
				}
				if (Settings.footerVerbose.get()) {
					delta.setText("[" + delta.getText() + "]");
				}
				updateColors(DELTA);
			} else {
				delta.setText("");
				inlineDeltaBest.setText("");
				deltaBest.setText("");
			}
		}
		if ((identifier & TEXT) == TEXT) {
			if (useSplit) {
				labelPrev.setText("" + Language.LB_FT_SPLIT);
			} else {
				labelPrev.setText("" + Language.LB_FT_SEGMENT);
			}
			labelLive.setText("" + Language.LB_FT_LIVE);
			labelBest.setText("" + Language.LB_FT_BEST);
			labelDelta.setText("" + Language.LB_FT_DELTA);
			labelDeltaBest.setText("" + Language.LB_FT_DELTA_BEST);
		}
	}
}
