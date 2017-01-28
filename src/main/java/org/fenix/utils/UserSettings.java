package org.fenix.utils;

import org.fenix.llanfair.config.Settings;

import java.io.File;

public class UserSettings {
	private static File settingsPath = new File(System.getProperty("user.home") + File.separator + ".llanfair");
	private static File splitsPath = new File(settingsPath.getPath() + File.separator + "splits");

	/**
	 * Checks for the existence of the directory used to store the user's settings for the
	 * application. If it does not exist, we attempt to create it.
	 */
	public static void initDirectory() {
		Boolean settingsPathExists = settingsPath.exists();

		if (!settingsPathExists)
			settingsPathExists = settingsPath.mkdir();

		if (!splitsPath.exists() && settingsPathExists)
			splitsPath.mkdir();
	}

	/**
	 * Returns the path to the location where user settings can be saved in.
	 */
	public static String getSettingsPath() {
		return settingsPath.getPath();
	}

	/**
	 * Returns the path to the location where splits can be saved in. This is
	 * located as a subdirectory within the user settings directory.
	 */
	public static String getDefaultSplitsPath() {
		return splitsPath.getPath();
	}

	public static String getSplitsPath(File selectedFile) {
		if (selectedFile != null)
			return selectedFile.toString();
		else {
			if (Settings.useDefaultSplitsPath.get())
				return UserSettings.getDefaultSplitsPath();
			else
				return Settings.customSplitsPath.get();
		}
	}

}
