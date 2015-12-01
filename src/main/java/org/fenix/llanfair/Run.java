package org.fenix.llanfair;

import org.fenix.llanfair.config.Settings;
import org.fenix.utils.TableModelSupport;
import org.fenix.utils.config.Configuration;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Modélise une course comportant un certain nombre de segments. Une fois une
 * course définie avec des objectifs chiffrés à chaque segment, il est possible
 * de démarrer la course. À chaque fois qu’un segment est passé, on réalise un
 * <em>split</em>, enregistrant le nouveau temps réalisé. Différentes méthodes
 * sont disponibles afin de comparer le temps réalisé avec le temps objectif.
 * De plus, {@code Run} implémente {@link TableModel} dans lequel chaque segment
 * est une ligne.  
 *
 * @author  Xavier "Xunkar" Sencert
 * @see     TableModel
 * @see     Segment
 */
public class Run implements TableModel, Serializable {

	// -------------------------------------------------------------- CONSTANTS

	/**
	 * The serial version identifier used to determine the compatibility of the
	 * different serialized versions of this type. This identifier must change
	 * when modifications that break backward-compatibility are made to the
	 * type.
	 */
	public static final long serialVersionUID = 1010L;

	public static final int MAX_COUNTERS = 4;

	/**
	 * Identifier for the column containing the segment’s icon.
	 */
	public static final int COLUMN_ICON = 0;

	/**
	 * Identifier for the column containing the segment’s name.
	 */
	public static final int COLUMN_NAME = 1;

	/**
	 * Identifier for the column containing the segment’s run time.
	 */
	public static final int COLUMN_TIME = 2;

	/**
	 * Identifier for the column containing the segment’s segment time.
	 */
	public static final int COLUMN_SEGMENT = 3;

	/**
	 * Identifier for the column containing the segment’s best segment time.
	 */
	public static final int COLUMN_BEST = 4;

	/**
	 * Number of columns displayed in the table of segments.
	 */
	private static final int COLUMN_COUNT = 5;

	/**
	 * Identifier for the bean property name of the run.
	 */
	public static final String NAME_PROPERTY = "run.name";

	/**
	 * Identifier for the bean property state of the run.
	 */
	public static final String STATE_PROPERTY = "run.state";

	/**
	 * Identifier for the bean property current segment of the run.
	 */
	public static final String CURRENT_SEGMENT_PROPERTY = "run.currentSegment";

	public static final String GOAL_PROPERTY = "run.goal";

	public static final String COUNTER_VALUE_PROPERTY = "run.counters.value";

	public static final String COUNTER_ADD_PROPERTY = "run.counters.add";

	public static final String COUNTER_REMOVE_PROPERTY = "run.counters.remove";

	public static final String COUNTER_EDIT_PROPERTY = "run.counters.edit";

	// ------------------------------------------------------------- ATTRIBUTES

	/**
	 * The name of the run.
	 */
	private String name;

	/**
	 * Current state of the run. Transient as a deserialized run will always
	 * start in {@link State#READY}.
	 */
	private transient State state;

	/**
	 * Backup copy of the run’s state. Transient as it is only used during
	 * editing of the run.
	 */
	private transient State stateBackup;

	/**
	 * List of all the segments contained within this run.
	 */
	private List<Segment> segments;

	/**
	 * Backup copy of the segments’ list. Necessary to buffer edits from
	 * {@code JTable}s and revert to the original state in case the user
	 * cancels.
	 */
	private transient List<Segment> segmentsBackup;

	/**
	 * Index of the segment being currently run. Only represents a segment when
	 * the run is {@link State#ONGOING}.
	 */
	private transient int current;

	/**
	 * Number of milliseconds on the clock when the run started.
	 */
	private transient long startTime;

	/**
	 * Delegate handling {@code PropertyChangeEvent}s.
	 */
	private transient PropertyChangeSupport pcSupport;

	/**
	 * Delegate handling {@code TableModelEvent}s.
	 */
	private transient TableModelSupport tmSupport;

	private String goal;

	private boolean segmented;

	private List<Counters> counters;

	private Configuration configuration;

