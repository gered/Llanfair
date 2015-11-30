package org.fenix.llanfair.extern;

import java.io.BufferedReader;
import java.io.IOException;
import javax.swing.ImageIcon;
import org.fenix.llanfair.Llanfair;
import org.fenix.llanfair.Run;
import org.fenix.llanfair.Segment;
import org.fenix.llanfair.Time;

/**
 * Utility class that provides method to interface with WSplit.
 * 
 * @author Xavier "Xunkar" Sencert
 * @version 1.1
 */
public class WSplit {
    
    /**
     * Parses the given stream opened on a WSplit run file and sets it as
     * the currently opened run in Llanfair.
     * 
     * @param master the Llanfair instance calling the parser
     * @param in an opened stream on a WSplit run file
     * @throws Exception if the reading operation cannot complete
     */
    public static void parse( Llanfair master, BufferedReader in ) 
            throws Exception {
        if ( master == null ) {
            throw new NullPointerException( "Null Llanfair instance" );
        }
        if ( in == null ) {
            throw new NullPointerException( "Null file reader" );
        }
        try {
            // Title
            String line = in.readLine();
            Run run = new Run( line.split( "=" )[1] );
            // Attempts, Offset, Size
            in.readLine();
            in.readLine();
            in.readLine();
            // Segment List
            line = parseSegments( in, run );
            // Segment Icons
            parseIcons( line, run );
            master.setRun( run );
        } catch ( Exception ex ) {
            throw ex;
        }
    }
    
    /**
     * Parses the list of segment in a WSplit run file. The segment list is
     * formatted like this: each segment is on one line and comprised of the
     * following information: Name, Old Time, Best Time, Best Segment 
     * comma-separated.
     * 
     * @param in the opened stream on a WSplit run file
     * @param run the run currently built by the parser
     * @return the currently read line marker
     * @throws IOException if reading operations fail
     */
    private static String parseSegments( BufferedReader in, Run run ) 
            throws IOException {
        String line;
        while ( !( line = in.readLine() ).startsWith( "Icons" ) ) {
            String[] args = line.split( "," );
            Segment segment = new Segment( args[0] );
            run.addSegment( segment );

            double parsed = Double.parseDouble( args[2] );
            run.setValueAt( 
                    parsed == 0.0 ? null : new Time( parsed ), 
                    run.getRowCount() - 1, Run.COLUMN_TIME 
            );
            parsed = Double.parseDouble( args[3] );
            segment.setTime(
                    parsed == 0.0 ? null : new Time(parsed), Segment.BEST
            );
        }
        return line;
    }
    
    /**
     * Parses the list of segment icons in a WSplit run file. The list is a 
     * single line, listing the icons in the segment order and formatted as
     * follows: Icons="icon1","icon2",...,"iconN"
     * 
     * @param line the line containing the icon list
     * @param run the run currently built by the parser
     * @throws IOException if reading operations fail
     */
    private static void parseIcons( String line, Run run ) throws IOException {
        line = line.substring( line.indexOf( "=" ) + 1 );
        String[] args = line.split( "," );
        
        for ( int i = 0; i < args.length; i++ ) {
            ImageIcon icon = null;
            String[] lst = args[i].split( "\\\"" );
            
            if ( lst.length > 0 && !lst[1].equals( "" ) ) {
                icon = new ImageIcon( lst[1] );
            }
            run.getSegment( i ).setIcon( icon );
        }
    }
    
}
