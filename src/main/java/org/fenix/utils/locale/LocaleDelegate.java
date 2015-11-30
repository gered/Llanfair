//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.fenix.utils.locale;

import javax.swing.event.EventListenerList;
import java.util.Locale;

public class LocaleDelegate {
	private static EventListenerList listeners = new EventListenerList();

	public LocaleDelegate() {
	}

	public static void setDefault(Locale locale) {
		if(locale == null) {
			throw new NullPointerException("Null locale");
		} else {
			Locale old = Locale.getDefault();
			if(!old.equals(locale)) {
				Locale.setDefault(locale);
				fireLocalChanged(old, locale);
			}

		}
	}

	public static void addLocaleListener(LocaleListener listener) {
		if(listener == null) {
			throw new NullPointerException("Null listener");
		} else {
			listeners.add(LocaleListener.class, listener);
		}
	}

	public static void removeLocaleListener(LocaleListener listener) {
		listeners.remove(LocaleListener.class, listener);
	}

	private static void fireLocalChanged(Locale oldLocale, Locale newLocale) {
		LocaleListener[] arr$ = listeners.getListeners(LocaleListener.class);
		int len$ = arr$.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			LocaleListener ll = arr$[i$];
			ll.localeChanged(new LocaleEvent(oldLocale, newLocale));
		}

	}
}
