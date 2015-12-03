package org.fenix.llanfair;

import org.fenix.utils.Resources;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Enumeration of all externalized strings used by {@code Llanfair}. While it is
 * possible to ask directly the {@link Resources} singleton that this class uses
 * we prefer declaring explicitely every token here to make sure we never call
 * a non-existent reference.
 *
 * @author  Xavier "Xunkar" Sencert
 * @see     Resources
 */
public enum Language {

	// Settings > Generic
	setting_alwaysOnTop,
	setting_language,
	setting_viewerLanguage,
	setting_recentFiles,
	setting_coordinates,
	setting_dimension,
	setting_compareMethod,
	setting_accuracy,
	setting_locked,
	setting_warnOnReset,

	// Settings > Color
	setting_color_background,
	setting_color_foreground,
	setting_color_time,
	setting_color_timer,
	setting_color_timeGained,
	setting_color_timeLost,
	setting_color_newRecord,
	setting_color_title,
	setting_color_highlight,
	setting_color_separators,

	// Settings > Hotkey
	setting_useGlobalHotkeys,
	setting_hotkey_split,
	setting_hotkey_unsplit,
	setting_hotkey_skip,
	setting_hotkey_reset,
	setting_hotkey_stop,
	setting_hotkey_pause,
	setting_hotkey_lock,
	GLOBAL_HOTKEYS_WARNING,
	GLOBAL_HOTKEYS_HOOK_RETRY,
	GLOBAL_HOTKEYS_HOOK_ERROR,

	// Settings > Header
	setting_header_goal,
	setting_header_title,

	// Settings > History
	setting_history_rowCount,
	setting_history_tabular,
	setting_history_blankRows,
	setting_history_multiline,
	setting_history_merge,
	setting_history_liveTimes,
	setting_history_deltas,
	setting_history_icons,
	setting_history_iconSize,
	setting_history_offset,
	setting_history_alwaysShowLast,
	setting_history_segmentFont,
	setting_history_timeFont,

	// Settings > Core
	setting_core_accuracy,
	setting_core_icons,
	setting_core_iconSize,
	setting_core_segmentName,
	setting_core_splitTime,
	setting_core_segmentTime,
	setting_core_bestTime,
	setting_core_segmentTimer,
	setting_core_timerFont,
	setting_core_segmentTimerFont,

	// Settings > Graph
	setting_graph_display,
	setting_graph_scale,

	// Settings > Footer
	setting_footer_display,
	setting_footer_useSplitData,
	setting_footer_verbose,
	setting_footer_bestTime,
	setting_footer_multiline,
	setting_footer_deltaLabels,

	// Accuracy
	accuracy_seconds,
	accuracy_tenth,
	accuracy_hundredth,

	// Compare
	compare_best_overall_run,
	compare_sum_of_best_segments,

	// Merge
	merge_none,
	merge_live,
	merge_delta,

	// MenuItem
	menuItem_edit,
	menuItem_new,
	menuItem_open,
	menuItem_open_recent,
	menuItem_import,
	menuItem_save,
	menuItem_save_as,
	menuItem_reset,
	menuItem_lock,
	menuItem_unlock,
	menuItem_resize_default,
	menuItem_resize_preferred,
	menuItem_settings,
	menuItem_about,
	menuItem_exit,

	// Errors
	error_read_file,
	error_write_file,
	error_import_run,

	// Actions
	action_accept,

	// Titles
	title_about,

	GENERAL,
	TIMER,
	FOOTER,
	MISC,
	USE_MAIN_FONT,
	LB_GOAL,
	ICON,
	COLORS,

	// Edit Dialog
	ED_SEGMENTED,
	TT_ED_SEGMENTED,
	ED_DELAYED_START,

	// Panels Title
	PN_DIMENSION,
	PN_DISPLAY,
	PN_FONTS,
	PN_SCROLLING,

	// History
	HISTORY,
	MERGE_DELTA,
	MERGE_LIVE,
	MERGE_NONE,
	TT_HS_OFFSET,

	// Core
	LB_CR_BEST,
	LB_CR_SEGMENT,
	LB_CR_SPLIT,

	// Footer
	LB_FT_BEST,
	LB_FT_DELTA,
	LB_FT_DELTA_BEST,
	LB_FT_LIVE,
	LB_FT_SEGMENT,
	LB_FT_SPLIT,

	/*
	 * Messages.
	 */
	ICON_TOO_BIG,
	ILLEGAL_TIME,
	ILLEGAL_SEGMENT_TIME,
	INPUT_NAN,
	INPUT_NEGATIVE,
	INVALID_TIME_STAMP,
	WARN_BETTER_RUN,
	WARN_BETTER_TIMES,
	WARN_RESET_SETTINGS,

	/*
	 * Tooltips.
	 */
	TT_ADD_SEGMENT,
	TT_COLOR_PICK,
	TT_COLUMN_BEST,
	TT_COLUMN_SEGMENT,
	TT_COLUMN_TIME,
	TT_REMOVE_SEGMENT,
	TT_MOVE_SEGMENT_UP,
	TT_MOVE_SEGMENT_DOWN,

	/*
	 * Run.State enumeration.
	 */
	RUN_NULL,
	RUN_OVER,
	RUN_READY,
	RUN_STOPPED,

	/*
	 * Time.Accuracy enumeration.
	 */
	ACCURACY,
   
	/*
	 * Miscellaneous tokens.
	 */
	ACCEPT,
	APPLICATION,
	BEST,
	CANCEL,
	COMPARE_METHOD,
	COMPONENTS,
	DISABLED,
	EDITING,
	ERROR,
	GOAL,
	IMAGE,
	INPUTS,
	MAX_ORDINATE,
	NAME,
	RUN_TITLE,
	RUN_FILE_FILTER,
	SEGMENT,
	SEGMENTS,
	SPLIT,
	TIME,
	UNTITLED,
	WARNING,

	/*
	 * 1.4
	 */
	INCREMENT,
	START_VALUE;

	public static final Locale[] LANGUAGES = new Locale[] {
		Locale.ENGLISH,
		Locale.FRENCH,
		Locale.GERMAN,
		new Locale("nl"),
		new Locale("sv")
	};

	public static final Map<String, String> LOCALE_NAMES =
			new HashMap<String, String>();
	static {
		LOCALE_NAMES.put("de", "Deutsch");
		LOCALE_NAMES.put("en", "English");
		LOCALE_NAMES.put("fr", "Français");
		LOCALE_NAMES.put("nl", "Nederlands");
		LOCALE_NAMES.put("sv", "Svenska");
	}

	// -------------------------------------------------------------- INTERFACE

	/**
	 * Returns the localized string get of this language element.
	 *
	 * @return  the localized string for this element.
	 */
	public String get() {
	  return Llanfair.getResources().getString(name());
	}

	/**
	 * Returns the localized string get of this language element. This method
	 * also passes down an array of parameters to replace the tokens with.
	 *
	 * @param   parameters  - the array of values for each token of the string.
	 * @return  the localized string filled with the given parameters.
	 */
	public String get(Object... parameters) {
	  return Llanfair.getResources().getString(name(), parameters);
	}

	/**
	 * The string representation of an enumerate is the localized string
	 * corresponding to its name.
	 */
	@Override public String toString() {
	  return get();
	}
}
