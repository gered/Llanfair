package org.fenix.llanfair;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.fenix.llanfair.config.Settings;
import org.fenix.llanfair.dialog.EditRun;
import org.fenix.llanfair.dialog.EditSettings;
import org.fenix.llanfair.extern.WSplit;
import org.fenix.utils.UserSettings;
import org.fenix.utils.about.AboutDialog;
import org.jnativehook.keyboard.NativeKeyEvent;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Regroups all actions, in the meaning of {@link Action}, used by Llanfair.
 * All inputs and menu items callbacks are processed by this delegate to 
 * simplify the work of the main class.
 * 
 * @author Xavier "Xunkar" Sencert
 * @version 1.0
 */
final class Actions {

	public enum FILE_CHOOSER_TYPE {
		OPEN,
		SAVE
	}

	private static final long GHOST_DELAY = 300L;
	private static ResourceBundle BUNDLE = null;

	private Llanfair master;

	private File file;
	private JFileChooser fileChooser;

	private volatile long lastUnsplit;
	private volatile long lastSkip;

	/**
	 * Creates a new delegate. This constructor is package private since it
	 * only need be called by the main class.
	 *
	 * @param owner the Llanfair instance owning this delegate
	 */
	Actions( Llanfair owner ) {
		assert ( owner != null );
		master = owner;

		file = null;
		fileChooser = new JFileChooser(UserSettings.getSplitsPath());
		fileChooser.setFileFilter(new FileNameExtensionFilter("" + Language.RUN_FILE_FILTER, "lfs"));

		lastUnsplit = 0L;
		lastSkip = 0L;

		if ( BUNDLE == null ) {
			BUNDLE = Llanfair.getResources().getBundle( "llanfair" );
		}
	}

	/**
	 * Processes the given native key event. This method must be called from
	 * a thread to prevent possible deadlock.
	 *
	 * @param event the native key event to process
	 */
	void process( NativeKeyEvent event ) {
		assert ( event != null );

		int keyCode = event.getKeyCode();
		Run run = master.getRun();
		Run.State state = run.getState();

		if ( keyCode == Settings.hotkeySplit.get() ) {
			split();
		} else if ( keyCode == Settings.hotkeyReset.get() ) {
			reset();
		} else if ( keyCode == Settings.hotkeyUnsplit.get() ) {
			unsplit();
		} else if ( keyCode == Settings.hotkeySkip.get() ) {
			skip();
		} else if ( keyCode == Settings.hotkeyStop.get() ) {
			if ( state == Run.State.ONGOING ) {
				run.stop();
			}
		} else if ( keyCode == Settings.hotkeyPause.get() ) {
			if ( state == Run.State.ONGOING ) {
				run.pause();
			} else if ( state == Run.State.PAUSED ) {
				run.resume();
			}
		} else if ( keyCode == Settings.hotkeyLock.get() ) {
			master.setIgnoreNativeInputs( !master.ignoresNativeInputs() );
		}
	}

	/**
	 * Processes the given action event. It is assumed here that the action
	 * event is one triggered by a menu item. This method must be called from
	 * a thread to prevent possible deadlock.
	 *
	 * @param event the event to process
	 */
	void process( ActionEvent event ) {
		assert ( event != null );

		Run run = master.getRun();
		MenuItem source = ( MenuItem ) event.getSource();

		if ( source == MenuItem.EDIT ) {
			EditRun dialog = new EditRun( run );
			dialog.display( true, master );
		} else if ( source == MenuItem.NEW ) {
			if ( confirmOverwrite() ) {
				master.setRun( new Run() );
				this.file = null;
			}
		} else if ( source == MenuItem.OPEN ) {
			open( null );
		} else if ( source == MenuItem.OPEN_RECENT ) {
			open( new File( event.getActionCommand() ) );
		} else if ( source == MenuItem.IMPORT ) {
			importOtherFormat();
		} else if ( source == MenuItem.SAVE ) {
			run.saveLiveTimes( !run.isPersonalBest() );
			run.reset();
			save(this.file);
		} else if ( source == MenuItem.SAVE_AS ) {
			save(null);
		} else if ( source == MenuItem.RESET ) {
			reset();
			} else if ( source == MenuItem.LOCK ) {
			master.setLockedHotkeys(true);
		} else if ( source == MenuItem.UNLOCK ) {
			master.setLockedHotkeys(false);
		} else if ( source == MenuItem.SETTINGS ) {
			EditSettings dialog = new EditSettings();
			dialog.display( true, master );
		} else if ( source == MenuItem.ABOUT ) {
			about();
		} else if ( source == MenuItem.EXIT ) {
			if ( confirmOverwrite() ) {
				// these might not really be necessary, but at this point just trying to be sure a running timer isn't
				// sometimes keeping the app open somehow ??
				if (run.getState() == Run.State.ONGOING)
					run.stop();
				run.reset();

				// exit the app
				master.dispose();
			}
		}
	}

