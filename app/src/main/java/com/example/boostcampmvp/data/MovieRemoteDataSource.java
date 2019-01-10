package com.example.boostcampmvp.data;

import android.content.Context;

import com.example.boostcampmvp.util.MovieGetAsynTask;

// 추상화를 통해 Remote, Local Data 를 가져오는 방법을 각각 정의한다
// 여기서는 Data를 가져오는 Util class 를 따로 작성하였고, 결과를 callback을 통해 반환한다.
public class MovieRemoteDataSource implements MovieSource {

    @Override
    public void getMovieData(Context context, String title, int startPos, MovieLoadCallback callback) {
        // Lambda 식을 이용하여 가독성을 높인다
        new MovieGetAsynTask(context, (items, isEnd)-> {
            if(callback != null)
                callback.onMovieLoaded(items, isEnd);
        }).execute(new String[]{title, Integer.toString(startPos)});
    }
}
