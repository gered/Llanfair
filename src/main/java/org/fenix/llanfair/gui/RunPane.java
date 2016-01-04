package org.fenix.llanfair.gui;

import org.fenix.llanfair.Run;
import org.fenix.llanfair.Run.State;
import org.fenix.llanfair.config.Settings;
import org.fenix.utils.gui.GBC;
import org.fenix.utils.locale.LocaleEvent;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * A panel representing informations on a given run. This panel uses numerous
 * sub-component to given different kind of informations like a graph or a 
 * segment’s history that can be toggled on or off via the {@link Settings}.
 *
 * @author  Xavier "Xunkar" Sencert
 */
public class RunPane extends JPanel {

	// -------------------------------------------------------------- CONSTANTS

	/**
	 * Font used to render the title of the run.
	 */
	private static final Font RUN_TITLE_FONT = Font.decode("Arial-12-BOLD");

	/**
	 * Update identifier for every category.
	 */
	private static final int ALL = 0xff;

	/**
	 * Update identifier for time variables.
	 */
	private static final int SUBTITLE = 0x01;

	/**
	 * Update identifier for separator variables.
	 */
	private static final int SEPARATOR = 0x02;

	/**
	 * Update identifier for text variables.
	 */
	private static final int TEXT = 0x04;

	/**
	 * Update identifier for title variables.
	 */
	private static final int TITLE = 0x08;

	/**
	 * Update identifier for background variables.
	 */
	private static final int BACKGROUND = 0x10;

	/**
	 * Update identifier for the graph component.
	 */
	private static final int GRAPH = 0x20;

	/**
	 * Update identifier for the footer component.
	 */
	private static final int FOOTER = 0x40;

	/**
	 * Update identifier for the attempt counter component.
	 */
	private static final int ATTEMPTS = 0x80;


	// ------------------------------------------------------------- ATTRIBUTES

	/**
	 * Run instance represented by this component.
	 */
	private Run run;

	/**
	 * Label displaying the title of the current run.
	 */
	private JLabel title;

	/**
	 * Label displaying the sub title of the current run.
	 */
	private JLabel subTitle;

	/**
	 * Label displaying the number of attempts the user has made of this run.
	 */
	private JLabel attemptCounter;

	/**
	 * A list containing empty labels serving as separators.
	 */
	private List<JLabel> separators;

	/**
	 * Simple footer displaying information on the previous segment if any.
	 * Is only visible if {@link Settings#FOOTER_DISPLAY} is {@code true}.
	 */
	private Footer footer;

	/**
	 * Panel displaying the core information like the current segment and the
	 * necessary timers.
	 */
	private Core core;

	/**
	 * Panel representing the current run as a graph.
	 */
	private Graph graph;

	/**
	 * Scrolling panel displaying information regarding every segment of the
	 * run up to the last segment or {@link Settings#DISPLAYED_SEGMENTS}.
	 */
	private History history;

	// ----------------------------------------------------------- CONSTRUCTORS

	/**
	 * Creates a default panel representing the given run.
	 *
	 * @param   run - the run to represent.
	 */
	public RunPane(Run run) {
		super(new GridBagLayout());
		if (run == null) {
			throw new NullPointerException("null run");
		}
		title           = new JLabel();
		subTitle        = new JLabel();
		attemptCounter  = new JLabel();
		core            = new Core(run);
		graph           = new Graph(run);
		history         = new History(run);
		footer    = new Footer(run);
		separators      = new ArrayList<JLabel>();

		title.setHorizontalAlignment(SwingConstants.CENTER);
		subTitle.setHorizontalAlignment(SwingConstants.CENTER);

		placeComponents();
		setRun(run);

		updateValues(TEXT);
		updateColors(ALL);
		updateFonts(ALL);
		updateVisibility(ALL);
	}

	// -------------------------------------------------------------- INTERFACE

	/**
	 * Sets the new run to represent.
	 *
	 * @param   run - the new run to represent.
	 */
	public final void setRun(Run run) {
		if (run == null) {
			throw new NullPointerException("null run");
		}
		this.run = run;
		core.setRun(run);
		graph.setRun(run);
		history.setRun(run);
		footer.setRun(run);

		updateValues(ALL & ~TEXT);
	}

	// -------------------------------------------------------------- CALLBACKS

	/**
	 * Callback invoked by the parent when default local for this instance of
	 * the JVM has changed.
	 *
	 * @param   event   - the event describing the update.
	 */
	public void processLocaleEvent(LocaleEvent event) {
		core.processLocaleEvent(event);
		graph.processLocaleEvent(event);
		footer.processLocaleEvent(event);

		updateValues(TEXT);
	}

