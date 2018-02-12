package org.fenix.WorldRecord;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class RecordDialog extends JDialog
{
    private JLabel searchLabel = new JLabel("Search game:");
    private JTextField searchField = new JTextField();
    private JButton searchButton = new JButton("Search");

    private JLabel gamesLabel = new JLabel("Games:");
    private DefaultComboBoxModel<Game> gameListModel = new DefaultComboBoxModel<>();
    private JComboBox<Game> games = new JComboBox<>(gameListModel);

    private JLabel categoriesLabel = new JLabel("Categories:");
    private DefaultComboBoxModel<Category> categoryListModel = new DefaultComboBoxModel<>();
    private JComboBox<Category> categories = new JComboBox<>(categoryListModel);

    private JLabel worldRecord = new JLabel();
    private JButton ok = new JButton("Ok");
    private JButton close = new JButton("Close");

    private String category_id = "";

    public RecordDialog()
    {
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

            games.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    getCategories((Game) games.getSelectedItem());
                }
            });
        }
        JPanel categoriesPanel = new JPanel(new GridLayout(1,2));
        {
            categoriesPanel.add(categoriesLabel);
            categoriesPanel.add(categories);

            categories.setEnabled(false);

            categories.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    getWorldRecord((Category) categories.getSelectedItem());
                }
            });
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

    private void searchGame(String name)
    {
        ArrayList<Game> games = new ArrayList<>();

        try {
            games = WorldRecordParser.searchGames(name);
        } catch (IOException e)
        {
            System.out.println("fout");
        }

        this.setGames(games);
    }

    private void getCategories(Game game)
    {
        ArrayList<Category> categories = new ArrayList<>();

        try {
            categories = WorldRecordParser.getCategories(game);
        } catch (IOException e)
        {
            System.out.println("fout");
        }

        this.setCategories(categories);
    }

    private void getWorldRecord(Category category)
    {
        String worldRecord = "";

        try {
            worldRecord = WorldRecordParser.getRecord(category);
        } catch (IOException e)
        {
            System.out.println("fout");
        }

        this.worldRecord.setText(worldRecord);
        this.category_id = category.getId();
    }

    private void setGames(ArrayList<Game> games)
    {
        gameListModel.removeAllElements();

        for(Game game: games)
        {
            gameListModel.addElement(game);
        }

        this.games.setEnabled(true);
    }

    private void setCategories(ArrayList<Category> categories)
    {
        categoryListModel.removeAllElements();

        for(Category category: categories)
        {
            categoryListModel.addElement(category);
        }

        this.categories.setEnabled(true);
    }

    private void close()
    {
        this.setVisible(false);
    }

    private void actionOk()
    {
        this.category_id = ((Category) categories.getSelectedItem()).getId();
        this.setVisible(false);
    }
}
