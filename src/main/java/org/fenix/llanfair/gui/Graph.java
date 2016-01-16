package org.fenix.llanfair.gui;

import org.fenix.llanfair.Language;
import org.fenix.llanfair.Run;
import org.fenix.llanfair.Run.State;
import org.fenix.llanfair.Segment;
import org.fenix.llanfair.Time;
import org.fenix.llanfair.config.Settings;
import org.fenix.utils.gui.GBC;
import org.fenix.utils.locale.LocaleEvent;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Graph panel displaying information concerning a run. It includes an actual
 * graph where each vertex is a live split time and a scale. The vertices are 
 * placed in accordance to {@link Run#getCompareTime()}.
 *
 * @author  Xavier "Xunkar" Sencert
 */
class Graph extends JPanel {

	// -------------------------------------------------------------- CONSTANTS

	/**
	 * Half the thickness of the stroke used to paint the graph. This value is
	 * used to make sure the stroke retains its full thickness when reaching
	 * the top or the bottom of the canvas.
	 */
	protected static final int HALF_THICKNESS = 1;

	/**
	 * The stroke used to paint the graph in itself (i.e. the lines connecting
	 * the vertices.)
	 */
	protected static final Stroke GRAPH_STROKE = new BasicStroke(2.0F);

	/**
	 * The dashed stroke used to paint the projection of the vertices.
	 */
	protected static final Stroke DASHED_STROKE = new BasicStroke(
			1.0F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0F,
			new float[] { 2.0F }, 0.0F );

	/**
	 * Update identifier for every category.
	 */
	private static final int ALL = 0xff;

	/**
	 * Update identifier for text variables.
	 */
	private static final int TEXT = 0x01;

	/**
	 * Update identifier for time variables.
	 */
	private static final int TIME = 0x02;

	/**
	 * Minimum width in pixels of this component.
	 */
	private static final int PACK_WIDTH = 50;

	/**
	 * Minimum height in pixels of this component.
	 */
	private static final int PACK_HEIGHT = 50;

	// ------------------------------------------------------------- ATTRIBUTES

	/**
	 * Run instance represented by this component.
	 */
	protected Run run;

	/**
	 * Canvas where the graph will be drawn.
	 */
	private Canvas canvas;

	/**
	 * Label displaying the current scale of the graph. The scale actually
	 * displays the time represented by the maximum ordinate.
	 */
	private JLabel scale;

	/**
	 * Label describing the scale value being displayed.
	 */
	private JLabel scaleText;

	// ----------------------------------------------------------- CONSTRUCTORS

	/**
	 * Creates a default graph panel representing the given run.
	 *
	 * @param   run - the run to represent.
	 */
	Graph(Run run) {
		canvas    = new Canvas();
		scale     = new JLabel();
		scaleText = new JLabel();

		setRun(run);
		setOpaque(false);

		updateValues(TEXT);
		updateColors(ALL);
		updateFonts(ALL);
		placeComponents();

		Dimension size = new Dimension(PACK_WIDTH, PACK_HEIGHT);
		setPreferredSize(size);
		setMinimumSize(size);
	}

	// -------------------------------------------------------------- INTERFACE

	/**
	 * Sets the new run to represent.
	 *
	 * @param   run - the new run to represent.
	 */
	void setRun(Run run) {
		this.run = run;
		updateValues(TIME);
	}

	/**
	 * Callback invoked by the parent when the run or the application's
	 * settings have seen one of their properties updated.
	 *
	 * @param   event   - the event describing the update.
	 */
	void processPropertyChangeEvent(PropertyChangeEvent event) {
		String property = event.getPropertyName();
		// Settings.COLOR_FOREGROUND
		if (Settings.colorForeground.equals(property)) {
			updateColors(TEXT);
			canvas.repaint();
		// Settings.COLOR_TIME
		} else if (Settings.colorTime.equals(property)) {
			updateColors(TIME);
		// Settings.COLOR_BACKGROUND, COLOR_TIME_LOST, COLOR_TIME_GAINED
		// or Run.CURRENT_SEGMENT_PROPERTY
		} else if (Settings.colorBackground.equals(property)
				|| Settings.colorTimeGainedWhileBehind.equals(property)
		        || Settings.colorTimeLostWhileBehind.equals(property)
				|| Settings.colorTimeGainedWhileAhead.equals(property)
		        || Settings.colorTimeLostWhileAhead.equals(property)
				|| Settings.colorNewRecord.equals(property)
				|| Run.CURRENT_SEGMENT_PROPERTY.equals(property)) {
			canvas.repaint();
		// Settings.COMPARE_PERCENT or Settings.COMPARE_METHOD
		} else if (Settings.graphScale.equals(property)
				|| Settings.compareMethod.equals(property)) {
			updateValues(TIME);
			canvas.repaint();
		// Settings.ACCURACY
		} else if (Settings.accuracy.equals(property)) {
			updateValues(TIME);
		// Run.STATE_PROPERTY
		} else if (Run.STATE_PROPERTY.equals(property)) {
			if (run.getState() == State.READY) {
				canvas.repaint();
			} else if (run.getState() == State.NULL) {
				updateValues(TIME);
			}
		} else if (Settings.coreFont.equals(property)) {
			updateFonts(ALL);
		} else if (Settings.windowUserResizable.equals(property) || Settings.windowWidth.equals(property)) {
			updateSize();
			forceResize();
		}
	}

	private void forceResize() {
		revalidate();
	}

	/**
	 * Callback invoked by the parent when the run table of segments is
	 * updated.
	 *
	 * @param   event   - the event describing the update.
	 */
	void processTableModelEvent(TableModelEvent event) {
		int type   = event.getType();
		int column = event.getColumn();
		if (type != TableModelEvent.UPDATE
				|| column == TableModelEvent.ALL_COLUMNS
				|| column == Run.COLUMN_BEST
				|| column == Run.COLUMN_SEGMENT
				|| column == Run.COLUMN_TIME) {
			updateValues(TIME);
			canvas.repaint();
		}
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
	 * Returns a percent representing the delta split time of the segment of
	 * given index in relation to a set fraction of the whole run given by
	 * {@link Run#getCompareTime()}.
	 *
	 * @param   index   - the index of the segment to compare.
	 * @return  the percent of the segment delta split time and the run’s
	 *          compare time.
	 */
	protected long getCompareTimePercent(int index) {
		long compare = run.getCompareTime().getMilliseconds();
		long delta   = run.getTime(index, Segment.DELTA).getMilliseconds();

		return (delta * 100L) / compare;
	}

	/**
	 * Places the sub-components within this component.
	 */
	private void placeComponents() {
		setLayout(new GridBagLayout());
		JPanel scalePanel = new JPanel(); {
			scalePanel.add(scaleText);
			scalePanel.add(scale);
			scalePanel.setOpaque(false);
		}
		add(scalePanel, GBC.grid(0, 0).anchor(GBC.LINE_START));
		add(canvas, GBC.grid(0, 1).fill(GBC.BOTH).weight(1.0, 1.0));
	}

	/**
	 * Updates the values of the group of components specified by the
	 * identifier.
	 *
	 * @param   identifier  - one of the constant update identifier.
	 */
	private void updateValues(int identifier) {
		// TIME
		if ((identifier & TIME) == TIME) {
			Time time = run.getCompareTime();
			scale.setText("" + (time == null ? "???" : time));
		}
		// TEXT
		if ((identifier & TEXT) == TEXT) {
			scaleText.setText("" + Language.MAX_ORDINATE);
		}
	}

	/**
	 * Updates the colors of the group of components specified by the
	 * identifier.
	 *
	 * @param   identifier  - one of the constant update identifier.
	 */
	private void updateColors(int identifier) {
		// TIME
		if ((identifier & TIME) == TIME) {
			scale.setForeground(Settings.colorTime.get());
		}
		// TEXT
		if ((identifier & TEXT) == TEXT) {
			scaleText.setForeground(Settings.colorForeground.get());
		}
	}

	/**
	 * Updates the fonts of the group of components specified by the
	 * identifier.
	 * @param identifier - one of the constant update identifier.
	 */
	private void updateFonts(int identifier) {
		if ((identifier & TIME) == TIME) {
			scale.setFont(Settings.coreFont.get());
		}
		if ((identifier & TEXT) == TEXT) {
			scaleText.setFont(Settings.coreFont.get());
		}
	}

	private void updateSize() {
	}

	// ---------------------------------------------------------- INTERNAL TYPE

	/**
	 * A simple panel whose paint method has been overriden to draw the graph.
	 *
	 * @author  Xavier "Xunkar" Sencert
	 */
	protected class Canvas extends JPanel {

		// ----------------------------------------------------- INTERFACE

		/**
		 * Draws the graph onto the canvas.
		 */
		@Override protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			int clipH = getHeight();
			int clipW = getWidth();
			int halfH = clipH / 2;

			g2.setColor(Settings.colorBackground.get());
			g2.fillRect(0, 0, clipW, clipH);
  
			Color colorFG = Settings.colorForeground.get();
			Color colorTG = Settings.colorTimeGainedWhileAhead.get();
			Color colorTL = Settings.colorTimeLostWhileBehind.get();
			Color colorRC = Settings.colorNewRecord.get();

			// Draw the axis.
			g2.setColor(colorFG);
			g2.drawLine(0, halfH, clipW, halfH);

			if (run.getState() != State.NULL) {
				int    segCnt = run.getRowCount();
				double segGap = (double) clipW / segCnt;

				if (run.hasPreviousSegment()) {
					// Coordinates of the last drawn vertex.
					int prevX = 0;
					int prevY = halfH;

					for (int i = 0; i < run.getCurrent(); i++) {
						Time delta = run.getTime(i, Segment.DELTA);
						Time live  = run.getTime(i, Segment.LIVE);
						if (delta != null && live != null) {
							int percent = (int) getCompareTimePercent(i);
							g2.setColor(run.isBetterSegment(i) ? colorTG : colorTL);
							if (run.isBestSegment(i)) {
								g2.setColor(colorRC);
							}

							// Coordinates of this segment’s vertex.
							int coordY = halfH - ((percent * halfH) / 100);
							coordY     = Math.min(clipH - HALF_THICKNESS, coordY);
							coordY     = Math.max(HALF_THICKNESS, coordY);
							int coordX = (int) ((i + 1) * segGap);

							// Set the brush depending on the delta.
							g2.setStroke(GRAPH_STROKE);

							// Make sure the last vertex reaches the pane’s end.
							if (i == segCnt - 1) {
								coordX = Math.min(coordX - 1, clipW);
							}
							g2.drawLine(prevX, prevY, coordX, coordY);

							// Projection along the x axis.
							g2.setColor(colorFG);
							g2.setStroke(DASHED_STROKE);
							g2.drawLine(coordX, halfH, coordX, coordY);

							prevY = coordY;
							prevX = coordX;
						}
					}
				}
			}
		}

	}

}
