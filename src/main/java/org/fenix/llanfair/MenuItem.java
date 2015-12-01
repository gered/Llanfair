package org.fenix.llanfair;

import org.fenix.llanfair.Run.State;
import org.fenix.llanfair.config.Settings;
import org.fenix.utils.locale.LocaleEvent;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Enumerates the menu items available in the right-click context menu of
 * Llanfair. An item knows how to look but does not know how to behave. The
 * behavior is abstracted in the {@code Actions} class.
 * 
 * @author Xavier "Xunkar" Sencert
 * @version 1.1
 */
enum MenuItem implements ActionListener {

	EDIT( false, State.NULL, State.READY ),
	NEW( true, State.NULL, State.READY, State.STOPPED ),
	OPEN( false, State.NULL, State.READY, State.STOPPED ),
	OPEN_RECENT( false, State.NULL, State.READY, State.STOPPED ),
	IMPORT( false, State.NULL, State.READY, State.STOPPED ),
	SAVE( false, State.READY, State.STOPPED ),
	SAVE_AS( true, State.READY ),
	RESET( true, State.ONGOING, State.STOPPED, State.PAUSED ),
	LOCK( false, State.NULL, State.READY, State.STOPPED, State.ONGOING ),
	UNLOCK( false, State.NULL, State.READY, State.STOPPED, State.ONGOING ),
	RESIZE_DEFAULT( false, State.NULL, State.READY ),
	RESIZE_PREFERRED( true, State.NULL, State.READY ),
	SETTINGS( true, State.NULL, State.READY, State.STOPPED ),
	ABOUT( true, State.NULL, State.READY, State.STOPPED, State.ONGOING ),
	EXIT( false, State.NULL, State.READY, State.STOPPED, State.ONGOING );

	/**
	 * Static list of listeners that listen to all the menu items. Whenever
	 * one item fires an {@code ActionEvent}, the type statically fires an
	 * other {@code ActionEvent} with the {@code MenuItem} enumerate as the
	 * source component.
	 */
	private static EventListenerList listeners = new EventListenerList();

	private static final int MAX_FILES = 5;
	private static final int TRUNCATE = 30;

	private boolean isEndOfGroup;
	private List<State> activeStates;
	private JMenuItem menuItem;

	/**
	 * Internal constructor used to set the attributes. Only called by the
	 * enum type itself.
	 *
	 * @param isEndOfGroup indicates if this item is a group ender
	 * @param activeStates list of active run states of this item
	 */
	MenuItem(boolean isEndOfGroup, Run.State... activeStates) {
		this.isEndOfGroup = isEndOfGroup;
		this.activeStates = Arrays.asList( activeStates );

		if ( name().equals( "OPEN_RECENT" ) ) {
			menuItem = new JMenu( toString() );
		} else {
			menuItem = new JMenuItem( toString() );
		}
		Icon icon = Llanfair.getResources().getIcon( "jmi/" + name() + ".png" );
		menuItem.setIcon( icon );
		menuItem.addActionListener( this );
		// LOCK & UNLOCK mask each other and Llanfair always start unlocked
		if ( name().equals( "UNLOCK" ) ) {
			menuItem.setVisible( false );
		}
	}