	/**
	 * Performs a split or starts the run if it is ready. Can also resume a
	 * paused run in case the run is segmented.
	 */
	private void split() {
		Run run = master.getRun();
		Run.State state = run.getState();
		if ( state == Run.State.ONGOING ) {
			long milli = System.nanoTime() / 1000000L;
			long start = run.getSegment( run.getCurrent() ).getStartTime();
			if ( milli - start > GHOST_DELAY ) {
				run.split();
			}
		} else if ( state == Run.State.READY ) {
			run.start();
		} else if ( state == Run.State.PAUSED && run.isSegmented() ) {
			run.resume();
		}
	}

	/**
	 * Resets the current run to a ready state. If the user asked to be warned
	 * a pop-up will ask confirmation in case some live times are better.
	 */
	private void reset() {
		Run run = master.getRun();
		if ( run.getState() != Run.State.NULL ) {
			if ( !Settings.warnOnReset.get() || confirmOverwrite() ) {
				run.reset();
			}
		}
	}

	/**
	 * Performs an "unsplit" on the current run. If a split has been made, it
	 * is canceled and the time that passed after said split is added back to
	 * the timer, as if the split had not taken place.
	 */
	private void unsplit() {
		Run run = master.getRun();
		Run.State state = run.getState();
		if ( state == Run.State.ONGOING || state == Run.State.STOPPED ) {
			long milli = System.nanoTime() / 1000000L;
			if ( milli - lastUnsplit > GHOST_DELAY ) {
				lastUnsplit = milli;
				run.unsplit();
			}
		}
	}

	/**
	 * Skips the current split in the run. Skipping a split sets an undefined
	 * time for the current segment and merges the live time of the current
	 * segment with the following one.
	 */
	private void skip() {
		Run run = master.getRun();
		if ( run.getState() == Run.State.ONGOING ) {
			long milli = System.nanoTime() / 1000000L;
			if ( milli - lastSkip > GHOST_DELAY ) {
				lastSkip = milli;
				run.skip();
			}
		}
	}

