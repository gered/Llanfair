package org.fenix.llanfair.config;

import org.fenix.llanfair.Language;
import org.fenix.llanfair.Run;
import org.fenix.utils.UserSettings;
import org.fenix.utils.config.Configuration;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Stores the configuration of Llanfair. This class actually provides two levels
 * of configuration. The <em>global</em> configuration is read from the file 
 * {@code llanfair.xml} stored in the working directory. By default, every 
 * property uses their global values. The <em>local</em> configuration is 
 * read from a run file and passed down by the main class. Local values have a
 * higher priority and thus are always used instead of global values if 
 * defined. This allows users to set properties on a per-run basis.
 * 
 * @author Xavier "Xunkar" Sencert
 * @version 1.3
 */
public class Settings {

	/* GENERIC properties */

	public static final Property<Boolean> alwaysOnTop = new Property<>( "alwaysOnTop" );
	public static final Property<Locale> language = new Property<>( "language" );
	public static final Property<Locale> viewerLanguage = new Property<>( "viewerLanguage" );
	public static final Property<List<String>> recentFiles = new Property<>( "recentFiles" );
	public static final Property<Point> coordinates = new Property<>( "coordinates" );
	public static final Property<Dimension> dimension = new Property<>( "dimension" );
	public static final Property<Compare> compareMethod = new Property<>( "compareMethod" );
	public static final Property<Accuracy> accuracy = new Property<>( "accuracy" );
	public static final Property<Boolean> warnOnReset = new Property<>( "warnOnReset" );
	public static final Property<Boolean> windowUserResizable = new Property<>( "windowUserResizable" );
	public static final Property<Integer> windowWidth = new Property<>( "windowWidth" );

	/* COLOR properties */

	public static final Property<Color> colorBackground = new Property<>( "color.background" );
	public static final Property<Color> colorForeground = new Property<>( "color.foreground" );
	public static final Property<Color> colorTime = new Property<>( "color.time" );
	public static final Property<Color> colorTimer = new Property<>( "color.timer" );
	public static final Property<Color> colorNegativeTime = new Property<>( "color.negativeTime" );
	public static final Property<Color> colorTimeGainedWhileAhead = new Property<>( "color.timeGainedWhileAhead" );
	public static final Property<Color> colorTimeLostWhileAhead = new Property<>( "color.timeLostWhileAhead" );
	public static final Property<Color> colorTimeGainedWhileBehind = new Property<>( "color.timeGainedWhileBehind" );
	public static final Property<Color> colorTimeLostWhileBehind = new Property<>( "color.timeLostWhileBehind" );
	public static final Property<Color> colorNewRecord = new Property<>( "color.newRecord" );
	public static final Property<Color> colorTitle = new Property<>( "color.title" );
	public static final Property<Color> colorSubTitle = new Property<>( "color.subTitle" );
	public static final Property<Color> colorHighlight = new Property<>( "color.highlight" );
	public static final Property<Color> colorSeparators = new Property<>( "color.separators" );

	/* HOTKEY properties */

	public static final Property<Boolean> useGlobalHotkeys = new Property<>("useGlobalHotkeys");
	public static final Property<Integer> hotkeySplit = new Property<>( "hotkey.split" );
	public static final Property<Integer> hotkeyUnsplit = new Property<>( "hotkey.unsplit" );
	public static final Property<Integer> hotkeySkip = new Property<>( "hotkey.skip" );
	public static final Property<Integer> hotkeyReset = new Property<>( "hotkey.reset" );
	public static final Property<Integer> hotkeyStop = new Property<>( "hotkey.stop" );
	public static final Property<Integer> hotkeyPause = new Property<>( "hotkey.pause" );
	public static final Property<Integer> hotkeyLock = new Property<>( "hotkey.lock" );

	/* HEADER properties */

	public static final Property<Boolean> headerShowSubtitle = new Property<>( "header.subTitle" );
	public static final Property<Boolean> headerShowTitle = new Property<>( "header.title" );
	public static final Property<Boolean> headerShowAttempts = new Property<>( "header.showAttempts" );
	public static final Property<Font> headerTitleFont = new Property<>( "header.titleFont" );
	public static final Property<Font> headerSubTitleFont = new Property<>( "header.subTitleFont" );

	/* HISTORY properties */

