package org.fenix.llanfair.gui;

import org.fenix.llanfair.Language;
import org.fenix.llanfair.Run;
import org.fenix.llanfair.Run.State;
import org.fenix.llanfair.Segment;
import org.fenix.llanfair.Time;
import org.fenix.llanfair.config.Settings;
import org.fenix.utils.Images;
import org.fenix.utils.gui.GBC;
import org.fenix.utils.locale.LocaleEvent;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

/**
 * Core panel displaying the main informations for a run, namely: the run and 
 * segment timers and the current segment being runned.  
 *
 * @author  Xavier "Xunkar" Sencert
 */
class Core extends JPanel implements ActionListener {

	// -------------------------------------------------------------- CONSTANTS

	/**
	 * Update identifier for every category.
	 */
	private static final int ALL = 0xff;

	/**
	 * Update identifier for time variables.
	 */
	private static final int TIME = 0x01;

	/**
	 * Update identifier for timer variables.
	 */
	private static final int TIMER = 0x02;

	/**
	 * Update identifier for icon variables.
	 */
	private static final int ICON = 0x04;

	/**
	 * Update identifier for name variables.
	 */
	private static final int NAME = 0x08;

	private static final int FONT = 0x10;

	/**
	 * Minimum width in pixels of this component.
	 */
	private static final int MIN_WIDTH = 50;

	/**
	 * Dummy object used so we can get a string format for zero time instead of new'ing up
	 * a Time object each time.
	 */
	private static final Time zeroTime = new Time(0);

	// ------------------------------------------------------------- ATTRIBUTES

	/**
	 * Run instance represented by the panel.
	 */
	private Run run;

	/**
	 * Thread updating the value of the timers and repainting them every
	 * hundredth of a second.
	 */
	private Timer timer;

	/**
	 * Label displaying the main timer, timing the whole run.
	 */
	private JLabel splitTimer;

	/**
	 * Label displaying the segment timer, timing the current segment.
	 */
	private JLabel segmentTimer;

	/**
	 * Label displaying the name of the current segment.
	 */
	private JLabel name;

	/**
	 * Label displayong the icon (if any) of the current segment.
	 */
	private JLabel icon;

	/**
	 * Label displaying the registered split time of the current segment.
	 */
	private JLabel split;

	/**
	 * Label displaying the registered segment time of the current segment.
	 */
	private JLabel segment;

	private JLabel best;

	/**
	 * Registered split time of the current segment.
	 */
	private Time splitTime;

	/**
	 * Registered segment time of the current segment.
	 */
	private Time segmentTime;

	/**
	 * Time when the run was paused, if it was.
	 */
	private Time pauseTime;

	/**
	 * Flag indicating wether or not the split time for the current segment
	 * has been reached, meaning that we are now loosing time on the split.
	 */
	private volatile boolean splitLoss;

	/**
	 * Flag indicating wether or not the segment time for the current segment
	 * has been reached, meaning that we are now loosing time on the segment.
	 */
	private volatile boolean segmentLoss;

	private long blinkTime;

	/**
	 * The ideal display size of this component.
	 */
	private Dimension preferredSize;

	/**
	 * Wether or not the component should recompute its ideal size.
	 */
	private boolean resize;

	private JLabel labelSplit;
	private JLabel labelSegment;
	private JLabel labelBest;

	// ----------------------------------------------------------- CONSTRUCTORS

	/**
	 * Creates a default panel displaying information for the given run.
	 *
	 * @param   run - the run to represent.
	 */
	Core(Run run) {
		timer         = new Timer(10, this);
		splitTimer    = new JLabel();
		segmentTimer  = new JLabel();
		name          = new JLabel();
		icon          = new JLabel();
		split         = new JLabel();
		segment       = new JLabel();
		best          = new JLabel();
		labelSplit    = new JLabel("" + Language.LB_CR_SPLIT);
		labelSegment  = new JLabel("" + Language.LB_CR_SEGMENT);
		labelBest     = new JLabel("" + Language.LB_CR_BEST);
		blinkTime     = 0L;
		preferredSize = null;
		resize        = false;

		setRun(run);
		setOpaque(false);
		setDoubleBuffered(true);

		placeComponents();
		updateColors(ALL);
		updateFonts(ALL);
	}

