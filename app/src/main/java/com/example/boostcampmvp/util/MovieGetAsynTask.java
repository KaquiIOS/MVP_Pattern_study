package com.example.boostcampmvp.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.boostcampmvp.R;
import com.example.boostcampmvp.data.Movie;
import com.example.boostcampmvp.data.MovieBuilder;
import com.example.boostcampmvp.data.MovieSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MovieGetAsynTask extends AsyncTask<String, Void, List<Movie>> {

    private static final String API_URL = "https://openapi.naver.com/v1/search/movie.json";
    private static final String ENCODING = "utf-8";
    private static final String METHOD = "GET";
    private static final int TIME_LIMIT = 10;


    private static final String MOVIE_ASYN_TASK_TAG = "MOVIE_ASYN_TASK";

    private Context context;
    private MovieSource.MovieLoadCallback movieLoadCallback;

    private boolean isEnd = false;

    public MovieGetAsynTask(Context context, MovieSource.MovieLoadCallback movieLoadCallback) {
        this.context = context;
        this.movieLoadCallback = movieLoadCallback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Movie> doInBackground(String... strings) {
        // HTTP 통신을 통해 이미지를 받아온다.
        try {

            String query = API_URL +
                    "?query=" + URLEncoder.encode(strings[0], ENCODING) +
                    "&start=" + URLEncoder.encode(strings[1], ENCODING) ;

            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(METHOD);
            conn.setConnectTimeout(1000 * TIME_LIMIT);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-Naver-Client-Id", context.getString(R.string.naver_movie_key_id));
            conn.setRequestProperty("X-Naver-Client-Secret", context.getString(R.string.naver_movie_key_pw));

            if(conn.getResponseCode() == 200) {

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), ENCODING));

                StringBuilder sb = new StringBuilder();
                String line = "";

                while((line = bufferedReader.readLine()) != null)
                    sb.append(line);

                return parseJson(sb.toString(), Integer.parseInt(strings[1]));
            } else {
                Log.e(MOVIE_ASYN_TASK_TAG, conn.getResponseMessage());
                Log.e(MOVIE_ASYN_TASK_TAG, conn.getResponseCode() + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);
        movieLoadCallback.onMovieLoaded(movies, isEnd);
    }

    private List<Movie> parseJson(String json, int startPos) {

        List<Movie> parseResult = new ArrayList<>();

        // Json 에서 문제가 발생하는 경우
        if(json == null || json.isEmpty())
            return parseResult;

        try {

            JSONObject jsonObject = new JSONObject(json);

            if(jsonObject.getInt("total") <= startPos) {
                isEnd = true;
                return parseResult;
            }

            JSONArray array = jsonObject.getJSONArray("items");

            int len = array.length();

            MovieBuilder movieBuilder = new MovieBuilder();

            for(int i = 0; i < len; ++i) {
                JSONObject obj = array.getJSONObject(i);

                // 생성 부분을 Builder 로 수정
                Movie movie = movieBuilder
                        .setImageURL(obj.getString("image"))
                        .setLink(obj.getString("link"))
                        .setTitle(obj.getString("title"))
                        .setPubDate(obj.getString("pubDate"))
                        .setDirector(obj.getString("director"))
                        .setActors(obj.getString("actor"))
                        .setUserRating(obj.getDouble("userRating"))
                        .build();

                parseResult.add(movie);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parseResult;
    }
}
