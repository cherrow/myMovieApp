package com.cherrow.mymovie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2017/3/18.
 */

public class Movie implements java.io.Serializable  {
    /* 名字 */
    private String name;
    //海报
    //private Bitmap image;
    //评分
    private String rate;
    //高分辨海报地址
    private String imagePathLarge="";
    //中等分辨
    private String imagePathMedium="";
    //小分辨
    private String imagePathSmall="";
    //电影ID
    private int movieID;
    //摘要
    private String summary;
    //主演
    private String[] castsName;
    //类型
    private String[] genres;
    //导演
    private String[] directors;
    //电影介绍地址
    private String movieUrl;

  //  private byte[] imageByte;

    public Movie(String name, String rate){
        this.name=name;
        this.rate=rate;
    }

    public Bitmap Bytes2Bitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
                return baos.toByteArray();
            }

    public int getMovieID(){
        return movieID;
    }

    public void setMovieID(int movieID){
        this.movieID=movieID;
    }

    public String getName(){
        return name;
    }

    public String getRate(){
        return rate;
    }

    public String getImagePath(HttpUtil.ImageSize imageSize){
        switch (imageSize){
            case LARGE:
                return imagePathLarge;
            case MEDIUM:
                return imagePathMedium;
            case SMALL:
                return imagePathSmall;
        }
        return imagePathMedium;
    }

    public String getSummary(){
        return summary;
    }

    public String[] getCastsName(){
        return castsName;
    }

    public String[] getGenres(){
        return genres;
    }

    public String[] getDirectors(){
        return directors;
    }

    public String getMovieUrl(){
        return movieUrl;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setRate(String rate){
        this.rate=rate;
    }

    public void setImagePathLarge(String imagePathLarge){
            this.imagePathLarge=imagePathLarge;
    }

    public void setImagePathMedium(String imagePathMedium){
        this.imagePathMedium=imagePathMedium;
    }

    public void setImagePathSmall(String imagePathSmall){
        this.imagePathSmall=imagePathSmall;
    }

    public void setSummary(String summary){this.summary=summary;}

    public void setCastsName(String[] castsName) {
        this.castsName=castsName;
    }

    public void setGenres(String[] genres){
        this.genres=genres;
    }

    public  void setDirectors(String[] directors){
        this.directors=directors;
    }

    public void setMovieUrl(String movieUrl){
        this.movieUrl=movieUrl;
    }

}
