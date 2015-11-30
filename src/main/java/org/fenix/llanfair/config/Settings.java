package org.fenix.llanfair.config;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.fenix.llanfair.Language;
import org.fenix.llanfair.Run;
import org.fenix.utils.config.Configuration;

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
    
    public static final Property<Boolean> GNR_ATOP =
            new Property<Boolean>( "alwaysOnTop" );
    public static final Property<Locale> GNR_LANG =
            new Property<Locale>( "language" );
    public static final Property<Locale> GNR_VLNG =
            new Property<Locale>( "viewerLanguage" );
    public static final Property<List<String>> GNR_RCNT =
            new Property<List<String>>( "recentFiles" );
    public static final Property<Point> GNR_COOR =
            new Property<Point>( "coordinates" );
    public static final Property<Dimension> GNR_SIZE =
            new Property<Dimension>( "dimension" );
    public static final Property<Compare> GNR_COMP =
            new Property<Compare>( "compareMethod" );
    public static final Property<Accuracy> GNR_ACCY =
            new Property<Accuracy>( "accuracy" );
    public static final Property<Boolean> GNR_WARN =
            new Property<Boolean>( "warnOnReset" );
    
    /* COLOR properties */
    
    public static final Property<Color> CLR_BACK =
            new Property<Color>( "color.background" );
    public static final Property<Color> CLR_FORE =
            new Property<Color>( "color.foreground" );
    public static final Property<Color> CLR_TIME =
            new Property<Color>( "color.time" );
    public static final Property<Color> CLR_TIMR =
            new Property<Color>( "color.timer" );
    public static final Property<Color> CLR_GAIN =
            new Property<Color>( "color.timeGained" );
    public static final Property<Color> CLR_LOST =
            new Property<Color>( "color.timeLost" );
    public static final Property<Color> CLR_RCRD =
            new Property<Color>( "color.newRecord" );
    public static final Property<Color> CLR_TITL =
            new Property<Color>( "color.title" );
    public static final Property<Color> CLR_HIGH =
            new Property<Color>( "color.highlight" );
    public static final Property<Color> CLR_SPRT =
            new Property<Color>( "color.separators" );
    
    /* HOTKEY properties */
    
    public static final Property<Integer> KEY_SPLT =
            new Property<Integer>( "hotkey.split" );
    public static final Property<Integer> KEY_USPL =
            new Property<Integer>( "hotkey.unsplit" );
    public static final Property<Integer> KEY_SKIP =
            new Property<Integer>( "hotkey.skip" );
    public static final Property<Integer> KEY_RSET =
            new Property<Integer>( "hotkey.reset" );
    public static final Property<Integer> KEY_STOP =
            new Property<Integer>( "hotkey.stop" );
    public static final Property<Integer> KEY_PAUS =
            new Property<Integer>( "hotkey.pause" );
    public static final Property<Integer> KEY_LOCK =
            new Property<Integer>( "hotkey.lock" );
    
    /* HEADER properties */
    
    public static final Property<Boolean> HDR_TTLE =
            new Property<Boolean>( "header.goal" );
    public static final Property<Boolean> HDR_GOAL =
            new Property<Boolean>( "header.title" );
    
    /* HISTORY properties */
    
    public static final Property<Integer> HST_ROWS =
            new Property<Integer>( "history.rowCount" );
    public static final Property<Boolean> HST_TABL =
            new Property<Boolean>( "history.tabular" );
    public static final Property<Boolean> HST_BLNK =
            new Property<Boolean>( "history.blankRows" );
    public static final Property<Boolean> HST_LINE =
            new Property<Boolean>( "history.multiline" );
    public static final Property<Merge> HST_MERG =
            new Property<Merge>( "history.merge" );
    public static final Property<Boolean> HST_LIVE =
            new Property<Boolean>( "history.liveTimes" );
    public static final Property<Boolean> HST_DLTA =
            new Property<Boolean>( "history.deltas" );
    public static final Property<Boolean> HST_ICON =
            new Property<Boolean>( "history.icons" );
    public static final Property<Integer> HST_ICSZ =
            new Property<Integer>( "history.iconSize" );
    public static final Property<Integer> HST_OFFS =
            new Property<Integer>( "history.offset" );
    public static final Property<Boolean> HST_LAST =
            new Property<Boolean>( "history.alwaysShowLast" );
    public static final Property<Font> HST_SFNT =
            new Property<Font>( "history.segmentFont" );
    public static final Property<Font> HST_TFNT =
            new Property<Font>( "history.timeFont" );
    
    /* CORE properties */
    
    public static final Property<Accuracy> COR_ACCY =
            new Property<Accuracy>( "core.accuracy" );
    public static final Property<Boolean> COR_ICON =
            new Property<Boolean>( "core.icons" );
    public static final Property<Integer> COR_ICSZ =
            new Property<Integer>( "core.iconSize" );
    public static final Property<Boolean> COR_NAME =
            new Property<Boolean>( "core.segmentName" );
    public static final Property<Boolean> COR_SPLT =
            new Property<Boolean>( "core.splitTime" );
    public static final Property<Boolean> COR_SEGM =
            new Property<Boolean>( "core.segmentTime" );
    public static final Property<Boolean> COR_BEST =
            new Property<Boolean>( "core.bestTime" );
    public static final Property<Boolean> COR_STMR =
            new Property<Boolean>( "core.segmentTimer" );
    public static final Property<Font> COR_TFNT =
            new Property<Font>( "core.timerFont" );
    public static final Property<Font> COR_SFNT =
            new Property<Font>( "core.segmentTimerFont" );
    
    /* GRAPH properties */
    
    public static final Property<Boolean> GPH_SHOW =
            new Property<Boolean>( "graph.display" );
    public static final Property<Float> GPH_SCAL =
            new Property<Float>( "graph.scale" );
    
    /* FOOTER properties */
    
    public static final Property<Boolean> FOO_SHOW =
            new Property<Boolean>( "footer.display" );
    public static final Property<Boolean> FOO_SPLT =
            new Property<Boolean>( "footer.useSplitData" );
    public static final Property<Boolean> FOO_VERB =
            new Property<Boolean>( "footer.verbose" );
    public static final Property<Boolean> FOO_BEST =
            new Property<Boolean>( "footer.bestTime" );
    public static final Property<Boolean> FOO_LINE =
            new Property<Boolean>( "footer.multiline" );
    public static final Property<Boolean> FOO_DLBL =
            new Property<Boolean>( "footer.deltaLabels" );

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
        global = Configuration.newInstance( new File( "./llanfair.xml" ) );
        if ( global.isEmpty() ) {
            setDefaultValues();
        }
    } 

    /**
     * Fills the global configuration with every property, assigning them their
     * default value. This method can be called even when the global 
     * configuration is not empty, and will thus function as a reset.
     */
    private static void setDefaultValues() {
        global.put( GNR_ATOP.key, true );
        global.put( GNR_LANG.key, Locale.ENGLISH );
        global.put( GNR_VLNG.key, Locale.ENGLISH );
        global.put( GNR_RCNT.key, new ArrayList<String>() );
        global.put( GNR_COOR.key, null );
        global.put( GNR_SIZE.key, null );
        global.put( GNR_COMP.key, Compare.BEST_OVERALL_RUN );
        global.put( GNR_ACCY.key, Accuracy.TENTH );
        global.put( GNR_WARN.key, true );
        
        global.put( CLR_BACK.key, Color.decode( "0x000000" ) );
        global.put( CLR_FORE.key, Color.decode( "0xc0c0c0" ) );
        global.put( CLR_TIME.key, Color.decode( "0xffffff" ) );
        global.put( CLR_TIMR.key, Color.decode( "0x22cc22" ) );
        global.put( CLR_GAIN.key, Color.decode( "0x6295fc" ) );
        global.put( CLR_LOST.key, Color.decode( "0xe82323" ) );
        global.put( CLR_RCRD.key, Color.decode( "0xf0b012" ) );
        global.put( CLR_TITL.key, Color.decode( "0xf0b012" ) );
        global.put( CLR_HIGH.key, Color.decode( "0xffffff" ) );
        global.put( CLR_SPRT.key, Color.decode( "0x666666" ) );
        
        global.put( KEY_SPLT.key, -1 );
        global.put( KEY_USPL.key, -1 );
        global.put( KEY_SKIP.key, -1 );
        global.put( KEY_RSET.key, -1 );
        global.put( KEY_STOP.key, -1 );
        global.put( KEY_PAUS.key, -1 );
        global.put( KEY_LOCK.key, -1 );
        
        global.put( HDR_TTLE.key, true );
        global.put( HDR_GOAL.key, true );
        
        global.put( HST_ROWS.key, 8 );
        global.put( HST_TABL.key, true );
        global.put( HST_BLNK.key, false );
        global.put( HST_LINE.key, false );
        global.put( HST_MERG.key, Merge.LIVE );
        global.put( HST_LIVE.key, true );
        global.put( HST_DLTA.key, true );
        global.put( HST_ICON.key, true );
        global.put( HST_ICSZ.key, 16 );
        global.put( HST_OFFS.key, 0 );
        global.put( HST_LAST.key, true );
        global.put( HST_SFNT.key, Font.decode( "Arial-12" ) );
        global.put( HST_TFNT.key, Font.decode( "Arial-11" ) );
        
        global.put( COR_ACCY.key, Accuracy.HUNDREDTH );
        global.put( COR_ICON.key, true );
        global.put( COR_ICSZ.key, 40 );
        global.put( COR_NAME.key, true );
        global.put( COR_SPLT.key, false );
        global.put( COR_SEGM.key, true );
        global.put( COR_BEST.key, true );
        global.put( COR_STMR.key, true );
        global.put( COR_TFNT.key, Font.decode( "Digitalism-32" ) );
        global.put( COR_SFNT.key, Font.decode( "Digitalism-18" ) );
        
        global.put( GPH_SHOW.key, true );
        global.put( GPH_SCAL.key, 3.0F );
        
        global.put( FOO_SHOW.key, true );
        global.put( FOO_VERB.key, true );
        global.put( FOO_SPLT.key, false );
        global.put( FOO_BEST.key, true );
        global.put( FOO_LINE.key, true );
        global.put( FOO_DLBL.key, true );
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
                return run.<T>getSetting( key );
            }
            return global.<T>get( key );
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
         * @param valuethe value to assign to this property
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
