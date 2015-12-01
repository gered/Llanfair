package org.fenix.llanfair;

import org.fenix.llanfair.config.Settings;
import org.fenix.utils.Images;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Represents a portion of a run. As such, a segment is associated to a 
 * registered time as well as a best time and a live time set on the fly by
 * the run owning the segment.
 *
 * @author  Xavier "Xunkar" Sencert
 */
public class Segment implements Cloneable, Serializable {

	// -------------------------------------------------------------- CONSTANTS

	/**
	 * The serial version identifier used to determine the compatibility of the
	 * different serialized versions of this type. This identifier must change
	 * when modifications that break backward-compatibility are made to the
	 * type.
	 */
	private static final long serialVersionUID = 1001L;

	/**
	 * Array of legit display sizes for the segments’ icons.
	 */
	public static final Integer[] ICON_SIZES = new Integer[] {
			16, 24, 32, 40, 48, 56, 64
	};

	/**
	 * Maximum size allowed for the segment’s icons. When setting an icon for
	 * a segment, it will be scaled down to that size if it’s bigger, but will
	 * not be scaled up if it’s smaller.
	 */
	public static final int ICON_MAX_SIZE = ICON_SIZES[ICON_SIZES.length - 1];

	/**
	 * Identifier for the time of the segment as defined by the currently set
	 * compare method.
	 */
	public static final int SET = 0;

	/**
	 * Identifier for the registered time of the segment.
	 */
	public static final int RUN = 1;

	/**
	 * Identifier for the best ever registered time of the segment.
	 */
	public static final int BEST = 2;

	/**
	 * Identifier for the live time realized on this segment.
	 */
	public static final int LIVE = 3;

	/**
	 * Identifier for the delta between the live time and the time as defined
	 * by the currently set compare method, i.e. {@code LIVE - SET}.
	 */
	public static final int DELTA = 4;

	/**
	 * Identifier for the delta between the live time and the registered time
	 * of the segment, i.e. {@code LIVE - RUN}.
	 */
	public static final int DELTA_RUN = 5;

	/**
	 * Identifier for the delta between the live time and the best ever
	 * registered time of the segment, i.e. {@code LIVE - BEST}.
	 */
	public static final int DELTA_BEST = 6;

	// ------------------------------------------------------------- ATTRIBUTES

	/**
	 * Name of the segment.
	 */
	private String name;

	/**
	 * Icon associated with this segment. Can be {@code null} if no icon is to
	 * be displayed.
	 */
	private ImageIcon icon;

	/**
	 * Registered time for this segment during the best run.
	 */
	private Time runTime;

	/**
	 * Best time ever registered for this segment.
	 */
	private Time bestTime;

	/**
	 * Live time realized on this segment during a run. This value is never
	 * saved as is but can overwrite {@code runTime} or {@code bestTime}.
	 */
	private transient Time liveTime;

	/**
	 * Number of milliseconds on the clock when the segment started.
	 */
	private transient long startTime;

	// ----------------------------------------------------------- CONSTRUCTORS

	/**
	 * Creates a new segment of given name and undefined times.
	 *
	 * @param   name    - the name of the segment.
	 */
	public Segment(String name) {
		if (name == null) {
			throw new NullPointerException("null segment name");
		}
		this.name  = name;
		icon       = null;
		runTime    = null;
		bestTime   = null;
		initializeTransients();
	}

	/**
	 * Creates a default segment with a default name and undefined times.
	 */
	public Segment() {
		this("" + Language.UNTITLED);
	}

	// ---------------------------------------------------------------- GETTERS

	/**
	 * Returns the name of the segment.
	 *
	 * @return  the name of the segment.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the icon associated with this segment. Can be {@code null}.
	 *
	 * @return  the icon of the segment or {@code null}.
	 */
	public ImageIcon getIcon() {
		return icon;
	}

