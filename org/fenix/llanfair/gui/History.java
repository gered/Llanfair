package org.fenix.llanfair.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;

import org.fenix.llanfair.Language;
import org.fenix.llanfair.Run;
import org.fenix.llanfair.Segment;
import org.fenix.llanfair.config.Settings;
import org.fenix.llanfair.Time;
import org.fenix.llanfair.Run.State;
import org.fenix.llanfair.config.Merge;
import org.fenix.utils.gui.GBC;
import org.fenix.utils.Images;

/**
 * A scrolling pane capable of displaying a specific number of segments with
 * information concerning their times. This pane contains a viewport on
 * a number of segments equals to the minimum between the total number of 
 * segments in the run and the number of segments to display as per the 
 * settings. If the history is set to display blank rows, the number of 
 * segments in the viewport is always the number of desired segments.
 *
 * @author  Xavier "Xunkar" Sencert
 * @version 1.2
 * @see     Run
 * @see     Segment
 */
public class History extends JPanel {

    // Update Identifiers
    private static final int ALL    = 0xff;
    private static final int TIME   = 0x01;
    private static final int LIVE   = 0x02;
    private static final int NAME   = 0x04;
    private static final int MARKER = 0x08;
    private static final int TABS   = 0x10;
    private static final int DELTA  = 0x20;
    private static final int ICON   = 0x30;
    private static final int LINE   = 0x40;

    /**
     * Run instance represented by the panel. Package-private as to make it
     * available to inner types. Cannot be {@code null}.
     */
    Run run;

    /**
     * Current number of segments to display in the history, in other words, 
     * the number of rows currently displayed in the viewport.
     */
    private int rowCount;

    /**
     * List containing the rows of segment to be displayed in the history. This
     * list will contain all the rows we could need.
     * 
     * @see SegmentRow
     */
    private List<SegmentRow> segmentRows;

    /**
     * The ideal display size of this component. Stored in an attribute to
     * be retrieved easily without recomputing the size.
     */
    private Dimension preferredSize;

    /**
     * Wether or not the component should recompute its ideal size. 
     */
    private boolean resize;

    // ----------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a default panel displaying information for the given run. 
     * Package-private as an history must be created in a global GUI context.
     *
     * @param   run - the run to represent. Cannot be {@code null}.
     */
    History(Run run) {
        super(new GridBagLayout());

        segmentRows   = new ArrayList<SegmentRow>();
        preferredSize = null;
        resize        = false;

        setRun(run);
        setOpaque(false);
    }

    // -------------------------------------------------------------- INTERFACE

    /**
     * Returns the current number of segments to display in the history 
     * meaning, the number of rows in the viewport.
     * 
     * @return  the number of rows in the viewport.
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * Sets the run to represent. All rows are cleared and recreated using
     * the segments from the given run.
     *
     * @param   run - the new run to represent. 
     */
    final void setRun(Run run) {
        this.run = run;
        populateRows();
    }

