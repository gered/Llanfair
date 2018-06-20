package org.fenix.WorldRecord;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author  4ilo 2018
 */
public class WorldRecordParser
{
    /**
     * Search the speedrun.com database for the game with the given name
     * @param name The name of the game you want to search
     * @return List of games
     * @throws IOException
     */
    public static ArrayList<Game> searchGames(String name) throws IOException
    {
        String url = "https://www.speedrun.com/api/v1/games?name=" + name;
        JSONArray json_games;
        ArrayList<Game> games = new ArrayList<>();


        JSONObject json = JSONReader.readJsonFromUrl(url);
        json_games = json.getJSONArray("data");


        for(Object game: json_games)
        {
            JSONObject obj = (JSONObject) game;
            games.add(new Game(
                    obj.getJSONObject("names").get("international").toString(),
                    obj.get("id").toString()
            ));
        }

        return games;
    }

    /**
     * Get the speedrun.com categories for the given game
     * @param game WorldRecord.Game object received from a game search
     * @return List of categories
     * @throws IOException
     */
    public static ArrayList<Category> getCategories(Game game) throws IOException
    {
        String url = "https://www.speedrun.com/api/v1/games/" + game.getId() + "/categories";

        ArrayList<Category> categories = new ArrayList<>();


        JSONObject json = JSONReader.readJsonFromUrl(url);
        JSONArray json_categories = json.getJSONArray("data");

        for(Object category: json_categories)
        {
            JSONObject obj = (JSONObject) category;
            categories.add(new Category(
                    obj.get("name").toString(),
                    obj.get("id").toString()
            ));
        }

        return categories;
    }

    /**
     * Get the world record string for the given speedrun.com category
     * @param category WorldRecord.Category object received from a category search
     * @return The world record string
     * @throws IOException
     */
    public static String getRecord(Category category) throws IOException
    {
        String url = "https://www.speedrun.com/api/v1/categories/" + category.getId() + "/records";

        JSONObject json = JSONReader.readJsonFromUrl(url);
        JSONArray json_runs = json.getJSONArray("data").getJSONObject(0).getJSONArray("runs");

        JSONObject wr_run = json_runs.getJSONObject(0).getJSONObject("run");

        String player_name = getPlayerName(wr_run.getJSONArray("players").getJSONObject(0));

        return "World record: " + parseTime(wr_run.getJSONObject("times").getFloat("primary_t")) + " by " + player_name;
    }

    /**
     * Get the speedrun.com player name for the given player JSONObject
     * @param player    The player JSONObject extracted from a run object
     * @return The players name
     * @throws IOException
     */
    private static String getPlayerName(JSONObject player) throws IOException
    {
        String uri = player.get("uri").toString();
        JSONObject json = JSONReader.readJsonFromUrl(uri);
        return json.getJSONObject("data").getJSONObject("names").get("international").toString();
    }

    /**
     * Parse a time in seconds and miliseconds to a HH:MM:SS.sss format
     * @param time_seconds the time in seconds
     * @return Time in HH:MM:SS.sss format
     */
    private static String parseTime(float time_seconds)
    {
        int hours = (int) time_seconds / 3600;
        int secLeft = (int) time_seconds - hours*3600;

        int minutes = secLeft / 60;
        int seconds = secLeft - minutes * 60;

        String time = "";

        if(hours != 0)
            time += String.format("%02d:", hours);

        time += String.format("%02d:%02d", minutes, seconds);

        if(time_seconds % 1 != 0)
        {
            int miliseconds = Math.round((time_seconds%1) * 1000);
            time += "." + miliseconds;
        }

        return time;
    }
}
