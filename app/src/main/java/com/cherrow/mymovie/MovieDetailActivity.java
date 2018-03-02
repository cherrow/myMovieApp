package com.cherrow.mymovie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.net.URL;

public class MovieDetailActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView imageView;
    private TextView directorTextView;
    private TextView genreTextView;
    private TextView summaryTextView;
    private TextView castsTextView;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private Movie movie;
    private FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail);
        final Intent intent=getIntent();
        toolbar =(Toolbar)findViewById(R.id.detail_toolbar);
        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapseToolbar);
        imageView=(ImageView)findViewById(R.id.detail_image);
        directorTextView=(TextView)findViewById(R.id.movie_director_text);
        genreTextView=(TextView)findViewById(R.id.movie_genre_text);
        summaryTextView=(TextView)findViewById(R.id.movie_summary_text);
        castsTextView=(TextView)findViewById(R.id.movie_casts_text);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.detail_floatingBtn);
        setSupportActionBar(toolbar);
        actionBar=getSupportActionBar();
        if(actionBar!=null){
           actionBar.setDisplayHomeAsUpEnabled(true);
        }
        movie=(Movie)intent.getSerializableExtra("movie");
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMovieTrailer();
            }
        });
        collapsingToolbarLayout.setTitle(movie.getName());
        Glide.with(MovieDetailActivity.this)
                .load(movie.getImagePath(HttpUtil.ImageSize.LARGE))
                .into(imageView);
        MovieDetailTask movieDetailTask=new MovieDetailTask();
        movieDetailTask.execute();

    }

    private void SetDetailInfo(){
        //导演
        String[] directors=movie.getDirectors();
        String directorText="导演：";
        if(directors!=null){
            for(int i=0;i<directors.length;i++){
                directorText+=directors[i]+" ";
            }
        }
        directorTextView.setText(directorText);
        //类型
        String[] genres=movie.getGenres();
        String genreText="类型：";
        if(genres!=null){
            for(int i=0;i<genres.length;i++){
                genreText+=genres[i]+" ";
            }
        }
        genreTextView.setText(genreText);
        //主演
        String[] casts=movie.getCastsName();
        String castText="主演：";
        if(casts!=null){
            for(int i=0;i<casts.length;i++){
                castText+=casts[i]+" ";
            }
        }
        castsTextView.setText(castText);
        //摘要
        if(movie.getSummary()!=null){
            summaryTextView.setText("简介："+movie.getSummary());
        }
        else{
            summaryTextView.setText("简介：");
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private  class MovieDetailTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SetDetailInfo();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpUtil.SetMovieDetailInfo(movie,movie.getMovieID());
            return null;
        }
    }

    private class MovieTrailerTask extends AsyncTask<URL,Void,Uri>{
        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);
            if(uri==null||!uri.toString().contains(".mp4")){
                Toast.makeText(MovieDetailActivity.this,"该影片没有预告片",Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "video/mp4");
            startActivity(intent);
        }

        public MovieTrailerTask() {
            super();
        }

        @Override
        protected Uri doInBackground(URL... params) {
            Uri uri=HttpUtil.GetTrailerUri(params[0]);
            return uri;
        }
    }

    private void playMovieTrailer(){
        boolean isMobileNet=HttpUtil.isMobile(MovieDetailActivity.this);
        if(isMobileNet==true&&HttpUtil.isWiFi(MovieDetailActivity.this)==false){
            AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailActivity.this);
            builder.setCancelable(false);
            builder.setTitle("提示");
            builder.setMessage("您现在正在使用移动网络，是否继续？");
            builder.setPositiveButton("确认",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            try{
                                URL url=new URL(movie.getMovieUrl());
                                MovieTrailerTask movieTrailerTask=new MovieTrailerTask();
                                movieTrailerTask.execute(url);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
            builder.show();
        }
        else {
            try{
                URL url=new URL(movie.getMovieUrl());
                MovieTrailerTask movieTrailerTask=new MovieTrailerTask();
                movieTrailerTask.execute(url);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
