package org.fenix.llanfair.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;

import org.fenix.llanfair.Language;
import org.fenix.llanfair.Run;
import org.fenix.llanfair.Segment;
import org.fenix.llanfair.config.Settings;
import org.fenix.llanfair.Run.State;
import org.fenix.llanfair.Time;
import org.fenix.utils.gui.GBC;
import org.fenix.utils.locale.LocaleEvent;

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
    private static final int GOAL = 0x01;
    
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
     * Label displaying the current goal of run. By default it’s the time of
     * the run we’re comparing against, but it can be a customized string.
     */
    private JLabel goal;

    /**
     * Label describing the goal value being displayed.
     */
    private JLabel goalText;
    
    /**
     * Panel containing both goal value and text.
     */
    private JPanel goalPane;
    
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
        goal            = new JLabel();
        goalText        = new JLabel();
        core            = new Core(run);
        graph           = new Graph(run);
        history         = new History(run);
        footer    = new Footer(run);
        separators      = new ArrayList<JLabel>();
        
        placeComponents();
        setRun(run);
        
        updateValues(TEXT);
        updateColors(ALL);
        updateVisibility(ALL);
        
        title.setFont(RUN_TITLE_FONT);
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
            if (run.getState() == State.READY
                    || run.getState() == State.NULL) {
                updateValues(GOAL | SEPARATOR);
            }
        } else if (Run.NAME_PROPERTY.equals(property)) {
            updateValues(TITLE);
        } else if (Settings.CLR_BACK.equals(property)) {
            updateColors(BACKGROUND);
        } else if (Settings.CLR_FORE.equals(property)) {
            updateColors(TEXT);
        } else if (Settings.CLR_SPRT.equals(property)) {
            updateColors(SEPARATOR);
        } else if (Settings.HST_ROWS.equals(property)) {
            updateValues(SEPARATOR);
        } else if (Settings.CLR_TIME.equals(property)) {
            updateColors(GOAL);
        } else if (Settings.CLR_TITL.equals(property)) {
            updateColors(TITLE);
        } else if (Settings.GNR_COMP.equals(property)) {
            updateValues(GOAL);
        } else if (Settings.GPH_SHOW.equals(property)) {
            updateVisibility(GRAPH);
        } else if (Settings.FOO_SHOW.equals(property)) {
            updateVisibility(FOOTER);
        } else if (Settings.HDR_GOAL.equals(property)) {
            updateVisibility(GOAL);
            updateValues(SEPARATOR);
        } else if (Settings.HDR_TTLE.equals(property)) {
            updateVisibility(TITLE | GOAL);
            updateValues(SEPARATOR);
        } else if (Settings.GNR_ACCY.equals(property)
                || Run.GOAL_PROPERTY.equals(property)) {
            updateValues(GOAL);
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
            updateValues(GOAL);
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
        goalPane = new JPanel(new GridBagLayout()); {
            goalPane.add(goalText, GBC.grid(0, 0));
            goalPane.add(goal, GBC.grid(1, 0).insets(0, 3, 0, 0));
            goalPane.setOpaque(false);
        }
        add(title,GBC.grid(0, 0).insets(3, 0, 1, 0));
        add(createSeparator(), GBC.grid(0, 2).insets(3, 0).fill(GBC.H));
        add(history, GBC.grid(0, 3).fill(GBC.H).insets(0, 5));
        add(createSeparator(), GBC.grid(0, 4).insets(3, 0).fill(GBC.H));
        add(createSeparator(), GBC.grid(0, 6).insets(3, 0, 0, 0).fill(GBC.H));

        updateVisibility(ALL);
    }
    
    /**
     * Updates the values of the group of components specified by the 
     * identifier.
     *
     * @param   identifier  - one of the constant update identifier.
     */
    private void updateValues(int identifier) {
        if ((identifier & GOAL) == GOAL) {
            if (run.getGoal() == null || run.getGoal().equals("")) {
                Time time = run.getTime(Segment.SET);
                goal.setText("" + (time == null ? "???" : time));
            } else {
                goal.setText(run.getGoal());
            }
        }
        if ((identifier & TEXT) == TEXT) {
            goalText.setText("" + Language.GOAL);
        }
        if ((identifier & TITLE) == TITLE) {
            title.setText("" + run.getName());
        }
        if ((identifier & SEPARATOR) == SEPARATOR) {
            boolean hdTitle = Settings.HDR_TTLE.get();
            boolean hdGoal = Settings.HDR_GOAL.get();
            boolean hsRows = history.getRowCount() > 0;

            separators.get(0).setVisible(hdTitle || hdGoal);
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
        if ((identifier & GOAL) == GOAL) {
            goal.setForeground(Settings.CLR_TIME.get());
        }
        if ((identifier & TEXT) == TEXT) {
            goalText.setForeground(Settings.CLR_FORE.get());
        }
        if ((identifier & TITLE) == TITLE) {
            title.setForeground(Settings.CLR_TITL.get());
        }
        if ((identifier & BACKGROUND) == BACKGROUND) {
            setBackground(Settings.CLR_BACK.get());
        }
        if ((identifier & SEPARATOR) == SEPARATOR) {
            Color color = Settings.CLR_SPRT.get();
            for (JLabel separator : separators) {
                separator.setBorder(
                        BorderFactory.createMatteBorder(1, 0, 0, 0, color));
            }
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
            if (Settings.GPH_SHOW.get()) {
                remove(core);
                add(core, GBC.grid(0, 5).insets(0, 5).fill(GBC.H));
                add(graph, GBC.grid(0, 7).fill(GBC.B).insets(0, 0, 3, 0)
                                                        .weight(1.0, 1.0));
            } else {
                remove(graph);
                remove(core);
                add(core, GBC.grid(0, 5).insets(0, 5).fill(GBC.H)
                                                        .weight(1.0, 1.0));
            }            
        }
        if ((identifier & FOOTER) == FOOTER) {
            if (Settings.FOO_SHOW.get()) {
                add(footer, GBC.grid(0, 8).insets(0, 3).fill(GBC.H));
            } else {
                remove(footer);
            }
        }
        if ((identifier & GOAL) == GOAL) {
            if (Settings.HDR_GOAL.get()) {
                if (Settings.HDR_TTLE.get()) {
                    add(goalPane, GBC.grid(0, 1));
                } else {
                    add(goalPane, GBC.grid(0, 1).insets(3, 0, 0, 0));
                }
            } else {
                remove(goalPane);
            }
        }        
        if ((identifier & TITLE) == TITLE) {
            title.setVisible(Settings.HDR_TTLE.get());
        }
        revalidate();
        repaint();
    }

}