	private String getFileExtension(String filename) {
		try {
			// doing it this way because if one of the directories leading up to the filename contains
			// a '.' but the actual filename doesn't, we'd otherwise think that the filename had an extension.
			File file = new File(filename);

			int p = file.getName().lastIndexOf(".");
			if (p == -1)
				return null;

			return file.getName().substring(p + 1);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Displays a dialog to let the user select a file. The user is able to
	 * cancel this action, which results in a {@code null} being returned.
	 *
	 * @param dialogType the type of dialog to show (save or open)
	 *
	 * @return a file selected by the user or {@code null} if he canceled
	 */
	private File selectFile(FILE_CHOOSER_TYPE dialogType) {
		int action = -1;

		if (this.file == null)
			fileChooser.setCurrentDirectory(new File(UserSettings.getSplitsPath()));
		else
			fileChooser.setCurrentDirectory(this.file);

		if (dialogType == FILE_CHOOSER_TYPE.OPEN)
			action = fileChooser.showOpenDialog(master);
		else if (dialogType == FILE_CHOOSER_TYPE.SAVE)
			action = fileChooser.showSaveDialog(master);

		if (action == JFileChooser.APPROVE_OPTION) {
			String selectedFile = fileChooser.getSelectedFile().getPath();
			if (dialogType == FILE_CHOOSER_TYPE.SAVE) {
				// add default ".lfs" extension if the user did not add one themselves
				if (getFileExtension(selectedFile) == null)
					selectedFile = selectedFile + ".lfs";
			}
			return new File(selectedFile);
		} else
			return null;
	}

	/**
	 * Asks the user to confirm the discard of the current run. The popup
	 * window will only trigger if the current run has not been saved after
	 * some editing or if the run presents better times.
	 *
	 * @return {@code true} if the user wants to discard the run
	 */
	private boolean confirmOverwrite() {
		boolean before = master.ignoresNativeInputs();
		master.setIgnoreNativeInputs( true );

		Run run = master.getRun();
		boolean betterRun = run.isPersonalBest();
		boolean betterSgt = run.hasSegmentsBest();

		boolean confirmed = true;

		if ( betterRun || betterSgt ) {
			String message = betterRun
					? Language.WARN_BETTER_RUN.get()
					: Language.WARN_BETTER_TIMES.get();

			int option = JOptionPane.showConfirmDialog( master, message,
					Language.WARNING.get(), JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE );

			if ( option == JOptionPane.CANCEL_OPTION )
				confirmed = false;
			else if ( option == JOptionPane.YES_OPTION ) {
				run.saveLiveTimes( !betterRun );
				run.reset();
				save(this.file);
				confirmed = true;
			}
		}
		master.setIgnoreNativeInputs( before );
		return confirmed;
	}

	/**
	 * Opens the given file. If the file is {@code null}, the user is asked
	 * to select one. Before anything is done, the user is also asked for
	 * a confirmation if the current run has not been saved.
	 *
	 * @param file the file to open
	 */
	void open( File file ) {
		if ( !confirmOverwrite() ) {
			return;
		}
		if ( file == null ) {
			if ( ( file = selectFile(FILE_CHOOSER_TYPE.OPEN) ) == null ) {
				return;
			}
		}
		this.file = file;
		String name = file.getName();
		try {
			open();
		} catch ( Exception ex ) {
			master.showError( Language.error_read_file.get( name ) );
			this.file = null;
		}
	}

	/**
	 * Opens the currently selected file. This method will first try to open
	 * the file using the new method (XStream XML) and if it fails will try to
	 * use the legacy method (Java ObjectStream.)
	 *
	 * @throws Exception if the reading operation fails
	 */
	private void open() throws Exception {
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream( new FileInputStream( file ) );
			try {
				in.mark( Integer.MAX_VALUE );
				xmlRead( in );
			} catch ( Exception ex ) {
				in.reset();
				legacyRead( new ObjectInputStream( in ) );
			}
			MenuItem.recentlyOpened( "" + file );
		} catch ( Exception ex ) {
			throw ex;
		} finally {
			try {
				in.close();
			} catch ( Exception ex ) {
				//$FALL-THROUGH$
			}
		}
	}

	/**
	 * Reads a stream on a run file using the new (since 1.5) XML method. This
	 * method will not be able to read a legacy run file and will throw an
	 * exception if confronted with such run file.
	 *
	 * @param in the input stream on the run file
	 */
	private void xmlRead( InputStream in ) {
		XStream xml = new XStream( new DomDriver() );
		SerializationUtils.customize(xml);
		master.setRun( ( Run ) xml.fromXML( in ) );
	}

	/**
	 * Reads a stream on a run file using the legacy Java method. This method
	 * might fail if the given file is not a Llanfair run.
	 *
	 * @param in an input stream on the run file
	 * @throws Exception if the stream cannot be read
	 */
	private void legacyRead( ObjectInputStream in ) throws Exception {
		master.setRun( ( Run ) in.readObject() );
		try {
			Settings.dimension.set( ( Dimension ) in.readObject(), true );
		} catch ( Exception ex ) {
			// $FALL-THROUGH$
		}
	}

	/**
	 * Saves the currently opened run to the currently selected file. If no
	 * file has been selected, the user is asked for one.
	 *
	 * @param file the file to save to, or if null, the user is prompted to select one
	 */
	private void save(File file) {
		if ( file == null ) {
			if ( ( file = selectFile(FILE_CHOOSER_TYPE.SAVE) ) == null ) {
				return;
			}
		}
		this.file = file;
		Settings.coordinates.set( master.getLocationOnScreen(), true );
		Settings.dimension.set( master.getSize(), true );

		String name = file.getName();
		BufferedOutputStream out = null;
		try {
			XStream xml = new XStream( new DomDriver() );
			SerializationUtils.customize(xml);
			out = new BufferedOutputStream( new FileOutputStream( file ) );
			xml.toXML( master.getRun(), out );
		} catch ( Exception ex ) {
			master.showError( Language.error_write_file.get( name ) );
		} finally {
			try {
				out.close();
			} catch ( Exception ex ) {
				// $FALL-THROUGH$
			}
		}
	}

	/**
	 * Imports a run from another timer application. If no file has been
	 * selected, the user is asked for one. As of now, only WSplit run files
	 * are supported.
	 */
	private void importOtherFormat() {
		if ( !confirmOverwrite() ) {
			return;
		}
		if ( file == null ) {
			if ( ( file = selectFile(FILE_CHOOSER_TYPE.OPEN) ) == null ) {
				return;
			}
		}
		String name = file.getName();
		BufferedReader in = null;
		try {
			in = new BufferedReader( new FileReader( file ) );
			WSplit.parse( master, in );
		} catch ( Exception ex ) {
			master.showError( Language.error_import_run.get( name ) );
		} finally {
			try {
				in.close();
			} catch ( Exception ex ) {
				// $FALL-THROUGH$
			}
		}
	}

	/**
	 * Displays the "about" dialog. The dialog displays the version of Llanfair,
	 * the creative commons licence, the credits of development, a link to
	 * Llanfair's website and a link to donate.
	 */
	private void about() {
		AboutDialog dialog = new AboutDialog(
				master, Language.title_about.get() );
		dialog.setMessage( BUNDLE.getString( "about" ));
		try {
			dialog.setWebsite( new URL( BUNDLE.getString( "website" ) ) );
		} catch ( MalformedURLException ex ) {
			// $FALL-THROUGH$
		}
		/*
		try {
			dialog.setDonateLink( new URL( BUNDLE.getString( "donate" ) ),
					Llanfair.getResources().getIcon( "donate.png" ) );
		} catch ( MalformedURLException ex ) {
		   // $FALL-THROUGH$
		}
		*/
		dialog.display();
	}
}