	// ----------------------------------------------------------- CONSTRUCTORS

	/**
	 * Creates a new run of given name. This run has no segments and so begins
	 * in the state {@link State#NULL}.
	 *
	 * @param   name    - the name of the run
	 */
	public Run(String name) {
		if (name == null) {
			throw new NullPointerException("null run name");
		}
		this.name           = name;
		segments            = new ArrayList<Segment>();
		goal                = "";
		segmented           = false;
		counters            = new ArrayList<Counters>();
		initializeTransients();
	}

	/**
	 * Creates a default run without any segments and a default placeholder
	 * run name. This new run starts in the state {@code State.NULL}.
	 */
	public Run() {
		this("" + Language.UNTITLED);
	}

	// ---------------------------------------------------------------- GETTERS

	/**
	 * Returns the name of the run.
	 *
	 * @return  the name of the run.
	 */
	public String getName() {
		return name;
	}

	public String getGoal() {
		return goal;
	}

	public boolean isSegmented() {
		return segmented;
	}

	/**
	 * Returns the current state of the run
	 *
	 * @return  the current state of the run.
	 */
	public State getState() {
		return state;
	}

	/**
	 * Returns the number of milliseconds on the clock when the run started.
	 *
	 * @return  the run’s start time stamp.
	 */
	public long getStartTime() {
		return startTime;
	}

	public Counters getCounter(int index) {
		if (index < 0 || index >= MAX_COUNTERS) {
			throw new IllegalArgumentException("illegal counter id " + index);
		}
		return counters.get(index);
	}

	/**
	 * Returns the index of the segment being currently run. Only represents a
	 * segment when the run is {@link State#ONGOING}.
	 *
	 * @return  the current segment index.
	 */
	public int getCurrent() {
		return current;
	}

	/**
	 * Returns the index of the previously runned segment. Only represents a
	 * segment if {@link #hasPreviousSegment()}.
	 *
	 * @return  the previous segment index.
	 */
	public int getPrevious() {
		return current - 1;
	}

	/**
	 * Indicates wether or not a previous segment is available. This is the case
	 * if and only if the run is {@link State#ONGOING} or {@link State#STOPPED}
	 * and we currently are on the second segment of farther down the run.
	 *
	 * @return  wether a previous segment is available or not.
	 */
	public boolean hasPreviousSegment() {
		return current > 0;
	}

	/**
	 * Returns the segment of given index. The index must be within the range
	 * {@code [0..getSegmentCount()[}.
	 *
	 * @param   segmentIndex    - the index of the segment to return.
	 * @return  the segment of given index.
	 */
	public Segment getSegment(int segmentIndex) {
		return segments.get(segmentIndex);
	}

	/**
	 * Returns the run time of given type up to the given segment. Such a time
	 * can be {@code null} if the last segment has an undefined time.
	 *
	 * @param   segmentIndex    - index of the segment up to which get the time.
	 * @param   type            - one of the identifier.
	 * @return  the run time up to the given segment
	 * @see     Segment#getTime(int)
	 */
	public Time getTime(int segmentIndex, int type) {
		return getTime(segmentIndex, type, true);
	}

	/**
	 * Returns the run time of given type. Such a time can be {@code null} if
	 * the last segment has an undefined time.
	 *
	 * @param   type    - one of the identifier.
	 * @return  the run time of given type.
	 * @see     Segment#getTime(int)
	 */
	public Time getTime(int type) {
		return getTime(getRowCount() - 1, type);
	}

	public Time getTime(int segmentIndex, int type, boolean allowNull) {
		if (segmentIndex < 0 || segmentIndex >= getRowCount()) {
			return null;
		}
		if (type == Segment.DELTA) {
			Time set  = getTime(segmentIndex, Segment.SET);
			Time live = getTime(segmentIndex, Segment.LIVE);
			return (set == null ? null : Time.getDelta(live, set));
		}
		Time runTime = new Time();
		for (int i = 0; i <= segmentIndex; i++) {
			runTime.add(segments.get(i).getTime(type));
		}
		Time lastSegmentTime = segments.get(segmentIndex).getTime(type);
		return allowNull ? (lastSegmentTime == null ? null : runTime) : runTime;
	}

