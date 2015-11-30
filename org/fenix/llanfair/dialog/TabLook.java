package org.fenix.llanfair.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fenix.llanfair.Language;
import org.fenix.llanfair.Llanfair;
import org.fenix.llanfair.config.Settings;
import org.fenix.utils.gui.GBC;

/**
 * A panel allowing the user the change the settings related to the look of
 * the application: colors, fonts and fonts’ sizes.
 *
 * @author  Xavier "Xunkar" Sencert
 */
class TabLook extends SettingsTab implements ActionListener, ChangeListener  {
    
    // -------------------------------------------------------------- CONSTANTS
    
    /**
     * Dimension of the revert button.
     */
    private static final Dimension REVERT_SIZE = new Dimension(18, 18);
    
    // ------------------------------------------------------------- ATTRIBUTES
    
    /**
     * List of all color buttons, inserted in the order of their name.
     */
    private List<ColorButton> colorButtons;
    
    /**
     * List of labels displaying the name of the color button.
     */
    private List<JLabel> colorTexts;
    
    /**
     * Panel allowing the user to select a color.
     */
    private JColorChooser colorChooser;
    
    /**
     * The index of the currently selected color button. Any color change will 
     * be made to this button.
     */
    private int selected;
    
    /**
     * Label displaying a text explaining to the user that he must first
     * select a color button before editing its color.
     */
    private JLabel helperText;
    
    /**
     * Panel listing the color buttons and their name label.
     */
    private JPanel colorPanel;

    /**
     * Small button displayed next to the selected color button and resetting
     * its color to its original value.
     */
    private JButton revert;
    
    /**
     * Invisible panel the size of the revert button used to take up space when
     * the button is not showing.
     */
    private JPanel placeHolder;
    
    // ----------------------------------------------------------- CONSTRUCTORS
    
    /**
     * Creates the "Look" settings tab. Only called by {@link EditSettings}.
     */
    TabLook() {
        selected     = -1;
        helperText   = new JLabel("" + Language.TT_COLOR_PICK);
        colorTexts   = new ArrayList<JLabel>();
        colorButtons = new ArrayList<ColorButton>();

        int index = 0;
        for (Settings.Property<?> colorSetting : Settings.getAll("color")) {
            ColorButton colorButton = new ColorButton(
                    index, (Settings.Property<Color>) colorSetting
            );
            colorButton.addActionListener(this);
            colorButtons.add(colorButton);
            colorTexts.add(new JLabel("" + colorSetting));
            index++;
        }
        
        colorChooser = new JColorChooser();
        colorChooser.setPreviewPanel(new JPanel());
        colorChooser.getSelectionModel().addChangeListener(this);
        colorChooser.setEnabled(false);
        
        revert = new JButton(Llanfair.getResources().getIcon("REVERT"));
        revert.setPreferredSize(REVERT_SIZE);
        revert.addActionListener(this);
        
        placeHolder = new JPanel();
        placeHolder.setPreferredSize(REVERT_SIZE);
        
        place();
    }
    
    // -------------------------------------------------------------- CALLBACKS
    
