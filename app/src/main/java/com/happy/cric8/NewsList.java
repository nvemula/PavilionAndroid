package com.happy.cric8;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.happy.cric8.adapters.NewsListAdapter;
import com.happy.cric8.data.NewsItem;
import com.happy.cric8.xalexchen.*;
import com.happy.cric8.xalexchen.FastBlur.FastBlur;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class NewsList extends Activity implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipeRefreshLayout;
    AspectRatioImageView live_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_list);

        live_image =  (AspectRatioImageView) findViewById(R.id.live_image);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        FetchNews fetchNews = new FetchNews();
        fetchNews.execute(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_list, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {

        FetchNews fetchNews = new FetchNews();
        swipeRefreshLayout.setRefreshing(false);
        fetchNews.execute(this);
    }


    public class FetchNews extends AsyncTask<Context,String,List<NewsItem>>{
        Context c;
        Bitmap img;

        @Override
        protected List<NewsItem> doInBackground(Context... params) {
             //Get context
            c = params[0];
            //Fetch latest News json
            //Parse json
            //Generate list of Newsitem objects

            String news_url = "https://www.dropbox.com/s/a28ghxey4slp88k/news.json?dl=1";
            List<NewsItem> newsList = new ArrayList<NewsItem>();


            InputStream inputStream = null;

            ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo netInfo = cManager.getActiveNetworkInfo();

            if(netInfo !=null && netInfo.isConnected()){
                //Phone is connected to the internet
                try{
                    URL url = new URL(news_url);
                    //Connect to the Server
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setConnectTimeout(20000);
                    conn.setReadTimeout(10000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();
                    int response = conn.getResponseCode();
                    Log.v("ConnectionResponse ",String.valueOf(response));

                    inputStream = conn.getInputStream();

                    //Get data
                    //Check if data is valid and not empty

                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    StringBuilder responseStrBuilder = new StringBuilder();

                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null)
                        responseStrBuilder.append(inputStr);

                    String jsonResponse = responseStrBuilder.toString();
                    Log.v("jsonResponse", String.valueOf(jsonResponse));

                    JSONObject jsonObject = new JSONObject(jsonResponse);


                    //Parse data and create a list of objects

                    JSONArray newsArray = jsonObject.getJSONArray("news");
                    String imgURL = jsonObject.getString("imgurl");
                    img = downloadImage(imgURL);




                    Log.v("jsonlength",String.valueOf(newsArray.length()));


                    newsList = JSONtonewsList(newsArray);






                }catch(Exception ie){

                    Log.v("jsonexception",ie.getMessage());

                }

            }

            else {
                //There is no internet connection so don't connect

            }

            return newsList;
        }

        @Override
        protected void onPostExecute(List<NewsItem> newsItems) {
               updateView(newsItems,img);


            //Create Adapter to update listview
            //Update listview

        }
    }

    public List<NewsItem> JSONtonewsList(JSONArray newsArray) throws IOException,JSONException {
        List<NewsItem> newsList = new ArrayList<NewsItem>();

        for(int i=0;i<newsArray.length();i++){

            NewsItem articleData = new NewsItem();

            JSONObject news_object = newsArray.getJSONObject(i);

            articleData.heading = news_object.getString("title");
            articleData.articleSource = news_object.getString("link");
            articleData.shortIntro = news_object.getString("description");
            articleData.shortLink = news_object.getString("source");


            newsList.add(articleData);

        }
        return newsList;
    }

    private void blur(Bitmap bkg) {
        float radius = 50;



        Bitmap overlay = Bitmap.createBitmap((int) (bkg.getWidth()),
                (int) (bkg.getHeight()), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-live_image.getLeft(), -live_image.getTop());
        canvas.drawBitmap(bkg, 0, 0, null);
        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        live_image.setBackground(new BitmapDrawable(getResources(), overlay));

    }



    public void updateView(final List<NewsItem> newsItems,final Bitmap img){

        ListView newsList = (ListView)findViewById(R.id.news_list);
        NewsListAdapter newsListAdapter = new NewsListAdapter(this,newsItems);
        newsList.setAdapter(newsListAdapter);
        final Context context = this;
        if (img !=null){

            blur(img);

        }

       newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent = new Intent(context,ArticleView.class);
               //intent.putExtra("articleSource",newsItems.get(position));
               intent.putExtra("shareURL",newsItems.get(position).articleSource);

               startActivity(intent);
           }
       });

    }

    public static Bitmap downloadImage (String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            return myBitmap;

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("getBmpFromUrl error: ", e.getMessage().toString());
            return null;
        }
    }

}
