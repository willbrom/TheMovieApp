package com.example.toshiba.themovieapp.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();

    public static String[][] parseRawJson(String rawJson) {
        String[][] originalTitleArray = null;

        try {
            JSONObject mainBody = new JSONObject(rawJson);
            JSONArray resultsArray = mainBody.getJSONArray("results");
            originalTitleArray = new String[resultsArray.length()][6];
            for (int i = 0; i < originalTitleArray.length; i++) {
                JSONObject resultsObject = resultsArray.getJSONObject(i);
                String originalTitle = resultsObject.getString("original_title");
                String posterPath = resultsObject.getString("poster_path");
                String overview = resultsObject.getString("overview");
                String releaseDate = resultsObject.getString("release_date");
                String voteAverage = resultsObject.getString("vote_average");
                String id = resultsObject.getString("id");

                originalTitleArray[i][0] = originalTitle;
                originalTitleArray[i][1] = posterPath;
                originalTitleArray[i][2] = overview;
                originalTitleArray[i][3] = releaseDate;
                originalTitleArray[i][4] = voteAverage;
                originalTitleArray[i][5] = id;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return originalTitleArray;
    }

    public static String[] parsedJsonTrailers(String rawJson) {
        String[] trailerPath = null;
        try {
            JSONObject mainBody = new JSONObject(rawJson);
            JSONArray results = mainBody.getJSONArray("results");
            JSONObject resultsObject1 = results.getJSONObject(0);
            JSONObject resultsObject2 = results.getJSONObject(1);
            String path1 =resultsObject1.getString("key");
            String path2 =resultsObject2.getString("key");
            Log.d(TAG, "The path of trailer is: " + path1);
            trailerPath = new String[]{path1, path2};
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailerPath;
    }

    public static String[] parseReviewJson(String rawJson) {
        String[] reviews = null;
        try {
            JSONObject mainObject = new JSONObject(rawJson);
            JSONArray results = mainObject.getJSONArray("results");
            reviews = new String[results.length()];
            for (int i = 0; i < results.length();i++) {
                JSONObject resultsObj = results.getJSONObject(i);
                reviews[i] = resultsObj.getString("content");
                Log.d(TAG, "Content at " + i + ": " + reviews[i]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }

}
