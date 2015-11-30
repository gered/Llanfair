package org.fenix.llanfair;

import java.io.Serializable;
import org.fenix.llanfair.Language;
import org.fenix.llanfair.config.Accuracy;
import static org.fenix.llanfair.config.Accuracy.HUNDREDTH;
import static org.fenix.llanfair.config.Accuracy.SECONDS;
import static org.fenix.llanfair.config.Accuracy.TENTH;
import org.fenix.llanfair.config.Settings;

/**
 * Represents independent time values. Times can be compared between each other
 * or to zero using the constant {@link Time.ZERO}. Times can also be displayed
 * as a user-friendly string according to a given accuracy. Accuracy is a
 * float statically set for every Time objects but that can also be passed as a 
 * parameter with {@code toString()}.
 * 
 * There exists two ways of representing a time: the standard format (H:M:S) or 
 * the delta format (+/-H:M:S) if the time object represents a delta between two
 * times. 
 *
 * @author Xavier "Xunkar" Sencert
 * @version 1.1
 */
public class Time implements Cloneable, Comparable<Time>, Serializable {

	/**
	 * A time of zero milliseconds. This constant can be used for comparisons
	 * with other times and nothing else.
	 */
	public static final Time ZERO = new Time();

	private static final long serialVersionUID = 1000L;

	private long milliseconds;

	/**
	 * Creates a default time of zero milliseconds.
	 */
	public Time() {
		milliseconds = 0L;
	}

	/**
	 * Creates a time representing a given number of milliseconds. This number
	 * is truncated to the milliseconds to prevent discrepencies resulting from
	 * successive calls to {@link System.currentTimeMillis()} and thus ensures
	 * that the compare method remains accurate.
	 *
	 * @param ms the number of milliseconds to represent
	 */
	public Time(long ms) {
		milliseconds = (ms / 10L) * 10L;
	}

	/**
	 * Creates a time representing the given decimal number of seconds. This
	 * number is truncated to the milliseconds to prevent discrepencies
	 * resulting from successive calls to {@link System.currentTimeMillis()}
	 * and thus ensures that the compare method remains accurate.
	 *
	 * @param seconds the number of seconds to represent
	 */
	public Time(double seconds) {
		this((long) (seconds * 1000.0));
	}

	/**
	 * Creates a time representing the given time-stamp. The time-stamp is a
	 * string describing the time in a format understandable by the user. The
	 * time-stamp must be formatted according to the following:
	 *
	 * <pre>((H)H:)((M)M:)(S)S(.T(H(M)))</pre>
	 *
	 * Where a letter represents a digit and parenthesis indicate optionality.
	 * H stands for hours, M for minutes, S for seconds, T for tenths, H for
	 * hundredth and M for milliseconds.
	 *
	 * @param timestamp a string representation of the time to parse
	 * @throws IllegalArgumentException if the timestamp cannot be parsed
	 */
	public Time(String timeStamp) {
		try {
			milliseconds = parseTimeStamp(timeStamp);
		} catch (Exception ex) {
			throw new IllegalArgumentException(
					Language.INVALID_TIME_STAMP.get(timeStamp)
			);
		}
	}

	/**
	 * Returns the delta of time between two times. The returned time is
	 * equivalent to, but more convenient than, the following code:
	 * {@code new Time(t1.getMilliseconds() - t2.getMilliseconds())}.
	 *
	 * @param t1 the first time
	 * @param t2 the time to substract from the first
	 * @return the delta of time between the two times
	 */
	public static Time getDelta(Time t1, Time t2) {
		if (t1 == null) {
			t1 = new Time();
		} else if (t2 == null) {
			t2 = new Time();
		}
		return new Time(t1.milliseconds - t2.milliseconds);
	}

	/**
	 * Returns the number of milliseconds represented by that time.
	 *
	 * @return the number of milliseconds represented by that time
	 */
	public long getMilliseconds() {
		return milliseconds;
	}

	/**
	 * Adds the given time to this time. The number of milliseconds
	 * represented by this time object is now equals to:
	 * {@code getMilliseconds() + time.getMilliseconds()}
	 *
	 * @param time the time object to add to this time
	 */
	public void add(Time time) {
		milliseconds += (time == null ? 0L : time.milliseconds);
	}

