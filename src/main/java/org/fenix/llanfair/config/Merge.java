package org.fenix.llanfair.config;

import org.fenix.llanfair.Language;

import java.io.Serializable;

/**
 *
 * @author Xavier
 */
public enum Merge implements Serializable {

	NONE,
	LIVE,
	DELTA;

	private static final long serialVersionUID = 1000L;

	@Override public String toString() {
		return Language.valueOf("merge_" + name().toLowerCase()).get();
	}
}
