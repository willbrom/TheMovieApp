package com.example.toshiba.themovieapp.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    public static String[][] parseRawJson(String rawJson) {
        String[][] originalTitleArray = null;

        try {
            JSONObject mainBody = new JSONObject(rawJson);
            JSONArray resultsArray = mainBody.getJSONArray("results");
            originalTitleArray = new String[resultsArray.length()][5];
            for (int i = 0; i < originalTitleArray.length; i++) {
                JSONObject resultsObject = resultsArray.getJSONObject(i);
                String originalTitle = resultsObject.getString("original_title");
                String posterPath = resultsObject.getString("poster_path");
                String overview = resultsObject.getString("overview");
                String releaseDate = resultsObject.getString("release_date");
                String voteAverage = resultsObject.getString("vote_average");

                originalTitleArray[i][0] = originalTitle;
                originalTitleArray[i][1] = posterPath;
                originalTitleArray[i][2] = overview;
                originalTitleArray[i][3] = releaseDate;
                originalTitleArray[i][4] = voteAverage;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return originalTitleArray;
    }
}