	/**
	 * Returns a popup menu composed of all the menu items. A separator is
	 * inserted after each item which are indicated as end of their group.
	 *
	 * @return a popup menu containing every menu item
	 */
	static JPopupMenu getPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		for ( MenuItem item : values() ) {
			menu.add( item.menuItem );
			if ( item.isEndOfGroup ) {
				menu.add( new JSeparator() );
			}
		}
		return menu;
	}

	/**
	 * Enables or disables every menu items depending on the given run state.
	 * If this state is present in the list of active states for an item, then
	 * its GUI component is enabled, else it is disabled.
	 *
	 * @param state the current run state, cannot be {@code null}
	 */
	static void setActiveState( Run.State state ) {
		if ( state == null ) {
			throw new IllegalArgumentException( "Null run state" );
		}
		for ( MenuItem item : values() ) {
			item.menuItem.setEnabled( item.activeStates.contains( state ) );
		}
	}

	/**
	 * Registers the given {@code ActionListener} to the list of listeners
	 * interested in capturing events from every menu items.
	 *
	 * @param listener the action listener to register
	 */
	static void addActionListener( ActionListener listener ) {
		listeners.add( ActionListener.class, listener );
	}

	/**
	 * Callback to invoke whenever a file is opened. This method will sort the
	 * recent files menu to put the recently opened file at the top.
	 *
	 * @param path the name of the recently opened file
	 */
	static void recentlyOpened( String path ) {
		assert ( path != null );
		List<String> recentFiles = Settings.recentFiles.get();

		if ( recentFiles.contains( path ) ) {
			recentFiles.remove( path );
		}
		recentFiles.add( 0, path );

		if ( recentFiles.size() > MAX_FILES ) {
			recentFiles.remove( MAX_FILES );
		}
		Settings.recentFiles.set( recentFiles );
		populateRecentlyOpened();
	}

	/**
	 * Fills the {@code OPEN_RECENT} item with the list of recently opened
	 * files. If {@code MAX_FILES} is somehow lower than the recent files list
	 * length, the overflowing files are removed.
	 */
	static void populateRecentlyOpened() {
		List<String> recentFiles = Settings.recentFiles.get();
		for ( int i = MAX_FILES; i < recentFiles.size(); i++ ) {
			recentFiles.remove( i - 1 );
		}
		OPEN_RECENT.menuItem.removeAll();
		for ( String fileName : Settings.recentFiles.get() ) {
			String text = fileName;
			int index = text.lastIndexOf( File.separatorChar );
			if ( index == -1 ) {
				int length = text.length();
				int start = length - Math.min( length, TRUNCATE );
				text = text.substring( start );
				if ( start == 0 ) {
					text = "[...]" + text;
				}
			} else {
				text = text.substring( index + 1 );
			}
			JMenuItem jmi = new JMenuItem( text );
			jmi.setName( "RECENT" + fileName );
			jmi.addActionListener( OPEN_RECENT );
			OPEN_RECENT.menuItem.add( jmi );
		}
	}

	/**
	 * Returns the localized name of this menu item.
	 */
	@Override public String toString() {
		return Language.valueOf( "menuItem_" + name().toLowerCase() ).get();
	}

	/**
	 * When a GUI component fires an action event, capture it and fire it
	 * back, setting the source as the enumerate value instead of the GUI
	 * component.
	 */
	@Override public void actionPerformed( ActionEvent event ) {
		Object source = event.getSource();

		if ( source.equals( LOCK.menuItem ) ) {
			LOCK.menuItem.setVisible( false );
			UNLOCK.menuItem.setVisible( true );
		} else if ( source.equals( UNLOCK.menuItem ) ) {
			LOCK.menuItem.setVisible( true );
			UNLOCK.menuItem.setVisible( false );
		}

		JMenuItem jmi = ( JMenuItem ) source;
		String name = jmi.getName();
		ActionListener[] als = listeners.getListeners( ActionListener.class );

		if ( name != null && name.startsWith( "RECENT" ) ) {
			event = new ActionEvent(
					this, ActionEvent.ACTION_PERFORMED, name.substring( 6 )
			);
		} else {
			event = new ActionEvent(
					this, ActionEvent.ACTION_PERFORMED, jmi.getText()
			);
		}
		for ( ActionListener al : als ) {
			al.actionPerformed( event );
		}
	}

	/**
	 * When the locale changes, every menu item must be updated to enforce the
	 * new locale setting.
	 */
	public static void localeChanged( LocaleEvent event ) {
		for ( MenuItem item : values() ) {
			item.menuItem.setText( "" + item );
		}
	}

}