    /**
     * Returns the preferred size of this component. This method is heap-cheap
     * as it recomputes the preferred size only when necessary.
     */
    @Override public Dimension getPreferredSize() {
        if (resize) {
            Graphics graphics = getGraphics();
            if (graphics != null) {
                // Segment Names
                FontMetrics nameMetric = graphics.getFontMetrics(
                        Settings.HST_SFNT.get()
                );
                int  wName = 0;
                for (int i = 0; i < run.getRowCount(); i++) {
                    String name = run.getSegment(i).getName();
                    wName = Math.max(wName, nameMetric.stringWidth(name));
                }
                // Split Time
                FontMetrics timeMetric = graphics.getFontMetrics(
                        Settings.HST_TFNT.get()
                );
                Time tmFake = new Time(600000L);
                Time tmRun  = run.getTime(Segment.SET);
                int  wRun   = timeMetric.stringWidth(
                        "" + (tmRun == null ? tmFake : tmRun)
                );
                Merge merge = Settings.HST_MERG.get();
                // Live Time
                int wLive = 0;
                if (Settings.HST_LIVE.get() && merge != Merge.LIVE) {
                    wLive = wRun;
                }
                // Delta Time
                int wDelta = 0;
                if (Settings.HST_DLTA.get() && merge != Merge.DELTA) {
                    wDelta = wRun + timeMetric.stringWidth("[+]");
                }
                // Segment Icons
                int wIcon = 0;
                if (Settings.HST_ICON.get() && run.getMaxIconHeight() > 0) {
                    wIcon = Settings.HST_ICSZ.get();
                }
                // MAX WIDTH
                int maxWidth;
                if (Settings.HST_LINE.get()) {
                    maxWidth = Math.max(wName + wIcon, wRun + wLive + wDelta);
                } else {
                    maxWidth = wName + wRun + wLive + wDelta + wIcon;
                }
                // Segment Names
                int hName = nameMetric.getHeight();
                // Times
                int hTime = timeMetric.getHeight();
                // Segment Icons.
                int hIcon = 0;
                if (Settings.HST_ICON.get() && run.getMaxIconHeight() > 0) {
                    hIcon = Settings.HST_ICSZ.get();
                }
                // MAX HEIGHT
                int maxHeight;
                if (Settings.HST_LINE.get()) {
                    maxHeight = Math.max(hName + hTime, hIcon);
                } else {
                    maxHeight = Math.max(hIcon, Math.max(hName, hTime));
                }
                maxHeight = rowCount * maxHeight;
                
                preferredSize = new Dimension(maxWidth + 10, maxHeight);
                setMinimumSize(new Dimension(50, maxHeight));
            }
            resize = false;
        }
        return (preferredSize == null ? getMinimumSize() : preferredSize);
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
            int neu = (Integer) event.getNewValue();
            int old = (Integer) event.getOldValue();
            // Display the live time for the segment we just split.
            int previous = run.getPrevious();
            if (previous > -1) {
                updateValues(LIVE, previous, previous);
            }
            // And move to the next segment in the history.
            updateColors(MARKER);
            computeViewport();
            // If we unsplit, restore the previous segment values.
            if (neu < old) {
                updateValues(TIME, neu, neu);
                updateColors(TIME, neu, neu);
                segmentRows.get(neu).live.setText("");
                segmentRows.get(neu).delta.setText("");
            }
//            updateColumnWidth();
        } else if (Run.STATE_PROPERTY.equals(property)) {
            // Clear the history when the run is reset.
            if (run.getState() == State.READY) {
                computeViewport();
                updateValues(TIME | LIVE);
                updateColors(MARKER | TIME);
//                updateColumnWidth();
                // When the run stops, clear the marker.    
            } else if (run.getState() == State.STOPPED) {
                updateColors(MARKER);
            }
        } else if (Settings.HST_TABL.equals(property)) {
            updateVisibility(LIVE | DELTA);
            updateColumnWidth();
        } else if (Settings.HST_SFNT.equals(property)) {
            updateFonts(NAME);
            forceResize();
        } else if (Settings.HST_TFNT.equals(property)) {
            updateFonts(TIME);
            forceResize();
        } else if (Settings.HST_LINE.equals(property)) {
            updateValues(LINE);
            forceResize();
        } else if (Settings.HST_ICSZ.equals(property)) {
            updateValues(ICON);
            forceResize();
        } else if (Settings.HST_ICON.equals(property)) {
            updateVisibility(ICON);
            forceResize();
        } else if (Settings.HST_LAST.equals(property)
                || Settings.HST_OFFS.equals(property)) {
            computeViewport();
        } else if (Settings.HST_ROWS.equals(property)
                || Settings.HST_BLNK.equals(property)) {
            populateRows();
        } else if (Settings.CLR_GAIN.equals(property)
                || Settings.CLR_LOST.equals(property)
                || Settings.CLR_RCRD.equals(property)) {
            updateColors(LIVE);
        } else if (Settings.CLR_HIGH.equals(property)) {
            updateColors(MARKER);
        } else if (Settings.CLR_TIME.equals(property)) {
            updateColors(TIME | LIVE);
        } else if (Settings.CLR_FORE.equals(property)) {
            updateColors(NAME); 
        } else if (Settings.GNR_ACCY.equals(property)
                || Settings.GNR_COMP.equals(property)) {
            updateValues(LIVE | TIME);
            forceResize();
        } else if (Settings.HST_DLTA.equals(property)) {
            updateVisibility(DELTA);
            forceResize();
        } else if (Settings.HST_LIVE.equals(property)) {
            updateVisibility(LIVE);
            forceResize();
        } else if (Settings.HST_MERG.equals(property)) {
            updateValues(TIME | LIVE);
            updateColors(TIME);
            forceResize();
        }
    }

    /**
     * Callback invoked by the parent when the run table of segments is
     * updated.
     *
     * @param   event   - the event describing the update.
     */
    void processTableModelEvent(TableModelEvent event) {
        int type     = event.getType();
        int firstRow = event.getFirstRow();
        int lastRow  = event.getLastRow();

        if (type == TableModelEvent.INSERT) {
            populateRows();
        } else if (type == TableModelEvent.DELETE) {
            populateRows();
            repaint();
        } else if (type == TableModelEvent.UPDATE) {
            if (firstRow == TableModelEvent.HEADER_ROW) {
                populateRows();
            } else {
                updateValues(TIME | NAME | ICON, firstRow, lastRow);
                updateVisibility(ICON);
            }
        }
    }

    // -------------------------------------------------------------- UTILITIES
    
    /**
     * Creates a segment row for each segment in the run, counting possible
     * blank rows in advance and places them in the panel. Each row is then
     * displayed using {@code setVisible()} depending on wether we want it
     * in the viewport or not. This function should be called everytime the
     * run structure is changed and rows must be added or removed.
     */
    private void populateRows() {
        // Clear the panel and the row list.
        removeAll();
        segmentRows.clear();
        // At most, we need as much segments as the run has or as much as
        // is demanded by the user.
        int count = Settings.HST_ROWS.get();
            count = Math.max(count, run.getRowCount());
        // Create and place the rows.
        for (int i = 0; i < count; i++) {
            SegmentRow row = new SegmentRow();
            add(row, GBC.grid(0, i).fill(GBC.H).weight(1.0, 0.0));
            segmentRows.add(i, row);
        }
        // Fill the rows with the segment data and colorize.
        updateValues(ALL);
        updateColors(ALL);
        updateFonts(ALL);
        updateVisibility(ALL);
        // Only display the segments we can currently see.
        computeViewport();
        // Force computation of minimum component size.
        forceResize();
    }
    
    /**
     * Asks the component to compute its minimum size and assume it. Should be
     * called everytime an update to the run or the settings could impact the
     * height/width of the history.
     */
    private void forceResize() {
        resize = true;
        revalidate();
    }

    /**
     * Computes which segments need to be displayed in the history and sets
     * their visibility accordingly. 
     */
    private void computeViewport() {
        // If we display blank rows, the row count is always the value
        // from the settings, else check how much segments are in the run.
        rowCount = Settings.HST_ROWS.get();
        if (!Settings.HST_BLNK.get()) {
            rowCount = Math.min(run.getRowCount(), rowCount);
        }
        // If we always display the last segment, we scroll on n-1 segments.
        boolean showLast  = Settings.HST_LAST.get();
        int     realCount = showLast ? rowCount - 1 : rowCount;
        int     endOffset = showLast ? 2 : 1;
        // Find out which segment will be at the end of the history.
        int desired = run.getCurrent() + Settings.HST_OFFS.get();
        int lastSeg = (desired < realCount) ? realCount - 1 : desired;
        if (lastSeg > run.getRowCount() - endOffset) {
            lastSeg = run.getRowCount() - endOffset;
        }
        // Set the visibility of every segments accordingly.
        for (int i = 0; i < segmentRows.size(); i++) {
            segmentRows.get(i).setVisible(
                    (i > lastSeg - realCount) && (i <= lastSeg)
            );
        }
        // Display the last segment if the setting is enabled.
        if (Settings.HST_LAST.get() && run.getRowCount() > 0) {
            segmentRows.get(run.getRowCount() - 1).setVisible(true);
        }
    }
    
    private void updateColumnWidth() {
        int width = 0;
        int height = 0;
        if (run.hasPreviousSegment()) {
            SegmentRow previous = segmentRows.get(run.getPrevious());
            FontMetrics metrics = getGraphics().getFontMetrics(
                    Settings.HST_TFNT.get()
            );
            width = metrics.stringWidth(previous.delta.getText());
            height = metrics.getHeight();
        }
        for (int i = 0; i < segmentRows.size(); i++) {
            segmentRows.get(i).delta.setPreferredSize(
                    new Dimension(width, height)
            );
            segmentRows.get(i).revalidate();
        }
    }

    private void updateColors(int identifier, int first, int last) {
        for (int i = first; i <= last; i++) {
            segmentRows.get(i).updateColors(i, identifier);
        }
    }

    private void updateColors(int identifier) {
        updateColors(identifier, 0, run.getRowCount() - 1);
    }

    private void updateValues(int identifier, int first, int last) {
        for (int i = first; i <= last; i++) {
            segmentRows.get(i).updateValues(i, identifier);
        }
    }

    private void updateValues(int identifier) {
        updateValues(identifier, 0, run.getRowCount() - 1);
    }

    private void updateVisibility(int identifier, int first, int last) {
        for (int i = first; i <= last; i++) {
            segmentRows.get(i).updateVisibility(i, identifier);
        }
    }

    private void updateVisibility(int identifier) {
        updateVisibility(identifier, 0, run.getRowCount() - 1);
    }
    
    private void updateFonts(int identifier, int first, int last) {
        for (int i = first; i <= last; i++) {
            segmentRows.get(i).updateFonts(i, identifier);
        }
    }

    private void updateFonts(int identifier) {
        updateFonts(identifier, 0, run.getRowCount() - 1);
    }

    // --------------------------------------------------------- INTERNAL TYPES

    private class SegmentRow extends JPanel {

        // ------------------------------------------------------ CONSTANTS

        /**
         * Margin in pixels between the labels. 
         */
        static final int INSET = 3;

        // ----------------------------------------------------- ATTRIBUTES

        /**
         * The icon (if any) of this segment.
         */
        JLabel icon;

        /**
         * The name of the segment.
         */
        JLabel name;

        /**
         * The registered split time of this segment.
         */
        JLabel time;

        /**
         * The achieved split time of this segment.
         */
        JLabel live;

        /**
         * The delta between the registered and achieved split time.
         */
        JLabel delta;

        /**
         * The counters updates that occured during this segment.
         */
        List<JLabel> counters;

        // --------------------------------------------------- CONSTRUCTORS

        /**
         * Creates an empty new segment row.
         */
        SegmentRow() {
            super(new GridBagLayout());

            icon     = new JLabel();
            name     = new JLabel();
            time     = new JLabel();
            live     = new JLabel();
            delta    = new JLabel();
            counters = new ArrayList<JLabel>();
            
            icon.setHorizontalAlignment(JLabel.CENTER);
            setOpaque(false);
            placeComponents(Settings.HST_LINE.get());
        }

        // ------------------------------------------------------ INTERFACE

        /**
         * Updates the values of the group of components specified by the 
         * identifier for this segment row. The row must know which segment
         * he represents by passing the segment index.
         *
         * @param   index       - index of the segment represented by this row.
         * @param   identifier  - one of the constant update identifier.
         */
        void updateValues(int index, int identifier) {
            if ((identifier & NAME) == NAME) {
                name.setText(run.getSegment(index).getName());
            }
            if ((identifier & TIME) == TIME) {
                Time setTime = run.getTime(index, Segment.SET);
                time.setText("" + (setTime == null ? "?" : setTime));
            }
            if ((identifier & ICON) == ICON) {
                int  iconSize = Settings.HST_ICSZ.get();
                Icon runIcon  = run.getSegment(index).getIcon();
                if (runIcon != null) {
                    icon.setIcon(Images.rescale(runIcon, iconSize));
                } else {
                    icon.setIcon(null);
                }
                icon.setPreferredSize(new Dimension(iconSize, iconSize));
                icon.setMinimumSize(new Dimension(iconSize, iconSize));
            }
            if ((identifier & LINE) == LINE) {
                removeAll();
                placeComponents(Settings.HST_LINE.get());
            }
            if ((identifier & LIVE) == LIVE && (index > -1)) {
                Merge  merge     = Settings.HST_MERG.get();
                JLabel realDelta = (merge == Merge.DELTA) ? time : delta;
                JLabel realLive  = (merge == Merge.LIVE ) ? time : live;   

                if (index < run.getCurrent()) {
                    Time liveTime = run.getTime(index, Segment.LIVE);
                    if (liveTime == null) {
                        realLive.setText("?");
                        realDelta.setText("[?]");
                    } else {
                        realLive.setText("" + liveTime);

                        String    text = "?";
                        Time deltaTime = run.getTime(index, Segment.DELTA);
                        if (deltaTime != null) {
                            text = deltaTime.toString(true);
                        }
                        if (merge == Merge.DELTA) {
                            realDelta.setText(text);
                        } else {
                            realDelta.setText("[" + text + "]");
                        }
                    }
                } else {
                    Time setTime = run.getTime(index, Segment.SET);
                    realLive.setText(
                            (merge == Merge.LIVE) ? 
                            "" + (setTime == null ? "?" : setTime) 
                            : ""
                    );
                    realDelta.setText(
                            (merge == Merge.DELTA) ? 
                            "" + (setTime == null ? "?" : setTime) 
                            : ""
                    );
                }
                updateColors(index, LIVE);
            }
        }

        void updateVisibility(int index, int identifier) {
            if ((identifier & LIVE) == LIVE) {
                live.setVisible(
                        Settings.HST_LIVE.get() || Settings.HST_TABL.get()
                );
            }
            if ((identifier & DELTA) == DELTA) {
                delta.setVisible(Settings.HST_DLTA.get()  || Settings.HST_TABL.get());
            }
            if ((identifier & ICON) == ICON) {
                icon.setVisible(Settings.HST_ICON.get() 
                        && run.getMaxIconHeight() > 0);
            }
        }

        void updateColors(int index, int identifier) {
            if ((identifier & NAME) == NAME) {
                name.setForeground(Settings.CLR_FORE.get());
            }
            if ((identifier & TIME) == TIME) {
                time.setForeground(Settings.CLR_TIME.get());
            }
            if ((identifier & MARKER) == MARKER) {
                if (run.getCurrent() == index) {
                    name.setForeground(Settings.CLR_HIGH.get());
                } else {
                    name.setForeground(Settings.CLR_FORE.get());
                }
            }
            if ((identifier & LIVE) == LIVE && (index > -1)) {
                Color lost  = Settings.CLR_LOST.get();
                Color gain  = Settings.CLR_GAIN.get();
                Color neut  = Settings.CLR_TIME.get();
                Color recd  = Settings.CLR_RCRD.get();
                int   prev  = run.getPrevious();

                Merge  merge     = Settings.HST_MERG.get();
                JLabel realDelta = (merge == Merge.DELTA) ? time : delta;
                JLabel realLive  = (merge == Merge.LIVE ) ? time : live;

                if (index <= prev) {
                    Time liveTime  = run.getTime(index, Segment.LIVE);
                    Time deltaTime = run.getTime(index, Segment.DELTA);
                    if (run.isBestSegment(index)) {
                        realLive.setForeground(recd);
                        realDelta.setForeground(recd);
                    } else {
                        if (deltaTime != null) {
                            int compare = deltaTime.compareTo(Time.ZERO);
                            if (liveTime == null) {
                                realLive.setForeground(neut);
                                realDelta.setForeground(neut);
                            } else {
                                if (compare > 0) {
                                    realDelta.setForeground(lost);
                                    realLive.setForeground(lost);
                                } else { 
                                    realDelta.setForeground(gain);
                                    realLive.setForeground(gain);
                                }
                            }
                        } else {
                            realDelta.setForeground(neut);
                            realLive.setForeground(neut);
                        }
                    }
                }
            }
        }
        
        void updateFonts(int index, int identifier) {
            if ((identifier & NAME) == NAME) {
                name.setFont(Settings.HST_SFNT.get());
            }
            if ((identifier & TIME) == TIME) {
                Font font = Settings.HST_TFNT.get();
                time.setFont(font);
                live.setFont(font);
                delta.setFont(font);
            }
        }

        // ------------------------------------------------------ UTILITIES

        private void placeComponents(boolean multiline) {
            if (!multiline) {
                add(icon , GBC.grid(0, 0).anchor(GBC.C));
                add(
                        name, GBC.grid(1, 0).insets(0, INSET, 0, 0).anchor(GBC.LS)
                        .fill(GBC.H).weight(1.0, 0.0)
                );
                add(time , GBC.grid(2, 0).insets(0, INSET, 0, 0).anchor(GBC.LE));
                add(live , GBC.grid(3, 0).insets(0, INSET, 0, 0).anchor(GBC.LE));
                add(delta, GBC.grid(4, 0).insets(0, INSET, 0, 0).anchor(GBC.LE));
            } else {
                add(icon, GBC.grid(0, 0, 1, 2).anchor(GBC.C));
                add(
                        name, GBC.grid(1, 0, 3, 1).anchor(GBC.LS).fill(GBC.H)
                            .weight(1.0, 0.0).insets(0, INSET, 0, 0)
                );
                add(time, GBC.grid(1, 1).anchor(GBC.LE).insets(0, INSET, 0, 0));
                add(live, GBC.grid(2, 1).anchor(GBC.LE).insets(0, INSET, 0, 0));
                add(delta, GBC.grid(3, 1).anchor(GBC.LE));
            }
        }
    }

}
