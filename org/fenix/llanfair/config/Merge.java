package org.fenix.llanfair.config;

import java.io.Serializable;
import org.fenix.llanfair.Language;

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
