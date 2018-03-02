package com.cherrow.mymovie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.io.InputStream;
import java.net.HttpURLConnection;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/18.
 */



public class HttpUtil {
    public static void sendRequst(String address , Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    public enum ImageSize{
            LARGE,MEDIUM,SMALL
    }

    String  imagePathLarge="";
    String  imagePathMedium="";
    String  imagePathSmall="";

    public static int movieTotalCount=0;

    public  List<Movie> parseJSON(String jsonData){
        List<Movie> movieList=new ArrayList<>();
        try{
            JSONObject jsonObject=new JSONObject(jsonData);
            if(jsonObject.has("subjects")==false){
                Toast.makeText(MyApplication.getContext(),"请求次数达到上限",Toast.LENGTH_SHORT).show();
                return null;
            }
            JSONArray jsonArray=jsonObject.getJSONArray("subjects");
            String totalCount=jsonObject.getString("total");
            movieTotalCount=Integer.parseInt(totalCount);
            for (int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonSubject=jsonArray.getJSONObject(i);
                if(jsonSubject.getString("subtype").equals("movie")==false){
                    continue;
                }
                String name=jsonSubject.getString("title");//电影名
                int movieID=jsonSubject.getInt("id");//电影ID
                JSONObject rateJson=jsonSubject.getJSONObject("rating");//电影评分
                String rate=rateJson.getString("average") ;//评分
                JSONObject imageJson=jsonSubject.getJSONObject("images");
                imagePathLarge=imageJson.getString("large");
                imagePathMedium=imageJson.getString("medium");
                imagePathSmall=imageJson.getString("small");
                Movie movie=new Movie(name,rate);
                movie.setImagePathLarge(imagePathLarge);
                movie.setImagePathMedium(imagePathMedium);
                movie.setImagePathSmall(imagePathSmall);
                movie.setMovieID(movieID);
                movieList.add(movie);
            }
        }catch (Exception e){
            Toast.makeText(MyApplication.getContext(),"请求次数达到上限",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return movieList;
    }

    public static Bitmap getHttpBitmap(final String url){
        Bitmap bitmap=null;
                try{
                    URL myFileURL;
                    myFileURL = new URL(url);
                    HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
        return bitmap;
    }

    public static void SetMovieDetailInfo(Movie movie,int movieID){
        String movieApiPath="https://api.douban.com/v2/movie/subject/"+movieID;
        try
        {
            OkHttpClient client =new OkHttpClient();
            Request request=new Request.Builder().url(movieApiPath).build();
            Response responsel=client.newCall(request).execute();
            JSONObject jsonSubject=new JSONObject(responsel.body().string());
            String summary=jsonSubject.getString("summary");//摘要
            JSONArray arrayCast=jsonSubject.getJSONArray("casts");//主演
            String[] castsName=new String[arrayCast.length()];
            for (int i=0;i<arrayCast.length();i++){
                JSONObject jsonCast=arrayCast.getJSONObject(i);
                castsName[i]=jsonCast.getString("name");
            }
            JSONArray arrayGenres=jsonSubject.getJSONArray("genres");
            String[] genres=new String[arrayGenres.length()];//类型
            for(int i=0;i<arrayGenres.length();i++){
                genres[i]=arrayGenres.get(i).toString();
            }
            JSONArray arrayDirectors=jsonSubject.getJSONArray("directors");
            String[] directors=new String[arrayDirectors.length()];//导演
            for (int i=0;i<arrayDirectors.length();i++){
                JSONObject jsonDirector=arrayDirectors.getJSONObject(i);
                directors[i]=jsonDirector.getString("name");
            }
            String movieUrl=jsonSubject.getString("mobile_url");//电影介绍网址

            movie.setSummary(summary);
            movie.setCastsName(castsName);
            movie.setGenres(genres);
            movie.setDirectors(directors);
            movie.setMovieUrl(movieUrl);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Uri GetTrailerUri(URL url){
        try{
            Uri trailerUrl;
            Document document= Jsoup.parse(url,5000);
            Elements elements=document.select("div.page").select("div#subject_page");
            trailerUrl=Uri.parse(elements.select("div#base").select("div.cover").select("a").attr("href"));
            return trailerUrl;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }


    public static String GetBingPic(){
        sendRequst("http://guolin.tech/api/bing_pic", new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
               final String bingPic=response.body().string();
                SharedPreferences.Editor editor= PreferenceManager
                        .getDefaultSharedPreferences(MyApplication.getContext())
                        .edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
            }
        });
        SharedPreferences prefs=PreferenceManager .getDefaultSharedPreferences(MyApplication.getContext());
        String bingpic=prefs.getString("bing_pic",null);
       return bingpic;
    }

    //判断网络连接是否可用
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
        } else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null&&networkInfo.length>0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //判断WiFi是否打开
    public static boolean isWiFi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    //判断移动数据是否打开
    public static boolean isMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

}