	public String toString(boolean signed, Accuracy accuracy) {
		if (signed) {
			return (milliseconds > 0L ? "+" : "-") + toString(false, accuracy);
		}
		long time = Math.abs(milliseconds);
		long cen = (time % 1000L) / 10L;
		long sec;

		// Round to the nearest tenth.
		if (accuracy == Accuracy.TENTH) {
			cen = Math.round((double) cen / 10L);
			if (cen == 10L) {
				cen = 0L;
				time = time + 1000L;
			}
		}

		// Round to the nearest second.
		if (accuracy == Accuracy.SECONDS) {
			sec = Math.round((double) time / 1000);
		} else {
			sec = time / 1000L;
		}
		long min = sec / 60L;
		sec      = sec % 60L;
		long hou = min / 60L;
		min      = min % 60L;

		if (hou == 0L) {
			if (min == 0L) {
				switch (accuracy) {
					case HUNDREDTH:
						return String.format("%d.%02d", sec, cen);
					case TENTH:
						return String.format("%d.%d", sec, cen);
					case SECONDS:
						return String.format("%d", sec);
				}
			}
			switch (accuracy) {
				case HUNDREDTH:
					return String.format("%d:%02d.%02d", min, sec, cen);
				case TENTH:
					return String.format("%d:%02d.%d", min, sec, cen);
				case SECONDS:
					return String.format("%d:%02d", min, sec);
			}
		}
		switch (accuracy) {
			case HUNDREDTH:
				return String.format("%d:%02d:%02d.%02d", hou, min, sec, cen);
			case TENTH:
				return String.format("%d:%02d:%02d.%d", hou, min, sec, cen);
			case SECONDS:
				return String.format("%d:%02d:%02d", hou, min, sec);
		}
		// Should not be reachable.
		return null;
	}

	/**
	 * A time represents itself as a string using the traditional format
	 * {@code H:M:S}. The global accuracy determines the presence of tenths
	 * or hundredths of a second and if the get should be rounded. Every digit
	 * of higher-order than a second is only displayed if necessary. The sign
	 * parameter determines if the string should be preceded by a plus or minus
	 * sign depending on the number of milliseconds. If not, only the absolute
	 * number is used.
	 *
	 * @param signed if this time is to be displayed as a delta of time
	 * @return a string representation of this time object
	 */
	public String toString(boolean signed) {
		return toString(signed, Settings.GNR_ACCY.get());
	}

	/**
	 * A time represents itself as a string using the traditional format
	 * {@code H:M:S}. The accuracy parameter determines the presence of tenths
	 * or hundredths of a second and if the get should be rounded. Every digit
	 * of higher-order than a second is only displayed if necessary.
	 *
	 * @param accuracy the target accuracy to display this time in
	 * @return a string representation of this time object
	 */
	public String toString(Accuracy accuracy) {
		return toString(false, accuracy);
	}

	/**
	 * A time represents itself as a string using the traditional format
	 * {@code H:M:S}. The accuracy setting determines the presence of tenths
	 * or hundredths of a second and if the get should be rounded. Every digit
	 * of higher-order than a second is only displayed if necessary.
	 *
	 * @return a string representation of this time object
	 */
	@Override public String toString() {
		return toString(false);
	}

	/**
	 * {@inheritDoc} Returns a deep-copy of this time object.
	 */
	@Override public Time clone() {
		return new Time(milliseconds);
	}

	/**
	 * {@inheritDoc} Two time objects are equal if and only if they represent
	 * the same amount of milliseconds.
	 */
	@Override public boolean equals(Object obj) {
		if (!(obj instanceof Time)) {
			return false;
		}
		Time time = (Time) obj;
		return (milliseconds == time.milliseconds);
	}

	/**
	 * {@inheritDoc} The hash code of a time object is its number of
	 * milliseconds cast into an integer get. As such, the load factor of a
	 * table storing time objects is floored by {@code Integer#MAX_VALUE}.
	 */
	@Override public int hashCode() {
		return (int) milliseconds;
	}

	/**
	 * {@inheritDoc} Time objects are compared using their amount of
	 * milliseconds
	 */
	@Override public int compareTo(Time time) {
		if (time == null) {
			return -1;
		}
		return ((Long) milliseconds).compareTo(time.milliseconds);
	}

	/**
	 * Parses a given time-stamp and converts it to a number of milliseconds.
	 *
	 * @param timestamp the timestamp to parse
	 * @return the number of milliseconds represented by the stamp
	 */
	private long parseTimeStamp(String timestamp) {
		String seconds = timestamp;
		long   millis  = 0L;

		// Hours or minutes in the stamp.
		if (timestamp.contains(":")) {
			String[] split = timestamp.split(":");
			if (split.length > 3) {
				throw new IllegalArgumentException();
			}
			// We keep the number of seconds and lower for later.
			seconds = split[split.length - 1];

			for (int i = 0; i < split.length - 1; i++) {
				long power = (long) Math.pow(60, split.length - i - 1);
				millis    += Long.valueOf(split[i]) * power * 1000L;
			}
		}
		double value = Double.parseDouble(seconds);
		return (millis + (long) (value * 1000.0));
	}

}