	/**
	 * Returns a portion of the registered run time to which live times should
	 * be compared. This is equal to {@link Settings#COMPARE_PERCENT} percent of
	 * the whole run time. This allows for more visually detailed comparison.
	 *
	 * @return  {@code P%} of {@code getTime(Segment.SET)} where {@code P} is
	 *          the configured compare percent.
	 */
	public Time getCompareTime() {
		int  i    = 2;
		Time time = getTime(Segment.SET);
		while (time == null) {
			if (getRowCount() - i < 0) {
				return Time.ZERO;
			}
			time = getTime(getRowCount() - i, Segment.SET);
			i++;
		}
		long ms = time.getMilliseconds();
		float pc = Settings.graphScale.get();

		return new Time((long) (ms * pc) / 100L);
	}

	/**
	 * Returns the maximum height in pixels of the icons assigned to the
	 * segments of this run. Since icons are scaled down proportionnaly, the
	 * exact height can be lower than the configured icon size (if they are
	 * wider than high.)
	 *
	 * @return  the exact maximum height of the icons of this run's segments.
	 */
	public int getMaxIconHeight() {
		int max = 0;
		for (Segment segment : segments) {
			Icon icon = segment.getIcon();
			if (icon != null) {
				max = Math.max(max, icon.getIconHeight());
			}
		}
		return max;
	}

	/**
	 * Indicates wether the live run is better than the registered one or not,
	 * i.e. if we've established a new personal best. Cannot be {@code true} if
	 * the run hasn't reached the last segment.
	 *
	 * @return  wether or not this run is a new personal best.
	 */
	public boolean isPersonalBest() {
		if (current < getRowCount()) {
			return false;
		}
		Time live = getTime(Segment.LIVE);
		Time run  = getTime(Segment.RUN);
		return (live.compareTo(run) < 0);
	}

