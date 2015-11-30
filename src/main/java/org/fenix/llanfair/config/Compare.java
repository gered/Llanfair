package org.fenix.llanfair.config;

import java.io.Serializable;
import org.fenix.llanfair.Language;

/**
 *
 * @author Xavier
 */
public enum Compare implements Serializable {
    
    BEST_OVERALL_RUN,
    SUM_OF_BEST_SEGMENTS;
    
    private static final long serialVersionUID = 1000L;
    
    @Override public String toString() {
        return Language.valueOf(
                "compare_" + name().toLowerCase().replaceAll("\\.", "_")
        ).get();
    }
}
