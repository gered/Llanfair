package org.fenix.WorldRecord;

import org.fenix.llanfair.Llanfair;
import org.fenix.llanfair.dialog.EditRun;
import org.fenix.llanfair.dialog.LlanfairDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Dialog window to select a world record on speedrun.com
 * @author  4ilo 2018
 */
public class RecordDialog extends LlanfairDialog
{
    final private Llanfair master;

    private JLabel searchLabel = new JLabel("Search game:");
    private JButton searchButton = new JButton("Search");
    private JTextField searchField = new JTextField();

    private DefaultComboBoxModel<Game> gameListModel = new DefaultComboBoxModel<>();
    private JComboBox<Game> games = new JComboBox<>(gameListModel);
    private JLabel gamesLabel = new JLabel("Games:");

    private DefaultComboBoxModel<Category> categoryListModel = new DefaultComboBoxModel<>();
    private JComboBox<Category> categories = new JComboBox<>(categoryListModel);
    private JLabel categoriesLabel = new JLabel("Categories:");

    private JButton close = new JButton("Close");
    private JButton ok = new JButton("Ok");
    private JLabel worldRecord = new JLabel("Unknown World Record");

    private ActionListener categoryListener;
    private ActionListener gameListener;

    private Category category;
    private EditRun editRun;


    public RecordDialog(EditRun editRun, Llanfair master)
    {
        super(editRun);
        this.master = master;
        this.editRun = editRun;

        JPanel searchPanel = new JPanel(new FlowLayout());
        {
            searchField.setPreferredSize(new Dimension(200,30));

            searchPanel.add(searchLabel);
            searchPanel.add(searchField);
            searchPanel.add(searchButton);

            searchButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    searchGame(searchField.getText());
                }
            });
        }
        JPanel gamesPanel = new JPanel(new GridLayout(1,2));
        {
            gamesPanel.add(gamesLabel);
            gamesPanel.add(games);

            games.setEnabled(false);
        }
        JPanel categoriesPanel = new JPanel(new GridLayout(1,2));
        {
            categoriesPanel.add(categoriesLabel);
            categoriesPanel.add(categories);

            categories.setEnabled(false);
        }
        JPanel buttonPanel = new JPanel(new FlowLayout());
        {
            buttonPanel.add(ok);
            buttonPanel.add(close);

            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    actionOk();
                }
            });

            close.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    close();
                }
            });
        }

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        this.add(searchPanel);
        this.add(gamesPanel);
        this.add(categoriesPanel);

        this.add(worldRecord);
        worldRecord.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.add(buttonPanel);

        this.setTitle("World Record selection");
        pack();
    }

    /**
     * Search the game title on speedrun.com
     * @param name  The name of the game
     */
    private void searchGame(String name)
    {
        this.resetFields();

        ArrayList<Game> games = new ArrayList<>();

        try {
            games = WorldRecordParser.searchGames(name);
        } catch (IOException e)
        {
            master.showError("Error searching for matching games from speedrun.com.", e);
        }

        this.setGames(games);
        this.addGameListener();
    }

    /**
     * Get the categories for a game on speedrun.com
     * @param game A game object received from the game search
     */
    private void getCategories(Game game)
    {
        ArrayList<Category> categories = new ArrayList<>();

        try {
            categories = WorldRecordParser.getCategories(game);
        } catch (IOException e)
        {
            master.showError("Error fetching game categories from speedrun.com.", e);
        }

        this.setCategories(categories);
        this.addCategoryListener();
    }

    /**
     * Get the world record time and owner from speedrun.com
     * @param category A category object received from the category search
     */
    private void getWorldRecord(Category category)
    {
        String worldRecord = "";

        try {
            worldRecord = WorldRecordParser.getRecord(category);
        } catch (IOException e)
        {
            master.showError("Error fetching game category world record time/owner from speedrun.com.", e);
        }

        this.worldRecord.setText(worldRecord);
        this.category = category;
    }

    /**
     * Append the games in the array to the games combobox
     * @param games A ArrayList of game objects
     */
    private void setGames(ArrayList<Game> games)
    {
        this.resetFields();

        for(Game game: games)
        {
            gameListModel.addElement(game);
        }

        this.games.setEnabled(true);
    }

    /**
     * Append the categories in the array to the categories checkbox
     * @param categories A ArrayList of Category objects
     */
    private void setCategories(ArrayList<Category> categories)
    {
        categoryListModel.removeAllElements();

        for(Category category: categories)
        {
            categoryListModel.addElement(category);
        }

        this.categories.setEnabled(true);
    }

    /**
     * Close the dialog without saving
     */
    private void close()
    {
        this.setVisible(false);
    }

    /**
     * Close the dialog and send a signal to the parent window
     */
    private void actionOk()
    {
        this.category = ((Category) categories.getSelectedItem());
        this.setVisible(false);

        this.editRun.recordSet();
    }

    /**
     * Add the changeListener to the categories combobox
     */
    private void addCategoryListener()
    {
        categoryListener = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                getWorldRecord((Category) categories.getSelectedItem());
            }
        };

        categories.addActionListener(categoryListener);
    }

    /**
     * Add the changeListener to the games combobox
     */
    private void addGameListener()
    {

        gameListener = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                getCategories((Game) games.getSelectedItem());
            }
        };

        games.addActionListener(gameListener);
    }

    /**
     * Reset the comboboxes
     */
    private void resetFields()
    {
        try {
            categories.removeActionListener(categoryListener);
            games.removeActionListener(gameListener);
        } catch (Exception e){}

        categories.setEnabled(false);
        games.setEnabled(false);

        gameListModel.removeAllElements();
        categoryListModel.removeAllElements();
    }

    /**
     * Get the selected category object
     * @return Category
     */
    public Category getCategory()
    {
        return category;
    }

    /**
     * Get the selected record string
     * @return String
     */
    public String getRecordString()
    {
        return worldRecord.getText();
    }
}