    /**
     * When a color button is pressed, we bring up the color chooser and
     * change the color of the text indicate it’s been selected. We also 
     * display the revert button next to the selected color. If the event
     * emanates from a revert button, we revert its associated color.
     */
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source.equals(revert)) {
            colorButtons.get(selected).resetColor();
        } else {
            if (selected != -1) {
                colorTexts.get(selected).setForeground(Color.BLACK);
                colorPanel.remove(revert);
            } else {
                colorPanel.remove(placeHolder);
                colorChooser.setEnabled(true);
            }
            selected = ((ColorButton) source).getIndex();
            colorTexts.get(selected).setForeground(Color.RED);
            colorPanel.add(revert, GBC.grid(2, selected).insets(0, 2));
            colorChooser.setColor(colorButtons.get(selected).getColor());
            revalidate();
        }
    }
    
    /**
     * When a color is selected in the color chooser, update the selected
     * color button (if any.)
     */
    public void stateChanged(ChangeEvent event) {
        if (selected != -1) {
            colorButtons.get(selected).setColor(colorChooser.getColor());
        }
    }
    
    // -------------------------------------------------------------- INHERITED
    
    @Override void doDelayedSettingChange() {}
    
    /**
     * Returns the localized name of this tab.
     */
    @Override public String toString() {
        return "" + Language.COLORS;
    }
    
    // -------------------------------------------------------------- UTILITIES
    
    /**
     * Places all sub-components within this panel.
     */
    private void place() {
        setLayout(new GridBagLayout());
        
        colorPanel = new JPanel(new GridBagLayout()); {
            for (int row = 0; row < colorButtons.size(); row++) {
                colorPanel.add(
                        colorTexts.get(row), 
                        GBC.grid(0, row).anchor(GBC.LE).insets(1, 4)
                );
                colorPanel.add(
                        colorButtons.get(row), GBC.grid(1, row).insets(2, 0)
                );
            }
            colorPanel.add(placeHolder, GBC.grid(2, 0).insets(0, 2));
        }
        JPanel swatchPanel = new JPanel(new GridBagLayout()); {
            swatchPanel.add(colorChooser, GBC.grid(0, 0));
            swatchPanel.add(helperText, GBC.grid(0, 1));
        }
        add(colorPanel, GBC.grid(0, 0).fill(GBC.B));
        add(swatchPanel, GBC.grid(1, 0).fill(GBC.B));
    }
    
    // --------------------------------------------------------- INTERNAL TYPES
    
    /**
     * A kind of {@code JButton} that displays a color setting by painting 
     * itself in that color. When the color is programatically set, the color
     * setting is automatically updated by the button. Each button stores an
     * index only valid in the owning view’s context and the initial value it
     * was constructed with, allowing for a reset. 
     * 
     * @author  Xavier "Xunkar" Sencert
     */
    class ColorButton extends JButton implements Comparable<ColorButton> {

        // ---------------------------------------------------- ATTRIBUTES
        
        /**
         * Index of this color button in the englobing model.
         */
        private int index;

        /**
         * Color currently represented by this button.
         */
        private Color color;
        
        /**
         * Color initially represented by this button. Used to revert to the
         * setting initial state.
         */
        private Color initialColor;
        
        /**
         * Color setting that this button represent. When the user selectes
         * a new color, this setting is updated with the user's choice.  
         */
        private Settings.Property<Color> setting; 
        
        // -------------------------------------------------- CONSTRUCTORS

        /**
         * Creates a button representing the given setting. An index must be
         * supplied to identify this button. The name of the setting becomes
         * the name of the button.
         * 
         * @param   index   - the index of this button.
         * @param   setting - the setting represented by this button.
         */
        ColorButton(int index, Settings.Property<Color> setting) {
            super(".");
            
            color        = setting.get();
            initialColor = color;
            this.index   = index;
            this.setting = setting;
            
            setName(setting.getKey());
        }

        // ------------------------------------------------------- GETTERS

        /**
         * Returns the current color of this button.
         * 
         * @return  the current color.
         */
        Color getColor() {
            return color;
        }
        
        /**
         * Returns the index of this button.
         * 
         * @return  the index of this button.
         */
        int getIndex() {
            return index;
        }

        // ------------------------------------------------------- SETTERS

        /**
         * Sets the color that this button should display. Updates the setting
         * and repaints itself.
         * 
         * @param   color   - the new color to display.
         */
        void setColor(Color color) {
            this.color = color;
            setting.set(color);
            repaint();
        }
        
        /**
         * Resets the color to be displayed by this button to the initial
         * color it was constructed with.
         */
        void resetColor() {
            setColor(initialColor);
        }
        
        // ----------------------------------------------------- INHERITED
        
        /**
         * Two {@code ColorButton}s are compared using the lexicographic 
         * comparison of their name.
         */
        public int compareTo(ColorButton o) {
            if (o == null) {
                return 1;
            }
            return getName().compareTo(o.getName());
        }

        /**
         * A {@code ColorButton} paints itself in the color he should 
         * display and does not display any string of text.
         */
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(color);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

}
