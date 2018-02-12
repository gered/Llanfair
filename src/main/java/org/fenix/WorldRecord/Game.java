package org.fenix.WorldRecord;

/**
 * Game class to represent a speedrun.com game
 * @author  4ilo 2018
 */
public class Game
{
    private String title;
    private String id;

    public Game(String title, String id)
    {
        this.title = title;
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public String getId()
    {
        return id;
    }

    public String toString()
    {
        return this.title;
    }
}