	// -------------------------------------------------------------- INTERFACE

	/**
	 * Sets the run to represent. All components are resetted to their initial
	 * state.
	 *
	 * @param   run - the new run to represent.
	 */
	final void setRun(Run run) {
		this.run = run;
		updateValues(ALL);
		updateVisibility(ALL);
		updateColors(ALL);
		forceResize();
	}

	/**
	 * Make sure to stop the updating thread when this component is being
	 * diposed of.
	 */
	@Override protected void finalize() throws Throwable {
		timer.stop();
		super.finalize();
	 }

	/**
	 * Returns the preferred size of this component. This method is heap-cheap
	 * as it recomputes the preferred size only when necessary.
	 */
	@Override public Dimension getPreferredSize() {
		if (resize) {
			Graphics graphics = getGraphics();
			if (graphics != null) {
				Time tmFake = new Time(600000L);
				Time tmRun  = run.getTime(Segment.SET);

				FontMetrics coreFontMetric = graphics.getFontMetrics(Settings.coreFont.get());
				FontMetrics coreOtherTimeFontMetric = graphics.getFontMetrics(Settings.coreOtherTimeFont.get());
				FontMetrics coreTimerFontMetric = graphics.getFontMetrics(Settings.coreTimerFont.get());
				FontMetrics coreSegmentTimerFontMetric = graphics.getFontMetrics(Settings.coreSegmentTimerFont.get());

				// Segment Name
				int         wName  = 0;
				int         hName  = 0;
				if (Settings.coreShowSegmentName.get()) {
					for (int i = 0; i < run.getRowCount(); i++) {
						String sName = run.getSegment(i).getName();
						wName = Math.max(wName, coreFontMetric.stringWidth(sName));
					}
					hName = coreFontMetric.getHeight();
				}
				// Segment Times
				int wTime = 0;
				int hTime = 0;
				int hBuff = coreOtherTimeFontMetric.getHeight();
				int wBuff = coreOtherTimeFontMetric.stringWidth(
						"" + (tmRun == null ? tmFake : tmRun)
				);
				wBuff += coreOtherTimeFontMetric.stringWidth("XX:");

				if (Settings.coreShowBestTime.get()) {
					hTime = hTime + hBuff;
					wTime = wBuff;
				}
				if (Settings.coreShowSegmentTime.get()) {
					hTime = hTime + hBuff;
					wTime = wBuff;
				}
				if (Settings.coreShowSplitTime.get()) {
					hTime = hTime + hBuff;
					wTime = wBuff;
				}
				// Segment Icon
				int hIcon = 0;
				int wIcon = 0;
				// TODO hasIcon ?
				if (Settings.coreShowIcons.get() || run.getMaxIconHeight() != 0) {
					hIcon = Settings.coreIconSize.get();
					wIcon = hIcon;
				}
				// Run Timer
				int wSpTimer = coreTimerFontMetric.stringWidth(
						"" + (tmRun == null ? tmFake : tmRun)
				);
				int hSpTimer = coreTimerFontMetric.getHeight();

				// Segment Timer
				int wSeTimer = 0;
				int hSeTimer = 0;
				if (Settings.coreShowSegmentTimer.get()) {
					wSeTimer = coreSegmentTimerFontMetric.stringWidth(
							"" + (tmRun == null ? tmFake : tmRun)
					);
					hSeTimer = coreSegmentTimerFontMetric.getHeight();
				}

				int maxHeight      = Math.max(hIcon, hSpTimer + hSeTimer);
					maxHeight      = Math.max(maxHeight, hTime + hName);

				int maxWidth = wIcon + Math.max(wName, wTime)
						+ Math.max(wSpTimer, wSeTimer) + 5;

				preferredSize = new Dimension(maxWidth, maxHeight);
				setMinimumSize(new Dimension(MIN_WIDTH, maxHeight));
			}
			resize = false;
		}
		return (preferredSize == null ? getMinimumSize() : preferredSize);
	}

	// -------------------------------------------------------------- CALLBACKS

