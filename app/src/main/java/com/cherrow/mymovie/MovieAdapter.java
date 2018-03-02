package com.cherrow.mymovie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/18.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {

    private Context mContext;
    private List<Movie> movieList;

    static class MovieHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        ImageView movieImage;
        TextView movieName;

        public MovieHolder(View view){
            super(view);
            cardView=(CardView)view;
            movieImage=(ImageView)view.findViewById(R.id.movie_image);
            movieName=(TextView)view.findViewById(R.id.movie_name);
        }
    }

    public MovieAdapter(List<Movie> movieList){
        this.movieList=movieList;
    }

    public MovieHolder onCreateViewHolder(ViewGroup parent,int viewType){
        if(parent==null) mContext=MyApplication.getContext();
        else if(mContext==null) mContext=parent.getContext();
        View view= LayoutInflater.from(mContext).inflate(R.layout.movie_item,parent,false);
        final MovieHolder holder=new MovieHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Movie movie=movieList.get(position);
                Intent intent=new Intent(mContext,MovieDetailActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("movie",movie);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    public void onBindViewHolder(final MovieHolder holder, int position){
        Movie movie=movieList.get(position);
        holder.movieName.setText(movie.getName()+"\n评分："+movie.getRate());
        Glide.with(MyApplication.getContext())
                .load(movie.getImagePath(HttpUtil.ImageSize.LARGE))
                .into(holder.movieImage);
    }

    public int getItemCount(){
        return movieList.size();
    }
}