	/**
	 * Callback invoked by the parent when the run or the application's
	 * settings have seen one of their properties updated.
	 *
	 * @param   event   - the event describing the update.
	 */
	public void processPropertyChangeEvent(PropertyChangeEvent event) {
		core.processPropertyChangeEvent(event);
		graph.processPropertyChangeEvent(event);
		history.processPropertyChangeEvent(event);
		footer.processPropertyChangeEvent(event);

		String property = event.getPropertyName();
		if (Run.STATE_PROPERTY.equals(property)) {
			if (run.getState() == State.READY || run.getState() == State.NULL) {
				updateValues(ATTEMPTS | SEPARATOR);
			}
		} else if (Run.NAME_PROPERTY.equals(property)) {
			updateValues(TITLE);
		} else if (Settings.colorBackground.equals(property)) {
			updateColors(BACKGROUND);
		} else if (Settings.colorForeground.equals(property)) {
			updateColors(TEXT);
		} else if (Settings.colorSeparators.equals(property)) {
			updateColors(SEPARATOR);
		} else if (Settings.historyRowCount.equals(property)) {
			updateValues(SEPARATOR);
		} else if (Settings.colorTitle.equals(property)
		           || Settings.colorSubTitle.equals(property)) {
			updateColors(TITLE);
		} else if (Settings.graphDisplay.equals(property)) {
			updateVisibility(GRAPH);
		} else if (Settings.footerDisplay.equals(property)) {
			updateVisibility(FOOTER);
		} else if (Settings.headerShowTitle.equals(property)) {
			updateVisibility(TITLE);
			updateValues(SEPARATOR);
		} else if (Settings.headerShowSubtitle.equals(property)) {
			updateVisibility(SUBTITLE);
			updateValues(SEPARATOR);
		} else if (Settings.headerShowAttempts.equals(property)) {
			updateVisibility(ATTEMPTS);
			updateValues(SEPARATOR);
		} else if (Settings.accuracy.equals(property)
				|| Run.SUBTITLE_PROPERTY.equals(property)) {
			updateValues(TITLE);
		} else if (Run.ATTEMPT_COUNTER_PROPERTY.equals(property) ||
			Run.COMPLETED_ATTEMPT_COUNTER_PROPERTY.equals(property)) {
			updateValues(ATTEMPTS);
		} else if (Settings.headerTitleFont.equals(property)
		           || Settings.headerSubTitleFont.equals(property)) {
			updateFonts(TITLE);
		} else if (Settings.coreFont.equals(property)) {
			updateFonts(ALL & ~TITLE);
		}
	}

	/**
	 * Callback invoked by the parent when the run table of segments is
	 * updated.
	 *
	 * @param   event   - the event describing the update.
	 */
	public void processTableModelEvent(TableModelEvent event) {
		core.processTableModelEvent(event);
		graph.processTableModelEvent(event);
		history.processTableModelEvent(event);
		if (event.getType() == TableModelEvent.INSERT
				|| event.getType() == TableModelEvent.DELETE
				|| event.getType() == TableModelEvent.UPDATE) {
			updateValues(TITLE);
		}
	}

	// -------------------------------------------------------------- UTILITIES

	/**
	 * Adds a new separator to the list of separators used by the component
	 * and returns it.
	 *
	 * @param   a new separator.
	 */
	private JLabel createSeparator() {
		JLabel label = new JLabel();
		separators.add(label);
		return label;
	}

	/**
	 * Places the sub-components within this component.
	 */
	private void placeComponents() {
		add(title, GBC.grid(0, 0).insets(3, 0, 1, 0).fill(GBC.B));
		add(subTitle, GBC.grid(0, 1).insets(3, 0, 0, 0).fill(GBC.B));
		add(attemptCounter, GBC.grid(0, 2).insets(1, 0, 1, 3).anchor(GBC.LE));
		add(createSeparator(), GBC.grid(0, 3).insets(3, 0).fill(GBC.H));
		add(history, GBC.grid(0, 4).fill(GBC.H).insets(0, 5));
		add(createSeparator(), GBC.grid(0, 5).insets(3, 0).fill(GBC.H));
		add(createSeparator(), GBC.grid(0, 7).insets(3, 0, 0, 0).fill(GBC.H));

		updateVisibility(ALL);
	}