	/**
	 * Callback invoked by the updater thread {@code timer}. Every hundredth
	 * of a second we update the values of the timers and change their color
	 * if we’ve reached a loss of time.
	 */
	@Override public synchronized void actionPerformed(ActionEvent event) {
		long now            = System.nanoTime() / 1000000L;
		Segment current     = run.getSegment(run.getCurrent());
		Time splitElapsed   = new Time(now - run.getStartTime());
		Time segmentElapsed = new Time(now - current.getStartTime());

		if (splitElapsed.getMilliseconds() < 0)
			splitTimer.setForeground(Settings.colorNegativeTime.get());
		else
			splitTimer.setForeground(Settings.colorTimer.get());

		if (run.getState().equals(State.PAUSED)) {
			splitTimer.setText("" + pauseTime);
			if (blinkTime == 0L || now - blinkTime >= 400L) {
				Color bg = Settings.colorBackground.get();
				if (splitTimer.getForeground().equals(bg)) {
					if (pauseTime.compareTo(splitTime) > 0) {
						splitTimer.setForeground(Settings.colorTimeLostWhileBehind.get());
					} else {
						splitTimer.setForeground(Settings.colorTimer.get());
					}
				} else {
					splitTimer.setForeground(bg);
				}
				blinkTime = now;
			}
		} else {
			if (splitElapsed.getMilliseconds() < 0) {
				splitTimer.setText("-" + splitElapsed);
				segmentTimer.setText("" + zeroTime);
			} else {
				splitTimer.setText("" + splitElapsed);
				segmentTimer.setText("" + segmentElapsed);
			}

			if (!splitLoss && splitElapsed.compareTo(splitTime) > 0) {
				splitLoss = true;
				splitTimer.setForeground(Settings.colorTimeLostWhileBehind.get());
			}
			if (!segmentLoss && segmentElapsed.compareTo(segmentTime) > 0) {
				segmentLoss = true;
				segmentTimer.setForeground(Settings.colorTimeLostWhileBehind.get());
			}
		}
	}

	/**
	 * Callback invoked by the parent when the run or the application's
	 * settings have seen one of their properties updated.
	 *
	 * @param   event   - the event describing the update.
	 */
	void processPropertyChangeEvent(PropertyChangeEvent event) {
		String property = event.getPropertyName();
		if (Run.STATE_PROPERTY.equals(property)) {
			updateValues(ALL);
			updateVisibility(TIME);
			if (run.getState().equals(State.PAUSED)) {
				long now  = System.nanoTime() / 1000000L;
				pauseTime = new Time(now - run.getStartTime());
			} else {
				updateColors(TIMER);
			}
		} else if (Run.CURRENT_SEGMENT_PROPERTY.equals(property)) {
			updateValues(ALL & ~TIMER);
			updateColors(TIMER);
		} else if (Run.DELAYED_START_PROPERTY.equals(property)) {
			updateValues(TIMER);
			updateColors(TIMER);
		} else if (Settings.colorForeground.equals(property)) {
			updateColors(NAME);
		} else if (Settings.colorTime.equals(property)) {
			updateColors(TIME);
		} else if (Settings.colorTimer.equals(property)) {
			updateColors(TIMER);
		} else if (Settings.compareMethod.equals(property)) {
			updateValues(TIME);
			updateColors(TIMER);
			forceResize();
		} else if (Settings.accuracy.equals(property)) {
			updateValues(TIME | TIMER);
			forceResize();
		} else if (Settings.coreShowBestTime.equals(property)
				|| Settings.coreShowSegmentTime.equals(property)
				|| Settings.coreShowSplitTime.equals(property)) {
			updateVisibility(TIME);
			forceResize();
		} else if (Settings.coreShowSegmentName.equals(property)) {
			updateVisibility(NAME);
			forceResize();
		} else if (Settings.coreIconSize.equals(property)) {
			forceResize();
		} else if (Settings.coreTimerFont.equals(property)
				|| Settings.coreSegmentTimerFont.equals(property)) {
			updateFonts(TIMER);
			forceResize();
		} else if (Settings.coreShowSegmentTimer.equals(property)) {
			updateVisibility(TIMER);
			forceResize();
		} else if (Settings.coreShowIcons.equals(property)) {
			updateVisibility(ICON);
			forceResize();
		} else if (Settings.coreFont.equals(property)) {
			updateFonts(NAME);
			forceResize();
		} else if (Settings.coreOtherTimeFont.equals(property)) {
			updateFonts(TIME);
			forceResize();
		} else if (Settings.windowAutoSize.equals(property) || Settings.windowWidth.equals(property)) {
			updateSize();
			forceResize();
		}
	}