	/**
	 * Returns the number of milliseconds on the clock when the segment started.
	 *
	 * @return  the start time of this segment.
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Returns the given type of time for this segment.
	 *
	 * @param   type    - one of the type identifier.
	 * @return  the segment time of given type.
	 */
	public Time getTime(int type) {
		switch (type) {
			case BEST:
				return bestTime;

			case LIVE:
				return liveTime;

			case RUN:
				return runTime;

			case DELTA_RUN:
				if (runTime == null) {
					return null;
				}
				return Time.getDelta(liveTime, runTime);

			case DELTA_BEST:
				if (bestTime == null) {
					return null;
				}
				return Time.getDelta(liveTime, bestTime);

			case DELTA:
				Time   time =  getTime();
				return (time == null ? null : Time.getDelta(liveTime, time));

			default:
				return getTime();
		}
	}

	/**
	 * As specified by {@code Cloneable}, returns a deep copy of the segment.
	 */
	public Segment clone() {
		Segment segment   = new Segment(name);
		segment.icon      = icon;
		segment.runTime   = (runTime  == null ? null : runTime.clone());
		segment.bestTime  = (bestTime == null ? null : bestTime.clone());
		segment.liveTime  = (liveTime == null ? null : liveTime.clone());
		segment.startTime = startTime;
		return segment;
	}

	// ---------------------------------------------------------------- SETTERS

	/**
	 * Sets the name of the segment to the given string.
	 *
	 * @param   name    - the new name of the segment.
	 */
	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException("null name");
		}
		this.name = name;
	}

	/**
	 * Sets the current icon of this segment. Can be {@code null} to remove
	 * the current icon or indicate that no icon should be used. The icon wil
	 * be scale down to {@code ICON_MAX_SIZE} if it’s bigger.
	 *
	 * @param   icon    - the new icon for this segment.
	 */
	public void setIcon(ImageIcon icon) {
		if (icon == null) {
			this.icon = null;
		} else {
			this.icon = Images.rescale(icon, ICON_MAX_SIZE);
		}
	}

	/**
	 * Sets the number of milliseconds on the clock when the segment started.
	 * Should only be called by the run owning this segment.
	 *
	 * @param   startTime   - the starting time of this segment.
	 * @throws  IllegalArgumentException    if the time is negative.
	 */
	void setStartTime(long startTime) {
		if (startTime < 0L) {
			throw new IllegalArgumentException("negative start time");
		}
		this.startTime = startTime;
	}

	/**
	 * Sets the given type of time to the new value. Note that some type of
	 * times cannot be set (such as {@code DELTA}s.) The new value can be
	 * {@code null} to indicate undefined times.
	 *
	 * @param   time    - the new time value for the given type.
	 * @param   type    - one of the type identifier.
	 * @throws  IllegalArgumentException if the new time value is lower than or
	 *          equal to zero.
	 */
	public void setTime(Time time, int type, boolean bypass) {
		if (!bypass) {
			if (time != null && time.compareTo(Time.ZERO) <= 0) {
				throw new IllegalArgumentException("" + Language.ILLEGAL_TIME);
			}
		}
		switch (type) {
			case BEST:  bestTime = time;    break;
			case LIVE:  liveTime = time;    break;
			case RUN:   runTime  = time;    break;
		}
	}

	public void setTime(Time time, int type) {
		setTime(time, type, false);
	}

	// -------------------------------------------------------------- UTILITIES

	/**
	 * Initialize all transient fields.
	 */
	private void initializeTransients() {
		liveTime  = null;
		startTime = 0L;
	}

	/**
	 * Returns the time of this segment as specified by the currently set
	 * compare method.
	 *
	 * @return  the time as defined by the current compare method.
	 */
	private Time getTime() {
		switch (Settings.compareMethod.get()) {
			case BEST_OVERALL_RUN:       return runTime;
			case SUM_OF_BEST_SEGMENTS:  return bestTime;
		}
		// Should not be reached.
		return null;
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

}
