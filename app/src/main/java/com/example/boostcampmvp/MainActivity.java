package com.example.boostcampmvp;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.boostcampmvp.adapter.MovieAdapter;
import com.example.boostcampmvp.data.MovieResourceRepository;
import com.example.boostcampmvp.presenter.MainContractor;
import com.example.boostcampmvp.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity implements MainContractor.View {

    private MainPresenter mMainPresenter;
    private MovieAdapter mMovieAdapter;

    private EditText mKeywordText;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainPresenter = new MainPresenter();
        mMainPresenter.attachView(this);

        mMovieAdapter = new MovieAdapter(this);
        mMainPresenter.setMovieAdapterModel(mMovieAdapter);
        mMainPresenter.setMovieAdapterView(mMovieAdapter);

        // Singleton 객체를 매개변수로 넘겨줌
        mMainPresenter.setMovieRepository(MovieResourceRepository.getInstance());

        RecyclerView recyclerView = findViewById(R.id.recycler_main_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mMovieAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(recyclerView.canScrollVertically(-1))  {
                    mMainPresenter.loadMoreMovies(MainActivity.this, mMovieAdapter.getSize());
                }
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mKeywordText = findViewById(R.id.edt_main_search);

        Button searchBtn = findViewById(R.id.btn_main_search);
        searchBtn.setOnClickListener(v -> {
            mMainPresenter.searchMovie(MainActivity.this, mKeywordText.getText().toString());
        });
    }

    @Override
    public void startProgressDialog(String message) {
        if(!mProgressDialog.isShowing()) {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
    }

    @Override
    public void endProgressDialog() {
        if(mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSnackBarMessage(String message) {
        Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 다이얼로그가 꺼지지 않았다면 다시 띄워주기
        if(mProgressDialog.isShowing())
            mProgressDialog.dismiss();

        mMainPresenter.detachView();
    }
}