	/**
	 * Indicates wehter or not any live segments time are better than their best
	 * registered time. Any time is always better than an undefined time.
	 *
	 * @return  wether or not this run has new segments' best.
	 */
	public boolean hasSegmentsBest() {
		for (int i = 0; i < current; i++) {
			Segment segment = segments.get(i);
			Time    live    = segment.getTime(Segment.LIVE);
			Time    best    = segment.getTime(Segment.BEST);

			if (live != null && live.compareTo(best) < 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns wether of not the given segment live time is better than its
	 * registered time. If that time is undefined, we check using the sum of
	 * the live time from the previous non-null segment.
	 *
	 * @param   index   - the index of the segment.
	 * @return  wether or not this segment live time is better.
	 */
	public boolean isBetterSegment(int index) {
		Segment segment = getSegment(index);
		Time    set     = segment.getTime(Segment.SET);
		Time    live    = segment.getTime(Segment.LIVE);

		if (live == null) {
			return false;
		}
		live = live.clone();

		if (set != null) {
			set = set.clone();
		}

		if (index > 0) {
			for (int i = index - 1; i >= 0; i--) {
				Segment prev = getSegment(i);
				if (prev.getTime(Segment.SET) == null) {
					live.add(prev.getTime(Segment.LIVE));
				} else {
					break;
				}
			}
			for (int i = index - 1; i >= 0; i--) {
				Segment prev = getSegment(i);
				if (prev.getTime(Segment.LIVE) == null) {
					set.add(prev.getTime(Segment.SET));
				} else {
					break;
				}
			}
		}
		return live.compareTo(set) < 0;
	}

	public boolean isBestSegment(int index) {
		Segment segment = getSegment(index);
		Time    set     = segment.getTime(Segment.BEST);
		Time    live    = segment.getTime(Segment.LIVE);

		if (live == null) {
			return false;
		}
		live = live.clone();

		if (set != null) {
			set = set.clone();
		}

		if (index > 0) {
			for (int i = index - 1; i >= 0; i--) {
				Segment prev = getSegment(i);
				if (prev.getTime(Segment.BEST) == null) {
					live.add(prev.getTime(Segment.LIVE));
				} else {
					break;
				}
			}
			for (int i = index - 1; i >= 0; i--) {
				Segment prev = getSegment(i);
				if (prev.getTime(Segment.LIVE) == null) {
					set.add(prev.getTime(Segment.BEST));
				} else {
					break;
				}
			}
		}
		return live.compareTo(set) < 0;
	}

	// ------------------------------------------------------ INHERITED GETTERS

	/**
	 * As specified by {@code TableModel}.
	 */
	public int getColumnCount() {
		return COLUMN_COUNT;
	}

	/**
	 * As specified by {@code TableModel}.
	 * Equivalent to the number of segments in the run.
	 */
	public int getRowCount() {
		return segments.size();
	}

	/**
	 * As specified by {@code TableModel}. Always yield {@code true} as every
	 * information of every segment is editable.
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	/**
	 * As specified by {@code TableModel}.
	 */
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
			case COLUMN_ICON:    return Icon.class;
			case COLUMN_NAME:    return String.class;
			case COLUMN_TIME:    return Time.class;
			case COLUMN_BEST:    return Time.class;
			case COLUMN_SEGMENT: return Time.class;
		}
		// Should not be reached.
		return null;
	}

	/**
	 * As specified by {@code TableModel}.
	 */
	public String getColumnName(int column) {
		switch (column) {
			case COLUMN_ICON:    return "" + Language.ICON;
			case COLUMN_NAME:    return "" + Language.NAME;
			case COLUMN_TIME:    return "" + Language.TIME;
			case COLUMN_BEST:    return "" + Language.BEST;
			case COLUMN_SEGMENT: return "" + Language.SEGMENT;
		}
		// Should not be reached.
		return null;
	}

	/**
	 * As specified by {@code TableModel}.
	 */
	public Object getValueAt(int row, int column) {
		switch (column) {
			case COLUMN_ICON:    return getSegment(row).getIcon();
			case COLUMN_NAME:    return getSegment(row).getName();
			case COLUMN_TIME:    return getTime(row, Segment.RUN);
			case COLUMN_BEST:    return getSegment(row).getTime(Segment.BEST);
			case COLUMN_SEGMENT: return getSegment(row).getTime(Segment.RUN);
		}
		// Should not be reached.
		return null;
	}

	// ---------------------------------------------------------------- SETTERS

	public<T> T getSetting( String key ) {
		return configuration.get(key);
	}

	public void putSetting( String key, Object value ) {
		configuration.put( key, value );
	}

	public void addSettingChangeListener( PropertyChangeListener pcl ) {
		configuration.addPropertyChangeListener( pcl );
	}

	public boolean containsSetting( String key ) {
		return configuration.contains( key );
	}

	/**
	 * Sets the name of this run to the given string.
	 *
	 * @param   name    - the new name of the run.
	 */
	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException("null run name");
		}
		String old = this.name;
		this.name  = name;
		pcSupport.firePropertyChange(NAME_PROPERTY, old, name);
	}

	public void setSegmented(boolean segmented) {
		this.segmented = segmented;
	}

	public void setGoal(String goal) {
		if (goal == null) {
			throw new NullPointerException("null goal string");
		}
		String old = this.goal;
		this.goal  = goal;
		pcSupport.firePropertyChange(GOAL_PROPERTY, old, goal);
	}

	/**
	 * Inserts the given segment at the end. If it's the first segment being
	 * added, the run becomes {@link State#READY}, meaning it can be started.
	 *
	 * @param   segment - the segment to add to this run.
	 */
	public void addSegment(Segment segment) {
		if (segment == null) {
			throw new NullPointerException("null segment");
		}
		int oldCount = getRowCount();
		segments.add(segment);
		tmSupport.fireTableRowsInserted(oldCount, oldCount);

		if (oldCount == 0) {
			state = State.READY;
			pcSupport.firePropertyChange(STATE_PROPERTY, State.NULL, state);
		}
	}

	/**
	 * Removes the segment of given index from the this run table of segments.
	 * If we remove the last segment, the run becomes {@link State#NULL}.
	 *
	 * @param   segmentIndex    - the index of the segment to remove.
	 */
	public void removeSegment(int segmentIndex) {
		setValueAt(null, segmentIndex, 2);
		segments.remove(segmentIndex);
		tmSupport.fireTableRowsDeleted(segmentIndex, segmentIndex);

		if (getRowCount() == 0) {
			State old = state;
			state     = State.NULL;
			pcSupport.firePropertyChange(STATE_PROPERTY, old, state);
		}
	}

	/**
	 * Removes the segment of given index and inserts it back one position
	 * lower. The segment effectively moves one position upward.
	 *
	 * @param   segmentIndex    - the index of the segment to move.
	 */
	public void moveSegmentUp(int segmentIndex) {
		if (segmentIndex > 0) {
			Segment segment = segments.get(segmentIndex);
			segments.remove(segmentIndex);
			segments.add(segmentIndex - 1, segment);
			tmSupport.fireTableStructureChanged();
		}
	}

	/**
	 * Removes the segment of given index and inserts it back one position
	 * higher. The segment effectively moves one position downward.
	 *
	 * @param   segmentIndex    - the index of the segment to move.
	 */
	public void moveSegmentDown(int segmentIndex) {
		if (segmentIndex < getRowCount() - 1) {
			Segment segment = segments.get(segmentIndex);
			segments.remove(segmentIndex);
			segments.add(segmentIndex + 1, segment);
			tmSupport.fireTableStructureChanged();
		}
	}

	/**
	 * Starts the race. The clock time is saved as the run start time and the
	 * current segment becomes the first segment of the run.
	 *
	 * @throws  IllegalStateException   if the run is on-going or null.
	 */
	public void start() {
		if (state == null || state == State.ONGOING) {
			throw new IllegalStateException("illegal state to start");
		}
		startTime = System.nanoTime() / 1000000L;
		current   = 0;
		state     = State.ONGOING;
		segments.get(current).setStartTime(startTime);

		pcSupport.firePropertyChange(STATE_PROPERTY, State.READY, state);
		pcSupport.firePropertyChange(CURRENT_SEGMENT_PROPERTY, -1, 0);
	}

	/**
	 * Makes a split, saving the elapsed time for the current segment and
	 * setting the next segment as current. If we were at the last segment,
	 * the run becomes {@link State#STOPPED}.
	 *
	 * @throws  IllegalStateException   if the run is not on-going.
	 */
	public void split() {
		if (state != State.ONGOING) {
			throw new IllegalStateException("run is not on-going");
		}
		long stopTime    = System.nanoTime() / 1000000L;
		long segmentTime = stopTime - getSegment(current).getStartTime();
		current          = current + 1;

		Time time        = new Time(segmentTime);
		segments.get(current - 1).setTime(time, Segment.LIVE);

		if (current == getRowCount()) {
			stop();
		} else {
			segments.get(current).setStartTime(stopTime);
		}
		pcSupport.firePropertyChange(
				CURRENT_SEGMENT_PROPERTY, current - 1, current);
		if (segmented && state == State.ONGOING && current > -1) {
			pause();
		}
	}

	/**
	 * If a run is on-going and a split has been made, this method will cancel
	 * it, reverting to the state before said split and discarding the
	 * established live time.
	 *
	 * @throws  IllegalStateException   if the run is not on-going.
	 */
	public void unsplit() {
		if (state == State.NULL || state == State.READY) {
			throw new IllegalStateException("illegal run state");
		}
		if (current > 0) {
			current = current - 1;
			getSegment(current).setTime(null, Segment.LIVE);

			pcSupport.firePropertyChange(
					CURRENT_SEGMENT_PROPERTY, current + 1, current);

			if (state == State.STOPPED) {
				state = State.ONGOING;
				pcSupport.firePropertyChange(
						STATE_PROPERTY, State.STOPPED, state);
			}
		}
	}

	public void pause() {
		if (state != State.ONGOING) {
			throw new IllegalStateException("run is not on-going");
		}
		state = State.PAUSED;
		long stopTime    = System.nanoTime() / 1000000L;
		long segmentTime = stopTime - getSegment(current).getStartTime();
		Time time        = new Time(segmentTime);
		segments.get(current).setTime(time, Segment.LIVE, true);
		pcSupport.firePropertyChange(STATE_PROPERTY, State.ONGOING, state);
	}

	public void resume() {
		if (state != State.PAUSED) {
			throw new IllegalStateException("run is not paused");
		}
		state     = State.ONGOING;
		long stop = System.nanoTime() / 1000000L;
		startTime = stop - getTime(current, Segment.LIVE, false).getMilliseconds();

		Segment crt = getSegment(current);
		crt.setStartTime(stop - crt.getTime(Segment.LIVE).getMilliseconds());

		long cumulative = 0L;
		for (int i = 0; i < current; i++) {
			Segment iSeg = getSegment(i);
			iSeg.setStartTime(startTime + cumulative);
			cumulative += iSeg.getTime(Segment.LIVE).getMilliseconds();
		}
		pcSupport.firePropertyChange(STATE_PROPERTY, State.PAUSED, state);
	}

	/**
	 * Stops the current on-going run.
	 *
	 * @throws  IllegalStateException   if the is not on-going.
	 */
	public void stop() {
		if (state != State.ONGOING) {
			throw new IllegalStateException("run is not on-going");
		}
		state     = State.STOPPED;
		pcSupport.firePropertyChange(STATE_PROPERTY, State.ONGOING, state);
	}

	/**
	 * Resets the current run, discarding any live times and becoming once
	 * again {@link State#READY}.
	 */
	public void reset() {
		for (Segment segment : segments) {
			segment.setTime(null, Segment.LIVE);
		}
		current   = -1;
		startTime = 0L;

		State old = state;
		state     = State.READY;
		pcSupport.firePropertyChange(STATE_PROPERTY, old, state);
	}

	/**
	 * Skips the current segment, setting its live segment time as undefined
	 * and moving to the next segment. Since it is not possible to skip the
	 * last segment, a next one always exist. The next segment time will be
	 * defined as the delta between the start of the previous non-null segment
	 * and the split time of this segment.
	 */
	public void skip() {
		if (current > - 1 && current < getRowCount() - 1) {
			Segment crtSegment = getSegment(current);
			long  segmentStart = crtSegment.getStartTime();
			crtSegment.setTime(null, Segment.LIVE);

			current = current + 1;
			getSegment(current).setStartTime(segmentStart);

			pcSupport.firePropertyChange(
					CURRENT_SEGMENT_PROPERTY, current - 1, current);
		}
	}

	/**
	 * Overwrites the registered with the live times, using the following
	 * algorithm: if a live segment time is better than its registered best
	 * time, the best time is overwritten. If we are not doing a {@code partial}
	 * save and the run is complete (its end was naturally reached), the
	 * registered segment time is overwritten with the live segment time.
	 *
	 * @param   partial - wether to only save best times or the whole run.
	 */
	public void saveLiveTimes(boolean partial) {
		boolean over = (current == getRowCount());
		for (Segment segment : segments) {
			Time live = segment.getTime(Segment.LIVE);
			if (live == null) {
				if (!partial && over) {
					segment.setTime(null, Segment.RUN);
				}
			} else {
				if (live.compareTo(segment.getTime(Segment.BEST)) < 0) {
					segment.setTime(live, Segment.BEST);
				}
				if (!partial && over) {
					segment.setTime(live, Segment.RUN);
				}
			}
		}
	}

	/**
	 * Creates a backup copy of this run table of segments in order to be able
	 * to revert any changes made by the user using direct-editing components.
	 */
	public void saveBackup() {
		stateBackup    = state;
		segmentsBackup = new ArrayList<Segment>();
		for (Segment segment : segments) {
			segmentsBackup.add(segment.clone());
		}
	}

	/**
	 * Reverts this run table of segments to its original state prior to the
	 * call to {@link #saveBackup()}. Does nothing if no backup has been
	 * created. After this call, the existing backup will be discarded.
	 */
	public void loadBackup() {
		if (segmentsBackup != null) {
			State old = state;
			state     = stateBackup;
			segments  = new ArrayList<Segment>();
			for (Segment segment : segmentsBackup) {
				segments.add(segment);
			}
			segmentsBackup = null;
			tmSupport.fireTableStructureChanged();
			pcSupport.firePropertyChange(STATE_PROPERTY, old, state);
		}
	}

	/**
	 * Sets the split time of the given segment. If the new value is
	 * {@code null} (meaning the time is undefined,) the segment time and best
	 * time also becomes {@code null} and the previously set segment time is
	 * added to the next non-null segment time. If it is not, the delta between
	 * the new and the old value is added to the next non-null segment and the
	 * new segment time is computed using the previous non-null segment time.
	 *
	 * @param   index   - the index of the segment to set a split time to.
	 * @param   time    - the new split time.
	 */
	public void setSplitTime(int index, Time time) {
		Time oldTime = getTime(index, Segment.RUN);

		if (time == null) {
			setSegmentTime(index, null);
		} else {
			// Compute the time added to/removed from the segment.
			if (oldTime == null) {
				if (index > 0) {
					oldTime = getTime(index - 1, Segment.RUN);
				}
			}
			Time delta   = Time.getDelta(oldTime, time);
			Time pTime = new Time();

			// Find the first previous non-null segment.
			for (int i = index - 1; i >= 0; i--) {
				if (getSegment(i).getTime(Segment.RUN) != null) {
					pTime = getTime(i, Segment.RUN);
					break;
				}
			}
			// If a next non-null segment exist possess a split time
			// inferior to the split time we are defining, we add
			// the delta to preserve consistency.
			for (int i = index + 1; i < getRowCount(); i++) {
				Segment nSegment = getSegment(i);
				if (nSegment.getTime(Segment.RUN) != null) {
					Time nTime = getTime(i, Segment.RUN);
					if (time.compareTo(nTime) < 0
							&& time.compareTo(pTime) > 0) {
						nSegment.getTime(Segment.RUN).add(delta);
					}
					break;
				}
			}
			time            = Time.getDelta(time, pTime);
			Segment segment = getSegment(index);
			segment.setTime(time, Segment.RUN);

			if (time.compareTo(segment.getTime(Segment.BEST)) < 0) {
				segment.setTime(time, Segment.BEST);
			}
		}
	}

	/**
	 * Registers the given listener as a new {@code PropertyChangeListener} for
	 * this run.
	 *
	 * @param   pcl - the listener to register.
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		pcSupport.addPropertyChangeListener(pcl);
	}

	/**
	 * Removes the given listener from the list of registered
	 * {@code PropertyChangeListener} of this run.
	 *
	 * @param   pcl - the listener to register.
	 */
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		pcSupport.removePropertyChangeListener(pcl);
	}

	// ------------------------------------------------------ INHERITED SETTERS

	/**
	 * Registers the given listener as a new {@code TableModelListener} for
	 * this run table of segments.
	 *
	 * @param   tml - the listener to register.
	 */
	public void addTableModelListener(TableModelListener tml) {
		tmSupport.addTableModelListener(tml);
	}

	/**
	 * Removes the given listener from the list of registered
	 * {@code TableModelListener} of this run.
	 *
	 * @param   tml - the listener to register.
	 */
	public void removeTableModelListener(TableModelListener tml) {
		tmSupport.removeTableModelListener(tml);
	}

	/**
	 * As specified by {@code TableModel}. Sets the value of given column for
	 * the segment of given row index.
	 */
	public void setValueAt(Object value, int row, int column) {
		Segment segment = getSegment(row);
		switch (column) {
			case COLUMN_ICON:
				segment.setIcon((ImageIcon) value);
				tmSupport.fireTableCellUpdated(row, column);
				break;

			case COLUMN_NAME:
				segment.setName((String) value);
				tmSupport.fireTableCellUpdated(row, column);
				break;

			case COLUMN_TIME:
				Time newTime = (Time) value;
				setSplitTime(row, newTime);
				tmSupport.fireTableDataChanged();
				break;

			case COLUMN_SEGMENT:
				setSegmentTime(row, (Time) value);
				tmSupport.fireTableDataChanged();
				break;

			case COLUMN_BEST:
				newTime      = (Time) value;
				Time runTime = segment.getTime(Segment.RUN);
				if (newTime == null || newTime.compareTo(runTime) > 0) {
					segment.setTime(runTime, Segment.BEST);
				} else {
					segment.setTime(newTime, Segment.BEST);
				}
				tmSupport.fireTableDataChanged();
				break;
		}
	}

	// -------------------------------------------------------------- UTILITIES

	/**
	 * Sets the segment time of the given segment. If the segment time is
	 * better than the best time, the best time is updated accordingly.
	 *
	 * @param   index   - the index of the segment to set a split time to.
	 * @param   time    - the new split time.
	 */
	private void setSegmentTime(int index, Time time) {
		if (time != null && time.compareTo(Time.ZERO) <= 0) {
			throw new IllegalArgumentException("" + Language.ILLEGAL_TIME);
		}
		Segment segment  = segments.get(index);
		Time    best     = segment.getTime(Segment.BEST);
		Time    old      = segment.getTime(Segment.RUN);
		int     rowCount = getRowCount();

		if (time == null) {
			// The old segment time of the newly undefined segment goes
			// to the next non-null segment to preserve split times
			// consistency.
			for (int i = index + 1; i < rowCount; i++) {
				Time nTime = getSegment(i).getTime(Segment.RUN);
				if (nTime != null) {
					nTime.add(old);
					break;
				}
			}

		} else if (old == null) {
			for (int i = index + 1; i < rowCount; i++) {
				Segment nSeg  = getSegment(i);
				Time    nTime = nSeg.getTime(Segment.RUN);
				if (nTime != null) {
					if (nTime.compareTo(time) <= 0) {
						throw new IllegalArgumentException(
								"" + Language.ILLEGAL_SEGMENT_TIME);
					}
					nSeg.setTime(Time.getDelta(nTime, time), Segment.RUN);
					break;
				}
			}

		}
		segment.setTime(time, Segment.RUN);

		if (time != null) {
			if (time.compareTo(best) < 0) {
				segment.setTime(time, Segment.BEST);
			}
		}
	}

	/**
	 * Initialize all transient fields.
	 */
	private void initializeTransients() {
		pcSupport      = new PropertyChangeSupport(this);
		tmSupport      = new TableModelSupport(this);
		segmentsBackup = null;
		stateBackup    = null;
		state          = getRowCount() > 0 ? State.READY : State.NULL;
		current        = -1;
		startTime      = 0L;

		if (goal == null) {
			goal = "";
		}
		if (counters == null) {
			counters = new ArrayList<Counters>();
		}
		if ( configuration == null ) {
			configuration = new Configuration();
		}
	}

	/**
	 * Deserialization process. Redefined to initialize transients fields upon
	 * deserialization.
	 */
	private void readObject(ObjectInputStream in)
									throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		initializeTransients();
	}

	// ---------------------------------------------------------- INTERNAL TYPE

	/**
	 * Enumeration of a run possible state.
	 *
	 * @author  Xavier "Xunkar" Sencert
	 */
	public enum State {

		// ---------------------------------------------------- ENUMERATES

		/**
		 * The run cannot start, probably because there are no segments.
		 */
		NULL,

		/**
		 * The run is ready to start. This state should be achieved upon
		 * opening a run and resetting it.
		 */
		READY,

		/**
		 * The run is being runned. A current segment now exists.
		 */
		ONGOING,

		/**
		 * The run was stopped, either naturally by making the last split or
		 * voluntarily by the user.
		 */
		STOPPED,

		/**
		 * The run was paused by the user. The timer should not be running
		 * while a run is paused.
		 */
		PAUSED;

		// ----------------------------------------------------- CONSTANTS

		/**
		 * The serial version identifier used to determine the compatibility of
		 * the different serialized versions of this type. This identifier must
		 * change when modifications that break backward-compatibility are made
		 * to the type.
		 */
		private static final long serialVersionUID = 1000L;
	}

}
