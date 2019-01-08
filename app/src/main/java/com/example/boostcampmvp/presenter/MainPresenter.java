package com.example.boostcampmvp.presenter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.customtabs.CustomTabsIntent;

import com.example.boostcampmvp.MainActivity;
import com.example.boostcampmvp.R;
import com.example.boostcampmvp.adapter.MovieAdapterContractor;
import com.example.boostcampmvp.adapter.OnItemClickListener;
import com.example.boostcampmvp.data.Movie;
import com.example.boostcampmvp.data.MovieResourceRepository;
import com.example.boostcampmvp.util.NetworkStatusChecker;

public class MainPresenter implements MainContractor.Presenter, OnItemClickListener {

    private static final int ACTIVITY_DELAY = 350;

    private MainContractor.View mMainView;

    private MovieResourceRepository mMovieRepository;

    private MovieAdapterContractor.View mMovieAdapterView;
    private MovieAdapterContractor.Model mMovieAdapterModel;

    private String mCurrentKeyWord;
    private boolean isSearchingNow = false;

    private Handler handler = new Handler();


    @Override
    public void attachView(MainContractor.View view) {
        this.mMainView = view;
    }

    @Override
    public void detachView() {
        this.mMainView = null;
    }

    @Override
    public void setMovieRepository(MovieResourceRepository movieRepository) {
        this.mMovieRepository = movieRepository;
    }

    @Override
    public void setMovieAdapterView(MovieAdapterContractor.View view) {
        this.mMovieAdapterView = view;
        this.mMovieAdapterView.setOnItemClickListener(this);
    }

    @Override
    public void setMovieAdapterModel(MovieAdapterContractor.Model model) {
        this.mMovieAdapterModel = model;
    }

    @Override
    public void searchMovie(Context context, String keyword) {

        if(keyword.isEmpty()) {
            mMainView.showToastMessage("검색어를 먼저 입력해주세요 !");
            return;
        } else if(!NetworkStatusChecker.isNetworkConnected(((MainActivity)mMainView).getApplicationContext())) {
            mMainView.showToastMessage("인터넷 연결 안되어있습니다");
            return;
        }

        mMainView.closeKeypad();
        mMainView.startProgressDialog(String.format("%s 영화를 검색중입니다 !", keyword));

        mCurrentKeyWord = keyword;

        mMovieRepository.getMovieData(context, keyword, 1, items -> {

            if(items == null) {
                mMainView.showToastMessage("검색중 오류가 발생했습니다");
            } else if(items.size() == 0) {
                mMainView.showToastMessage("검색 결과가 없습니다 ㅠ");
            } else{
                mMovieAdapterModel.clearMovies();
                mMovieAdapterModel.addMovies(items);
                // 이전 데이터들 이미지를 지우고 새로운 데이터를 갱신
                mMovieAdapterView.notifyAdapter();
            }

            mMainView.endProgressDialog();
        });
    }

    @Override
    public void loadMoreMovies(Context context, int startPos) {

        if(!isSearchingNow && NetworkStatusChecker.isNetworkConnected(((MainActivity)mMainView).getApplicationContext())) {
            isSearchingNow = true;

            mMovieRepository.getMovieData(context, mCurrentKeyWord, startPos + 1, (items) -> {
                if(items != null) {
                    mMovieAdapterModel.addMovies(items);
                    mMovieAdapterView.notifyAdapterFromTo(startPos, mMovieAdapterModel.getSize());
                }
                isSearchingNow = false;
            });
        }
    }

    @Override
    public void onClick(int position) {
        // item click event handle
        final CustomTabsIntent intent = new CustomTabsIntent.Builder().
                setToolbarColor(((MainActivity)mMainView).getResources().getColor(R.color.mint)).build();

        final String url = mMovieAdapterModel.getMovieItem(position).getLink();

        handler.postDelayed( ()-> {
            intent.launchUrl(((MainActivity)mMainView), Uri.parse(url));
            ((MainActivity)mMainView).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }, ACTIVITY_DELAY);
    }
}
