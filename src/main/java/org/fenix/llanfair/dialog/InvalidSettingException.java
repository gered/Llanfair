package org.fenix.llanfair.dialog;

import java.awt.*;

public class InvalidSettingException extends Exception {
	public final SettingsTab tab;
	public final Component field;

	public InvalidSettingException(SettingsTab tab, Component field, String message) {
		super(message);
		this.tab = tab;
		this.field = field;
	}

	public InvalidSettingException(SettingsTab tab, Component field, String message, Throwable cause) {
		super(message, cause);
		this.tab = tab;
		this.field = field;
	}
}
