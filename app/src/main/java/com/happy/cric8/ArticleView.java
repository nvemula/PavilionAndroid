package com.happy.cric8;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ArticleView extends Activity {
    String shareURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_view);

        // Hide the Status Bar
        getActionBar().hide();

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein_right);

        RelativeLayout shareBtn = (RelativeLayout) findViewById(R.id.shareBtn);

        shareBtn.startAnimation(fadeIn);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle extras = getIntent().getExtras();

        if(extras!=null){
           shareURL = extras.getString("shareURL");
        }


        WebView webpage = (WebView)findViewById(R.id.web_page);
        webpage.getSettings().setJavaScriptEnabled(true);
        webpage.loadUrl(String.valueOf(Uri.parse(shareURL)));
        final String javascript = "javascript:(function(){readConvertLinksToFootnotes = false;readStyle = 'style-newspaper';readSize = 'size-medium';readMargin = 'margin-wide';_readability_script = document.createElement('script');_readability_script.type = 'text/javascript';_readability_script.src='https://s3.amazonaws.com/readability-cric8/readability.js?x=' + (Math.random());document.documentElement.appendChild(_readability_script);_readability_css =document.createElement('link');_readability_css.rel = 'stylesheet';_readability_css.href = 'https://s3.amazonaws.com/readability-cric8/readability.css';_readability_css.type = 'text/css';_readability_css.media = 'all';document.documentElement.appendChild(_readability_css);})(); ";

        webpage.setWebChromeClient(new WebChromeClient());
        webpage.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {

                       view.loadUrl(javascript);
                ProgressBar loader = (ProgressBar) findViewById(R.id.load_article);
                loader.setVisibility(View.INVISIBLE);

                view.setVisibility(View.VISIBLE);
            }

        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article_view, menu);
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

    public void shareLink(View v){


            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");

            //intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_TEXT, shareURL);

            startActivity(Intent.createChooser(intent,"Share"));
   }
}