	/**
	 * Updates the values of the group of components specified by the
	 * identifier.
	 *
	 * @param   identifier  - one of the constant update identifier.
	 */
	private void updateValues(int identifier) {
		if ((identifier & TITLE) == TITLE) {
			title.setText("<html><div style='text-align: center;'>" + sanitizeTitleString(run.getName()) + "</div></html>");
			subTitle.setText(run.getSubTitle());
		}
		if ((identifier & ATTEMPTS) == ATTEMPTS) {
			int attempts = run.getNumberOfAttempts();
			int completedAttempts = run.getNumberOfCompletedAttempts();
			if (attempts == 0)
				attemptCounter.setText("0");
			else if (completedAttempts == 0)
				attemptCounter.setText(String.format("%d", attempts));
			else
				attemptCounter.setText(String.format("%d / %d", completedAttempts, attempts));
		}
		if ((identifier & SEPARATOR) == SEPARATOR) {
			boolean hdTitle = Settings.headerShowSubtitle.get();
			boolean hdSubtitle = Settings.headerShowTitle.get();
			boolean hdAttempts = Settings.headerShowAttempts.get();
			boolean hsRows = history.getRowCount() > 0;

			separators.get(0).setVisible(hdTitle || hdSubtitle || hdAttempts);
			separators.get(1).setVisible(hsRows);
		}
	}

	/**
	 * Updates the colors of the group of components specified by the
	 * identifier.
	 *
	 * @param   identifier  - one of the constant update identifier.
	 */
	private void updateColors(int identifier) {
		if ((identifier & TITLE) == TITLE) {
			title.setForeground(Settings.colorTitle.get());
			subTitle.setForeground(Settings.colorSubTitle.get());
		}
		if ((identifier & BACKGROUND) == BACKGROUND) {
			setBackground(Settings.colorBackground.get());
		}
		if ((identifier & SEPARATOR) == SEPARATOR) {
			Color color = Settings.colorSeparators.get();
			for (JLabel separator : separators) {
				separator.setBorder(
						BorderFactory.createMatteBorder(1, 0, 0, 0, color));
			}
		}

		attemptCounter.setForeground(Color.WHITE);
	}

	/**
	 * Updates the fonts of the group of components specified by the
	 * identifier.
	 *
	 * @param identifier - one of the constant update identifier.
	 */
	private void updateFonts(int identifier) {
		if ((identifier & TITLE) == TITLE) {
			title.setFont(Settings.headerTitleFont.get());
			subTitle.setFont(Settings.headerSubTitleFont.get());
		}
		if ((identifier & ATTEMPTS) == ATTEMPTS) {
			attemptCounter.setFont(Settings.coreFont.get());
		}
	}

	/**
	 * Updates the visibility of the components specified by the
	 * identifier.
	 *
	 * @param   identifier  - one of the constant update identifier.
	 */
	private void updateVisibility(int identifier) {
		if ((identifier & GRAPH) == GRAPH) {
			if (Settings.graphDisplay.get()) {
				remove(core);
				add(core, GBC.grid(0, 6).insets(0, 5).fill(GBC.H));
				add(graph, GBC.grid(0, 8).fill(GBC.B).insets(0, 0, 3, 0)
														.weight(1.0, 1.0));
			} else {
				remove(graph);
				remove(core);
				add(core, GBC.grid(0, 6).insets(0, 5).fill(GBC.H)
														.weight(1.0, 1.0));
			}
		}
		if ((identifier & FOOTER) == FOOTER) {
			if (Settings.footerDisplay.get()) {
				add(footer, GBC.grid(0, 9).insets(0, 3).fill(GBC.H));
			} else {
				remove(footer);
			}
		}
		if ((identifier & SUBTITLE) == SUBTITLE) {
			if (Settings.headerShowSubtitle.get()) {
				if (Settings.headerShowTitle.get()) {
					add(subTitle, GBC.grid(0, 1));
				} else {
					add(subTitle, GBC.grid(0, 1).insets(3, 0, 0, 0));
				}
			} else {
				remove(subTitle);
			}
		}
		if ((identifier & TITLE) == TITLE) {
			title.setVisible(Settings.headerShowTitle.get());
		}
		if ((identifier & ATTEMPTS) == ATTEMPTS) {
			attemptCounter.setVisible(Settings.headerShowAttempts.get());
		}
		revalidate();
		repaint();
	}

	private String sanitizeTitleString(String title) {
		return title
			.replace("<", "&lt;")
			.replace(">", "&gt;")
			.replace("\n", "<br/>");
	}

}
