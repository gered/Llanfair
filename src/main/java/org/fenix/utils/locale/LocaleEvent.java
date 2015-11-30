//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.fenix.utils.locale;

import java.awt.AWTEvent;
import java.util.Locale;

public class LocaleEvent extends AWTEvent {
	public static final int DEFAULT_UPDATE = 0;
	private Locale oldLocale;
	private Locale newLocale;

	public LocaleEvent(Locale old, Locale neu) {
		super(Locale.class, 0);
		this.oldLocale = old;
		this.newLocale = neu;
	}

	public Locale getOldLocale() {
		return this.oldLocale;
	}

	public Locale getNewLocale() {
		return this.newLocale;
	}
}
