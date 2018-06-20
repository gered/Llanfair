package org.fenix.WorldRecord;

/**
 * Category class to represent a speedrun.com category
 * @author  4ilo 2018
 */
public class Category
{
    private String name = "";
    private String id = "";

    public Category(String name, String id)
    {
        this.name = name;
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public String getId()
    {
        return id;
    }

    public String toString()
    {
        return this.name;
    }
}