	private void forceResize() {
		resize = true;
		revalidate();
	}
	/**
	 * Callback invoked by the parent when the run table of segments is
	 * updated.
	 *
	 * @param   event   - the event describing the update.
	 */
	void processTableModelEvent(TableModelEvent event) {
		resize = true;
	}

	/**
	 * Callback invoked by the parent when default local for this instance of
	 * the JVM has changed.
	 *
	 * @param   event   - the event describing the update.
	 */
	public void processLocaleEvent(LocaleEvent event) {
		updateValues(TIMER);
	}

	// -------------------------------------------------------------- UTILITIES

	/**
	 * Places the sub-components within this component.
	 */
	private void placeComponents() {
		setLayout(new GridBagLayout());
		JPanel infoPanel = new JPanel(new GridBagLayout()); {
			infoPanel.add(
					name, GBC.grid(0, 0, 2, 1).anchor(GBC.LS).weight(1.0, 0.0)
			);
			infoPanel.add(
					labelSplit, GBC.grid(0, 1).anchor(GBC.LS).insets(0, 0, 0, 3)
			);
			infoPanel.add(
					split, GBC.grid(1, 1).anchor(GBC.LS).weight(1.0, 0.0)
			);
			infoPanel.add(
					labelSegment,
					GBC.grid(0, 2).anchor(GBC.LS).insets(0, 0, 0, 3)
			);
			infoPanel.add(
					segment, GBC.grid(1, 2).anchor(GBC.LS).weight(1.0, 0.0)
			);
			infoPanel.add(
					labelBest, GBC.grid(0, 3).anchor(GBC.LS).insets(0, 0, 0, 3)
			);
			infoPanel.add(best, GBC.grid(1, 3).anchor(GBC.LS).weight(1.0, 0.0));
			infoPanel.setOpaque(false);
		}
		JPanel timePanel = new JPanel(new GridBagLayout()); {
			timePanel.add(splitTimer, GBC.grid(0, 0).anchor(GBC.LE));
			timePanel.add(segmentTimer, GBC.grid(0, 1).anchor(GBC.LE));
			timePanel.setOpaque(false);
		}
		add(icon, GBC.grid(0, 0).insets(0, 0, 0, 8));
		add(infoPanel, GBC.grid(1, 0).fill(GBC.B).weight(1.0, 1.0));
		add(timePanel, GBC.grid(2, 0).fill(GBC.H));
	}

	private void updateVisibility(int identifier) {
		if ((identifier & NAME) == NAME) {
			name.setVisible(Settings.coreShowSegmentName.get());
		}
		if ((identifier & TIME) == TIME) {
			State   state   = run.getState();
			boolean visible = (state == State.ONGOING || state == State.PAUSED);
			split.setVisible(Settings.coreShowSplitTime.get());
			labelSplit.setVisible(visible && Settings.coreShowSplitTime.get());
			segment.setVisible(Settings.coreShowSegmentTime.get());
			labelSegment.setVisible(visible && Settings.coreShowSegmentTime.get());
			best.setVisible(Settings.coreShowBestTime.get());
			labelBest.setVisible(visible && Settings.coreShowBestTime.get());
		}
		if ((identifier & TIMER) == TIMER) {
			segmentTimer.setVisible(Settings.coreShowSegmentTimer.get());
		}
		if ((identifier & ICON) == ICON) {
			icon.setVisible(Settings.coreShowIcons.get());
		}
	}

