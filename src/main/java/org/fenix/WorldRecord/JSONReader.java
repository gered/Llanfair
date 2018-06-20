package org.fenix.WorldRecord;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * JSONReader class to fetch JSON from api
 * @author  4ilo 2018
 */
public class JSONReader
{
    /**
     * Get the content of the given reader as a string
     * @param reader a reader object
     * @return String with the data of the reader
     * @throws IOException
     */
    private static String readAll(Reader reader) throws IOException
    {
        StringBuilder builder = new StringBuilder();
        int cp;

        while((cp = reader.read()) != -1)
        {
            builder.append((char) cp);
        }

        return builder.toString();
    }

    /**
     * Read the json from the given json-api url
     * @param url the api url
     * @return  JSONObject with the data from the url
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException
    {
        InputStream stream = new URL(url).openStream();

        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")));
            String jsonText = readAll(reader);

            return new JSONObject(jsonText);

        } finally
        {
            stream.close();
        }
    }
}