	public static final Property<Integer> historyRowCount = new Property<>( "history.rowCount" );
	public static final Property<Boolean> historyTabular = new Property<>( "history.tabular" );
	public static final Property<Boolean> historyBlankRows = new Property<>( "history.blankRows" );
	public static final Property<Boolean> historyMultiline = new Property<>( "history.multiline" );
	public static final Property<Merge> historyMerge = new Property<>( "history.merge" );
	public static final Property<Boolean> historyLiveTimes = new Property<>( "history.liveTimes" );
	public static final Property<Boolean> historyDeltas = new Property<>( "history.deltas" );
	public static final Property<Boolean> historyIcons = new Property<>( "history.icons" );
	public static final Property<Integer> historyIconSize = new Property<>( "history.iconSize" );
	public static final Property<Integer> historyOffset = new Property<>( "history.offset" );
	public static final Property<Boolean> historyAlwaysShowLast = new Property<>( "history.alwaysShowLast" );
	public static final Property<Font> historySegmentFont = new Property<>( "history.segmentFont" );
	public static final Property<Font> historyTimeFont = new Property<>( "history.timeFont" );

	/* CORE properties */

	public static final Property<Accuracy> coreAccuracy = new Property<>( "core.accuracy" );
	public static final Property<Boolean> coreShowIcons = new Property<>( "core.icons" );
	public static final Property<Integer> coreIconSize = new Property<>( "core.iconSize" );
	public static final Property<Boolean> coreShowSegmentName = new Property<>( "core.segmentName" );
	public static final Property<Boolean> coreShowSplitTime = new Property<>( "core.splitTime" );
	public static final Property<Boolean> coreShowSegmentTime = new Property<>( "core.segmentTime" );
	public static final Property<Boolean> coreShowBestTime = new Property<>( "core.bestTime" );
	public static final Property<Boolean> coreShowSegmentTimer = new Property<>( "core.segmentTimer" );
	public static final Property<Font> coreTimerFont = new Property<>( "core.timerFont" );
	public static final Property<Font> coreSegmentTimerFont = new Property<>( "core.segmentTimerFont" );
	public static final Property<Font> coreFont = new Property<>( "core.font" );
	public static final Property<Font> coreOtherTimeFont = new Property<>( "core.otherTimeFont" );

	/* GRAPH properties */

	public static final Property<Boolean> graphDisplay = new Property<>( "graph.display" );
	public static final Property<Float> graphScale = new Property<>( "graph.scale" );

	/* FOOTER properties */

	public static final Property<Boolean> footerDisplay = new Property<>( "footer.display" );
	public static final Property<Boolean> footerUseSplitData = new Property<>( "footer.useSplitData" );
	public static final Property<Boolean> footerVerbose = new Property<>( "footer.verbose" );
	public static final Property<Boolean> footerShowBestTime = new Property<>( "footer.bestTime" );
	public static final Property<Boolean> footerMultiline = new Property<>( "footer.multiline" );
	public static final Property<Boolean> footerShowDeltaLabels = new Property<>( "footer.deltaLabels" );

	private static Configuration global = null;
	private static Run run = null;

	/**
	 * Sets the currently opened run. The run will be asked for its local
	 * configuration first when retrieving a property. If the run does not have
	 * the given property its value will be taken from the global configuration.
	 *
	 * @param run the run currently viewed by Llanfair, cannot be {@code null}
	 */
	public static void setRun( Run run ) {
		if ( run == null ) {
			throw new NullPointerException( "Null run" );
		}
		Settings.run = run;
	}

	/**
	 * Returns a list of all the properties whose key starts with the given
	 * prefix. If given an empty string, every properties are returned.
	 *
	 * @param prefix the prefix for which a list of property is asked
	 * @return the list of all properties whose key starts with the prefix
	 */
	public static List<Property<?>> getAll( String prefix ) {
		if ( prefix == null ) {
			throw new NullPointerException( "Null prefix string" );
		}
		List<Property<?>> list = new ArrayList<Property<?>>();
		for ( Property<?> property : Property.P ) {
			if ( property.key.startsWith( prefix ) ) {
				list.add( property );
			}
		}
		return list;
	}
 
