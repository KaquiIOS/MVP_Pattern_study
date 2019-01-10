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

/*
* Prensenter는 Contract.Presenter를 상속받고 Contractor.View를 가진다.
* 또한, Repository와도 상호교류를 해야하므로 Presenter만 Repository 객체를 가진다.
* Presenter와 Repository 등.. 의 것들은 iew가 생성될 때 등록을 해준다.
 */
public class MainPresenter implements MainContractor.Presenter, OnItemClickListener {

    private static final int ACTIVITY_DELAY = 350;

    // View에 대한 참조
    private MainContractor.View mMainView;

    // Repositroy에 대한 참조
    private MovieResourceRepository mMovieRepository;

    // Adapter의 추상화를 통해 Presenter -> View -> Update 과정을 Presenter -> Update 과정으로 간소화 했다.
    // Adapter View(실제 반응과 관련되 - 클릭, notify 등..
    private MovieAdapterContractor.View mMovieAdapterView;

    // Adapter Model(데이터의 변경, 삭제, 추가, 읽기 등..) 참조
    private MovieAdapterContractor.Model mMovieAdapterModel;

    private String mCurrentKeyWord;
    private boolean isSearchingNow = false;
    private boolean isEnd = false;

    private Handler handler = new Handler();

    // View를 Presenter에 등록
    @Override
    public void attachView(MainContractor.View view) {
        this.mMainView = view;
    }

    // View를 Presenter에서 취소
    @Override
    public void detachView() {
        this.mMainView = null;
    }

    // Repository 설정
    @Override
    public void setMovieRepository(MovieResourceRepository movieRepository) {
        this.mMovieRepository = movieRepository;
    }

    // AdapterView 설정
    @Override
    public void setMovieAdapterView(MovieAdapterContractor.View view) {
        this.mMovieAdapterView = view;
        this.mMovieAdapterView.setOnItemClickListener(this);
    }

    // AdapterModel 설정
    @Override
    public void setMovieAdapterModel(MovieAdapterContractor.Model model) {
        this.mMovieAdapterModel = model;
    }

    // 영화 검색 함수
    @Override
    public void searchMovie(Context context, String keyword) {

        // 예외 처리 - 빈칸, 인터넷 연결이 안된경우
        if(keyword.isEmpty()) {
            mMainView.showToastMessage("검색어를 먼저 입력해주세요 !");
            return;
        } else if(!NetworkStatusChecker.isNetworkConnected(((MainActivity)mMainView).getApplicationContext())) {
            mMainView.showToastMessage("인터넷 연결 안되어있습니다");
            return;
        }

        isEnd = false;

        // 입력 캐패드 종료
        mMainView.closeKeypad();

        // Dialog 시작
        mMainView.startProgressDialog(String.format("%s 영화를 검색중입니다 !", keyword));

        // 무한 스크롤을 구현하기 위해서 이전 검색어를 저장해둔다
        mCurrentKeyWord = keyword;

        // 각 검색의 결과에 따른 행동들을 정의
        mMovieRepository.getMovieData(context, keyword, 1, (items, isEnd) -> {

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

            this.isEnd = isEnd;

            mMainView.endProgressDialog();
        });
    }

    @Override
    public void loadMoreMovies(Context context, int startPos) {

        // 이미 검색된 영화가 있고, 스크롤을 할 때 다음 데이터를 덧붙이는 과정
        if(!isEnd && !isSearchingNow && NetworkStatusChecker.isNetworkConnected(((MainActivity)mMainView).getApplicationContext())) {
            // lock을 얻는 모양과 비슷하게 구현해봄
            isSearchingNow = true;

            mMovieRepository.getMovieData(context, mCurrentKeyWord, startPos + 1, (items, isEnd) -> {
                if(items != null) {
                    mMovieAdapterModel.addMovies(items);
                    mMovieAdapterView.notifyAdapterFromTo(startPos, mMovieAdapterModel.getSize());
                }
                this.isEnd = isEnd;
                isSearchingNow = false;
            });
        }
    }

    // RecyclerView에서 발생하는 clickEvent를 Adapter에서 정의한 Interface 이벤트로 구현
    // 클릭 이벤트도 Presenter에서 구현한다.
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
