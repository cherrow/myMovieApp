package com.cherrow.mymovie;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.v4.view.GravityCompat.*;

public class MainActivity extends AppCompatActivity {

    public static String MOVIEAPIURL="https://api.douban.com/v2/movie/in_theaters";
    private List<Movie> movieListArray=new ArrayList<>();
    private MovieAdapter adapter;
    private String response;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean canShowDlg=true;
    private RecyclerView recyclerView;
    private boolean isNetworkEnable;
    private GridLayoutManager layoutManager;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ImageView heaer_imageView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("搜索电影");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                canShowDlg=true;
                if(recyclerView.getChildCount()>0) recyclerView.removeAllViews();
                MovieTask task=new MovieTask();
                task.execute("https://api.douban.com/v2/movie/search?q="+query);
                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        final Toolbar toolbar=(Toolbar)findViewById(R.id.toolBar_main);
        setSupportActionBar(toolbar);
        final FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.floatBtn);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);

        isNetworkEnable=HttpUtil.isNetworkAvailable(MainActivity.this);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navView=(NavigationView)findViewById(R.id.nav_view);
        View header_layout=navView.inflateHeaderView(R.layout.nav_header);
        heaer_imageView=(ImageView)header_layout.findViewById(R.id.nav_header_image);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        //左滑菜单
        navView.setCheckedItem(R.id.nav_intheater);
        Glide.with(MainActivity.this).load(HttpUtil.GetBingPic()).skipMemoryCache(true).centerCrop().into(heaer_imageView);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()){
                    case R.id.nav_intheater:
                        canShowDlg=true;
                        MOVIEAPIURL="https://api.douban.com/v2/movie/in_theaters";
                        MovieTask task=new MovieTask();
                        task.execute(new String[]{MOVIEAPIURL});
                        toolbar.setTitle("正在热映");
                        break;
                    case R.id.nav_coming_soon:
                        canShowDlg=true;
                        MOVIEAPIURL="https://api.douban.com/v2/movie/coming_soon";
                        MovieTask task_coming=new MovieTask();
                        task_coming.execute(new String[]{MOVIEAPIURL});
                        toolbar.setTitle("即将上映");
                        break;
                    case R.id.nav_top250:
                        canShowDlg=true;
                        MOVIEAPIURL="https://api.douban.com/v2/movie/top250";
                        MovieTask task_top=new MovieTask();
                        task_top.execute(new String[]{MOVIEAPIURL});
                        toolbar.setTitle("TOP250");
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        layoutManager =new GridLayoutManager(MainActivity.this,2);
        //查询网络
        if(isNetworkEnable==false){
            Toast.makeText(MainActivity.this,"网络连接不可用",Toast.LENGTH_SHORT).show();
            return;
        }
        //初始化界面
        MovieTask task=new MovieTask();
        task.execute(new String[]{"https://api.douban.com/v2/movie/in_theaters","0","6"});
        //点击事件
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Design By Cherrow",Toast.LENGTH_SHORT).show();
            }
        });

        //下拉刷新
        swipeRefreshLayout.setColorSchemeResources(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                canShowDlg=false;
                MovieTask task=new MovieTask();
                task.execute(new String[]{MOVIEAPIURL});
            }
        });
        //上拉加载更多
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    public void iniMovie(String url){
        try
        {
            movieListArray.clear();
            OkHttpClient client =new OkHttpClient();
            Request request=new Request.Builder().url(url).build();
            Response responsel=client.newCall(request).execute();
            response=responsel.body().string();
            HttpUtil util=new HttpUtil();
            movieListArray=util.parseJSON(response);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void iniMovie(String url,int start,int count){
        try{
            if(movieListArray.size()+count>HttpUtil.movieTotalCount&&HttpUtil.movieTotalCount!=0){
                count=HttpUtil.movieTotalCount-movieListArray.size();
            }
            OkHttpClient client=new OkHttpClient();
            FormBody body=new FormBody.Builder()
                    .add("start",Integer.toString(start))
                    .add("count",Integer.toString(count)).build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            HttpUtil util=new HttpUtil();
            if(start==0){
                movieListArray=util.parseJSON(response.body().string());
            }
            else {
                List<Movie> movieListAppend=util.parseJSON(response.body().string());
                movieListArray.addAll(movieListAppend);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class MovieTask extends AsyncTask<String ,Void ,List<Movie>>{
        ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(canShowDlg==true){
                progressDialog.setMessage("正在加载数据...");
                progressDialog.setTitle("请稍候");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movieList) {
            super.onPostExecute(movieList);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
           if(canShowDlg==true)  progressDialog.dismiss();
           else  swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            try{
                if(params.length==3){
                    iniMovie(params[0],Integer.parseInt(params[1]),Integer.parseInt(params[2]));
                }
               else{
                    iniMovie(params[0]);
                }
                adapter=new MovieAdapter(movieListArray);
                return movieListArray;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }
  }