	/**
	 * Registers a new {@code PropertyChangeListener} with the settings.
	 * Whenever a property sees its value updated globally or locally, listeners
	 * are warned of the update.
	 *
	 * @param pcl the listener to register with the settings
	 */
	public static void addPropertyChangeListener( PropertyChangeListener pcl ) {
		if ( pcl == null ) {
			throw new NullPointerException( "Null property listener" );
		}
		global.addPropertyChangeListener( pcl );
		if ( run != null ) {
			run.addSettingChangeListener( pcl );
		}
	}

	/**
	 * Saves the global configuration in {@code llanfair.xml} in the working
	 * directory. If such a file does not exist, it is created.
	 */
	public static void save() {
		global.serialize();
	}

	/**
	 * Retrieves the configuration of Llanfair. The configuration is read from
	 * {@code llanfair.xml} placed in the working directory. If such a file
	 * cannot be found, a default configuration is loaded. No local
	 * configuration is loaded here, a call to {@code setRun} is required to
	 * do just that. This method is lenient and called by the first property
	 * whose value is requested.
	 */
	private static void retrieve() {
		global = Configuration.newInstance( new File(UserSettings.getSettingsPath() + File.separator + "llanfair.xml" ) );
		setDefaultValues();
	}

	private static void setDefault(String key, Object value, boolean force) {
		if (force || !global.contains(key))
			global.put(key, value);
	}

	/**
	 * Sets default values in the global configuration for each property which is
	 * missing (key is not present). Existing values are preserved (even nulls).
	 */
	private static void setDefaultValues() {
		boolean force = false;

		setDefault( alwaysOnTop.key, true, force );
		setDefault( language.key, Locale.ENGLISH, force );
		setDefault( viewerLanguage.key, Locale.ENGLISH, force );
		setDefault( recentFiles.key, new ArrayList<String>(), force );
		setDefault( coordinates.key, null, force );
		setDefault( dimension.key, null, force );
		setDefault( compareMethod.key, Compare.BEST_OVERALL_RUN, force );
		setDefault( accuracy.key, Accuracy.TENTH, force );
		setDefault( warnOnReset.key, true, force );
		setDefault( windowUserResizable.key, true, force );
		setDefault( windowWidth.key, null, force );

		setDefault( colorBackground.key, Color.decode("0x000000"), force );
		setDefault( colorForeground.key, Color.decode( "0xc0c0c0" ), force );
		setDefault( colorTime.key, Color.decode( "0xffffff" ), force );
		setDefault( colorTimer.key, Color.decode( "0x22cc22" ), force );
		setDefault( colorNegativeTime.key, Color.decode ("0x808080" ), force );
		setDefault( colorTimeGainedWhileAhead.key, Color.decode( "0x6295fc" ), force );
		setDefault( colorTimeLostWhileAhead.key, Color.decode( "0x99ccff" ), force );
		setDefault( colorTimeGainedWhileBehind.key, Color.decode( "0xff8e8e" ), force );
		setDefault( colorTimeLostWhileBehind.key, Color.decode( "0xe82323" ), force );
		setDefault( colorNewRecord.key, Color.decode( "0xf0b012" ), force );
		setDefault( colorTitle.key, Color.decode( "0xf0b012" ), force );
		setDefault( colorSubTitle.key, Color.decode( "0xffffff" ), force );
		setDefault( colorHighlight.key, Color.decode( "0xffffff" ), force );
		setDefault( colorSeparators.key, Color.decode( "0x666666" ), force );

		setDefault( useGlobalHotkeys.key, false, force );
		setDefault( hotkeySplit.key, -1, force );
		setDefault( hotkeyUnsplit.key, -1, force );
		setDefault( hotkeySkip.key, -1, force );
		setDefault( hotkeyReset.key, -1, force );
		setDefault( hotkeyStop.key, -1, force );
		setDefault( hotkeyPause.key, -1, force );
		setDefault( hotkeyLock.key, -1, force );

		setDefault( headerShowSubtitle.key, true, force );
		setDefault( headerShowTitle.key, true, force );
		setDefault( headerShowAttempts.key, true, force );
		setDefault( headerTitleFont.key, Font.decode( "Arial-14" ), force );
		setDefault( headerSubTitleFont.key, Font.decode( "Arial-12" ), force );

		setDefault( historyRowCount.key, 8, force );
		setDefault( historyTabular.key, true, force );
		setDefault( historyBlankRows.key, false, force );
		setDefault( historyMultiline.key, false, force );
		setDefault( historyMerge.key, Merge.LIVE, force );
		setDefault( historyLiveTimes.key, true, force );
		setDefault( historyDeltas.key, true, force );
		setDefault( historyIcons.key, true, force );
		setDefault( historyIconSize.key, 16, force );
		setDefault( historyOffset.key, 0, force );
		setDefault( historyAlwaysShowLast.key, true, force );
		setDefault( historySegmentFont.key, Font.decode( "Arial-12" ), force );
		setDefault( historyTimeFont.key, Font.decode( "Arial-11" ), force );

		setDefault( coreAccuracy.key, Accuracy.HUNDREDTH, force );
		setDefault( coreShowIcons.key, true, force );
		setDefault( coreIconSize.key, 40, force );
		setDefault( coreShowSegmentName.key, true, force );
		setDefault( coreShowSplitTime.key, false, force );
		setDefault( coreShowSegmentTime.key, true, force );
		setDefault( coreShowBestTime.key, true, force );
		setDefault( coreShowSegmentTimer.key, true, force );
		setDefault( coreTimerFont.key, Font.decode( "Digitalism-32" ), force );
		setDefault( coreSegmentTimerFont.key, Font.decode( "Digitalism-18" ), force );
		setDefault( coreFont.key, Font.decode( "Arial-12" ), force );
		setDefault( coreOtherTimeFont.key, Font.decode( "Arial-11" ), force );

		setDefault( graphDisplay.key, true, force );
		setDefault( graphScale.key, 3.0F, force );

		setDefault( footerDisplay.key, true, force );
		setDefault( footerVerbose.key, true, force );
		setDefault( footerUseSplitData.key, false, force );
		setDefault( footerShowBestTime.key, true, force );
		setDefault( footerMultiline.key, true, force );
		setDefault( footerShowDeltaLabels.key, true, force );
	}

