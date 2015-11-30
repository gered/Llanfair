package org.fenix.llanfair;

import org.fenix.llanfair.config.Settings;
import org.fenix.llanfair.gui.RunPane;
import org.fenix.utils.Resources;
import org.fenix.utils.gui.BorderlessFrame;
import org.fenix.utils.locale.LocaleDelegate;
import org.fenix.utils.locale.LocaleEvent;
import org.fenix.utils.locale.LocaleListener;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Main frame executing Llanfair.
 *
 * @author Xavier "Xunkar" Sencert
 * @version 1.5
 */
public class Llanfair extends BorderlessFrame implements TableModelListener, 
		LocaleListener, MouseWheelListener, ActionListener, NativeKeyListener,
		PropertyChangeListener, WindowListener {

	private static Resources RESOURCES = null;


	static {
		ToolTipManager.sharedInstance().setInitialDelay( 1000 );
		ToolTipManager.sharedInstance().setDismissDelay( 7000 );
		ToolTipManager.sharedInstance().setReshowDelay( 0 );
	}

	private Run run;
	private RunPane runPane;

	private Actions actions;

	private JPopupMenu popupMenu;

	private volatile boolean ignoreNativeInputs;

	private Dimension preferredSize;

	/**
	 * Creates and initializes the application. As with any Swing application
	 * this constructor should be called from within a thread to avoid
	 * dead-lock.
	 */
	private Llanfair() {
		super( "Llanfair" );
		LocaleDelegate.setDefault( Settings.GNR_LANG.get() );
		LocaleDelegate.addLocaleListener( this );

		//ResourceBundle b = ResourceBundle.getBundle("language");

		RESOURCES = new Resources();
		registerFonts();
		setLookAndFeel();

		run = new Run();
		runPane = null;
		ignoreNativeInputs = false;
		preferredSize = null;
		actions = new Actions( this );

		setMenu();
		setBehavior();
		setRun( run );

		setVisible( true );
	}

	/**
	 * Main entry point of the application. This is the method called by Java
	 * when a user executes the JAR. Simply instantiantes a new Llanfair object.
	 * If an argument is passed, the program will not launch but will instead
	 * enter localization mode, dumping all language variables for the specified
	 * locale.
	 *
	 * @param args array of command line parameters supplied at launch
	 */
	public static void main( String[] args ) {
		if ( args.length > 0 ) {
			String locale = args[0];
			LocaleDelegate.setDefault( new Locale( locale ) );
			RESOURCES = new Resources();
			dumpLocalization();
			System.exit( 0 );
		}
		SwingUtilities.invokeLater( new Runnable() {
			@Override public void run() {
				new Llanfair();
			}
		} );
	}

	/**
	 * Grabs the resources of Llanfair. The Resources object is a front-end
	 * for every classpath resources associated with the application, including
	 * localization strings, icons, and properties.
	 *
	 * @return the resources object
	 */
	public static Resources getResources() {
		return RESOURCES;
	}

	/**
	 * Returns the run currently associated with this instance of Llanfair.
	 * While the run can be empty, it cannot be {@code null}.
	 *
	 * @return the current run
	 */
	Run getRun() {
		return run;
	}

	/**
	 * Sets the run to represent in this application to the given run. If the
	 * GUI does not exist (in other words, we are registering the first run) it
	 * is created on the fly.
	 *
	 * @param run the run to represent, cannot be {@code null}
	 */
	public final void setRun( Run run ) {
		if ( run == null ) {
			throw new NullPointerException( "Null run" );
		}
		this.run = run;
		// If we have a GUI, set the new model; else, create the GUI
		if ( runPane != null ) {
			runPane.setRun( run );
		} else {
			runPane = new RunPane( run );
			add( runPane );
		}
		Settings.setRun( run );
		run.addTableModelListener( this );
		run.addPropertyChangeListener( this );
		MenuItem.setActiveState( run.getState() );

		setPreferredSize( preferredSize );
		pack();

		// Replace the window to the run preferred location; center if none
		Point location = Settings.GNR_COOR.get();
		if ( location == null ) {
			setLocationRelativeTo( null );
		} else {
			setLocation( location );
		}
	}

	/**
	 * Indicates whether or not Llanfair currently ignores all native inputs.
	 * Since native inputs can be caught even when the application does not have
	 * the focus, it is necessary to be able to lock the application when the
	 * user needs to do something else whilst not interfering with the behavior
	 * of Llanfair.
	 *
	 * @return {@code true} if the current instance ignores native inputs
	 */
	public synchronized boolean ignoresNativeInputs() {
		return ignoreNativeInputs;
	}

	/**
	 * Tells Llanfair whether to ignore native input events or not. Since native
	 * inputs can be caught even when the application does not have the focus,
	 * it is necessary to be able to lock the application when the user needs
	 * to do something else whilst not interfering with the behavior of
	 * Llanfair.
	 *
	 * @param ignore if Llanfair must ignore the native inputs or not
	 */
	public synchronized void setIgnoreNativeInputs( boolean ignore ) {
		ignoreNativeInputs = ignore;
	}

	/**
	 * Outputs the given error in a dialog box. Only errors that are made for
	 * and useful to the user need to be displayed that way.
	 *
	 * @param message the localized error message
	 */
	void showError( String message ) {
		JOptionPane.showMessageDialog(
				this, message, Language.ERROR.get(), JOptionPane.ERROR_MESSAGE
		);
	}

	/**
	 * Sets the look and feel of the application. Provides a task bar icon and
	 * a general system dependent theme.
	 */
	private void setLookAndFeel() {
		setIconImage( RESOURCES.getImage( "Llanfair" ) );
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName()
			);
		} catch ( Exception ex ) {
			// $FALL-THROUGH$
		}
	}

	/**
	 * Register the fonts provided with Llanfair with its environment.
	 */
	private void registerFonts() {
		InputStream fontFile = RESOURCES.getStream( "digitalism.ttf" );
		try {
			Font digitalism = Font.createFont( Font.TRUETYPE_FONT, fontFile );
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(
					digitalism
			);
		} catch ( Exception ex ) {
			// $FALL-THROUGH$
		}
	}

	/**
	 * Writes all values from the {@code Language} enum in a property file.
	 * This method will append all the newly defined entries to the list of
	 * already existing values.
	 */
	private static void dumpLocalization() {
		try {
			String iso = Locale.getDefault().getLanguage();
			FileWriter fw = new FileWriter( "language_" + iso + ".properties" );
			for ( Language lang : Language.values() ) {
				String old = RESOURCES.getString( lang.name() );
				fw.write( lang.name() + " = " );
				if ( old != null ) {
					fw.write( old );
				}
				fw.write( "\n" );
			}
			fw.close();
		} catch ( IOException ex ) {
			// $FALL-THROUGH$
		}
	}

	/**
	 * When the locale changes, we first ask the resources to reload the locale
	 * dependent resources and pass the event to the GUI.
	 */
	@Override public void localeChanged( LocaleEvent event ) {
		RESOURCES.defaultLocaleChanged();
		if ( runPane != null ) {
			runPane.processLocaleEvent( event );
		}
		MenuItem.localeChanged( event );
	}

	/**
	 * If we do not ignore the native inputs, register the input and invokes
	 * a new thread to treat the input whenever possible without hogging the
	 * main thread.
	 */
	@Override public void nativeKeyPressed( final NativeKeyEvent event ) {
		int keyCode = event.getKeyCode();
		boolean hotkeysEnabler = ( keyCode == Settings.KEY_LOCK.get() );

		if ( !ignoresNativeInputs() || hotkeysEnabler ) {
			SwingUtilities.invokeLater( new Runnable() {
				@Override public void run() {
					actions.process( event );
				}
			} );
		}
	}

	@Override public void nativeKeyReleased( NativeKeyEvent event ) {}

	@Override public void nativeKeyTyped( NativeKeyEvent event ) {}

	/**
	 * A property change event might be fired from either the settings
	 * singleton or the run itself. In either case, we propagate the event to
	 * our children and update ourself with the new value of the given property.
	 */
	@Override public void propertyChange( PropertyChangeEvent event ) {
		runPane.processPropertyChangeEvent( event );
		String property = event.getPropertyName();

		if ( Run.STATE_PROPERTY.equals( property ) ) {
			MenuItem.setActiveState( run.getState() );
		} else if ( Settings.GNR_ATOP.equals( property ) ) {
			setAlwaysOnTop( Settings.GNR_ATOP.get() );
		} else if (Settings.HST_ROWS.equals(property)
				|| Settings.GPH_SHOW.equals(property)
				|| Settings.FOO_SHOW.equals(property)
				|| Settings.FOO_SPLT.equals(property)
				|| Settings.COR_ICSZ.equals(property)
				|| Settings.GNR_ACCY.equals(property)
				|| Settings.HDR_TTLE.equals(property)
				|| Settings.HDR_GOAL.equals(property)
				|| Settings.HST_DLTA.equals(property)
				|| Settings.HST_SFNT.equals(property)
				|| Settings.HST_TFNT.equals(property)
				|| Settings.HST_LIVE.equals(property)
				|| Settings.HST_MERG.equals(property)
				|| Settings.HST_BLNK.equals(property)
				|| Settings.HST_ICON.equals(property)
				|| Settings.HST_ICSZ.equals(property)
				|| Settings.HST_LINE.equals(property)
				|| Settings.COR_NAME.equals(property)
				|| Settings.COR_SPLT.equals(property)
				|| Settings.COR_SEGM.equals(property)
				|| Settings.COR_BEST.equals(property)
				|| Settings.COR_ICON.equals(property)
				|| Settings.COR_TFNT.equals(property)
				|| Settings.COR_SFNT.equals(property)
				|| Settings.COR_STMR.equals(property)
				|| Settings.FOO_BEST.equals(property)
				|| Settings.FOO_DLBL.equals(property)
				|| Settings.FOO_VERB.equals(property)
				|| Settings.FOO_LINE.equals(property)
				|| Run.NAME_PROPERTY.equals(property)) {
			setPreferredSize(null);
			pack();
		}
	}

	/**
	 * When the run's table of segments is updated, we ask the main panel to
	 * update itself accordingly and repack the frame as its dimensions may
	 * have changed.
	 */
	@Override public void tableChanged( TableModelEvent event ) {
		runPane.processTableModelEvent( event );
		// No need to recompute the size if we receive a HEADER_ROW UPDATE
		// as we only use them when a segment is moved up or down and when
		// the user cancel any changes made to his run.
		if (    event.getType() == TableModelEvent.UPDATE
			 && event.getFirstRow() == TableModelEvent.HEADER_ROW ) {
			setPreferredSize( preferredSize );
		} else {
			setPreferredSize( null );
		}
		pack();
	}

	/**
	 * When the user scrolls the mouse wheel, we update the graph's scale to
	 * zoom in or out depending on the direction of the scroll.
	 */
	@Override public void mouseWheelMoved( MouseWheelEvent event ) {
		int rotations = event.getWheelRotation();
		float percent = Settings.GPH_SCAL.get();
		if ( percent == 0.5F ) {
			percent = 1.0F;
			rotations--;
		}
		float newValue = Math.max( 0.5F, percent + rotations );
		Settings.GPH_SCAL.set( newValue );
	}

	/**
	 * When the user clicks on the mouse's right-button, we bring up the
	 * context menu at the click's location.
	 */
	@Override public void mousePressed( MouseEvent event ) {
		super.mousePressed( event );
		if ( SwingUtilities.isRightMouseButton( event ) ) {
			popupMenu.show( this, event.getX(), event.getY() );
		}
	}

	/**
	 * Whenever the frame is being disposed of, we save the settings and
	 * unregister the native hook of {@code JNativeHook}.
	 */
	@Override public void windowClosed( WindowEvent event ) {
		Settings.save();
		GlobalScreen.unregisterNativeHook();
	}

	@Override public void windowClosing(WindowEvent event) {}

	@Override public void windowOpened(WindowEvent event) {}

	@Override public void windowActivated(WindowEvent event) {}

	@Override public void windowDeactivated(WindowEvent event) {}

	@Override public void windowIconified(WindowEvent event) {}

	@Override public void windowDeiconified(WindowEvent event) {}

	/**
	 * Action events are fired by clicking on the entries of the context menu.
	 */
	@Override public synchronized void actionPerformed( final ActionEvent ev ) {
		MenuItem  source = ( MenuItem ) ev.getSource();

		SwingUtilities.invokeLater( new Runnable() {
			@Override public void run() {
				actions.process( ev );
			}
		} );

		if (source.equals(MenuItem.EDIT)) {

		} else if (source.equals(MenuItem.RESIZE_DEFAULT)) {
			setPreferredSize(null);
			pack();

		} else if (source.equals(MenuItem.RESIZE_PREFERRED)) {
			setPreferredSize(preferredSize);
			pack();
		}
	}

	/**
	 * Sets the persistent behavior of the application and its components.
	 *
	 * @throws  IllegalStateException if JNativeHook cannot be registered.
	 */
	private void setBehavior() {
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			throw new IllegalStateException("cannot register native hook");
		}
		setAlwaysOnTop(Settings.GNR_ATOP.get());
		addWindowListener(this);
		addMouseWheelListener(this);
		Settings.addPropertyChangeListener(this);
		GlobalScreen.getInstance().addNativeKeyListener(this);
	}

	/**
	 * Initializes the right-click context menu.
	 */
	private void setMenu() {
		popupMenu = MenuItem.getPopupMenu();
		MenuItem.addActionListener( this );
		MenuItem.populateRecentlyOpened();
	}

}
