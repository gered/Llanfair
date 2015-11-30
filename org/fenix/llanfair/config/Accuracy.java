package org.fenix.llanfair.config;

import java.io.Serializable;
import org.fenix.llanfair.Language;


public enum Accuracy implements Serializable {
    
    SECONDS,
    TENTH,
    HUNDREDTH;
    
    private static final long serialVersionUID = 1000L;

    @Override public String toString() {
        return Language.valueOf("accuracy_" + name().toLowerCase()).get();
    }
 
    
}