	/**
	 * Polymorphic property object. It is merely a string identifying the
	 * property and serving as an interface to the configuration. When asked
	 * for its value, the property will return the global value unless a local
	 * value has been defined. Will return a statically typecasted value.
	 *
	 * @param <T> the type of the values of the property
	 * @author Xavier "Xunkar" Sencert
	 * @version 1.0
	 */
	public static class Property<T> {

		private static final List<Property<?>> P = new ArrayList<Property<?>>();

		private String key;

		/**
		 * Creates a new property of given key. If the key contains a dot, the
		 * property name is interpreted as {@code section.key} allowing callers
		 * to grab a submap of the configuration with all properties starting
		 * with {@code section}.
		 *
		 * @param fullKey the name of the property
		 */
		private Property( String fullKey ) {
			this.key = fullKey;
			P.add( this );
		}

		/**
		 * Returns the key string of this property.
		 *
		 * @return the key string of this property
		 */
		public String getKey() {
			return key;
		}

		/**
		 * Returns the value assigned to this property. The value will be first
		 * read from the local configuration, and if no value has been defined
		 * for this property, it will be read from the global configuration. The
		 * first property to call this method will trigger the global
		 * configuration to be read and loaded in memory.
		 *
		 * @return the local value of this property, or the global one if there
		 *  isn't a locally defined value
		 */
		public T get() {
			if ( global == null ) {
				retrieve();
			}
			if ( run != null && run.containsSetting( key ) ) {
				return run.getSetting(key);
			}
			return global.get(key);
		}

		/**
		 * Sets the value of this property in the global configuration.
		 *
		 * @param value the value to assign to this property
		 */
		public void set( T value ) {
			set( value, false );
		}

		/**
		 * Sets the value of this property. The value of {@code locally}
		 * determines if the value must be stored in the local or global
		 * configuration.
		 *
		 * @param value the value to assign to this property
		 * @param locally if the value must be stored in the local configuration
		 */
		public void set( T value, boolean locally ) {
			if ( locally ) {
				run.putSetting( key, value );
			} else {
				global.put( key, value );
			}
		}

		/**
		 * Compares a property to a given string. This property is equal to
		 * the given string if and only if the string is equal to the full key
		 * of this property.
		 *
		 * @param str the string to compare this property against
		 * @return {@code true} if the string equals this property full key
		 */
		public boolean equals( String str ) {
			return key.equals( str );
		}

		/**
		 * Returns the localized name of this property.
		 */
		@Override public String toString() {
			return Language.valueOf(
					"setting_" + key.replaceAll( "\\.", "_" )
			).get();
		}

	}
}