	/**
	 * Updates the values of the group of components specified by the
	 * identifier.
	 *
	 * @param   identifier  - one of the constant update identifier.
	 */
	private void updateValues(int identifier) {
		State   state      = run.getState();
		boolean hasCurrent = (state == State.ONGOING || state == State.PAUSED);
		int     currentIdx = run.getCurrent();
		Segment currentSgt = null;

		if (hasCurrent) {
			currentSgt = run.getSegment(currentIdx);
		}
		if ((identifier & TIME) == TIME) {
			if (hasCurrent) {
				splitLoss   = false;
				segmentLoss = false;
				splitTime   = run.getTime(currentIdx, Segment.SET);
				segmentTime = currentSgt.getTime(Segment.SET);
				split.setText("" + (splitTime == null ? "?" : splitTime));
				segment.setText("" + (segmentTime == null ? "?" : segmentTime));
				Time bestTime = currentSgt.getTime(Segment.BEST);
				best.setText("" + (bestTime == null ? "?" : bestTime));
			} else {
				split.setText("");
				segment.setText("");
				best.setText("");
			}
		}
		if ((identifier & NAME) == NAME) {
			if (hasCurrent) {
				name.setText(currentSgt.getName());
			} else {
				name.setText("");
			}
		}
		if ((identifier & ICON) == ICON) {
			if (hasCurrent) {
				ImageIcon img = currentSgt.getIcon();
				if (img != null) {
					icon.setIcon(
							Images.rescale(img, Settings.coreIconSize.get()));
				} else {
					icon.setIcon(null);
				}
			} else {
				icon.setIcon(null);
			}
		}
		if ((identifier & TIMER) == TIMER) {
			synchronized (this) {
				if (state == State.STOPPED) {
					timer.stop();
					splitLoss   = false;
					segmentLoss = false;
					segmentTimer.setText("");
					Time time = run.getTime(Segment.LIVE);
					splitTimer.setText("" + (time == null ? Language.RUN_STOPPED : time));
				} else if (state == State.NULL) {
					splitTimer.setText("" + Language.RUN_NULL);
					segmentTimer.setText("");
				} else if (state == State.READY) {
					timer.stop();
					splitLoss   = false;
					segmentLoss = false;
					String timeString = getLiveTimeString();
					splitTimer.setText("" + (timeString == null ? Language.RUN_READY : timeString));
					segmentTimer.setText("");
				} else if (state == State.ONGOING) {
					timer.restart();
				}
			}
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
			Color color = Settings.colorTime.get();
			split.setForeground(color);
			segment.setForeground(color);
			best.setForeground(color);
		}
		if ((identifier & NAME) == NAME) {
			Color color = Settings.colorForeground.get();
			name.setForeground(color);
			labelBest.setForeground(color);
			labelSegment.setForeground(color);
			labelSplit.setForeground(color);
		}
		if ((identifier & TIMER) == TIMER) {
			synchronized (this) {
				Color color = Settings.colorTimer.get();
				if (isShowingNegativeTime() && run.getState() == State.READY)
					splitTimer.setForeground(Settings.colorNegativeTime.get());
				else
					splitTimer.setForeground(color);
				segmentTimer.setForeground(color);
			}
		}
	}

	/**
	 * Updates the fonts of the group of components specified by the
	 * identifier.
	 * @param identifier - one of the constant update identifier.
	 */
	private void updateFonts(int identifier) {
		if ((identifier & TIME) == TIME) {
			split.setFont(Settings.coreOtherTimeFont.get());
			segment.setFont(Settings.coreOtherTimeFont.get());
			best.setFont(Settings.coreOtherTimeFont.get());
		}
		if ((identifier & NAME) == NAME) {
			name.setFont(Settings.coreFont.get());
			labelBest.setFont(Settings.coreFont.get());
			labelSegment.setFont(Settings.coreFont.get());
			labelSplit.setFont(Settings.coreFont.get());
		}
		if ((identifier & TIMER) == TIMER) {
			splitTimer.setFont(Settings.coreTimerFont.get());
			segmentTimer.setFont(Settings.coreSegmentTimerFont.get());
		}
	}

	private void updateSize() {
	}

	private boolean isShowingNegativeTime() {
		if (run != null) {
			Time time = run.getTime(Segment.LIVE);
			if (time == null)
				time = new Time(0 - run.getDelayedStart());

			return time.getMilliseconds() < 0;
		}

		return false;
	}

	private String getLiveTimeString() {
		if (run != null) {
			Time time = run.getTime(Segment.LIVE);
			if (time == null)
				time = new Time(0 - run.getDelayedStart());

			if (time.getMilliseconds() < 0)
				return "-" + time.toString();
			else
				return time.toString();
		}

		return null;
	}
}
