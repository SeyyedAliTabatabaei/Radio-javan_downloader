package ir.rjdownloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.pushpole.sdk.PushPole;

import java.util.Map;

import ir.rjdownloader.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        PushPole.initialize(this,true);


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                binding.etLink.setText(sharedText);
            }
        }

        binding.download320.setOnClickListener(v -> {

            if (binding.etLink.getText().toString().isEmpty())
                binding.lTxtLink.setError(getString(R.string.txt_null));
            else {
                if (binding.rMusic.isChecked())
                    getInfo(binding.etLink.getText().toString() , "mp3-320" , "mp3");
                else if (binding.rPodcast.isChecked())
                    getInfo(binding.etLink.getText().toString() , "mp3-320" , "podcast");
                else if (binding.rVideo.isChecked())
                    getInfo(binding.etLink.getText().toString() , "" , "music_video");
                else
                    Toast.makeText(this, getString(R.string.selectType), Toast.LENGTH_SHORT).show();
            }
        });

        binding.download256.setOnClickListener(v -> {

            if (binding.etLink.getText().toString().isEmpty())
                binding.lTxtLink.setError(getString(R.string.txt_null));
            else {
                if (binding.rMusic.isChecked())
                    getInfo(binding.etLink.getText().toString() , "mp3-256" , "mp3");
                else if (binding.rPodcast.isChecked())
                    getInfo(binding.etLink.getText().toString() , "mp3-256" , "podcast");
                else if (binding.rVideo.isChecked())
                    getInfo(binding.etLink.getText().toString() , "" , "music_video");
                else
                    Toast.makeText(this, getString(R.string.selectType), Toast.LENGTH_SHORT).show();
            }
        });


        binding.toolBar.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()){
                case R.id.menu_info:
                    Dialog dialog = new Dialog(this);
                    dialog.setContentView(R.layout.dialog_info);
                    dialog.show();
                    break;
            }
            return false;
        });

    }

    public void getInfo(String link , String quality , String type){

        Toast.makeText(this , getString(R.string.get_info) , Toast.LENGTH_SHORT).show();
        DownloadManager downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

        binding.webview.loadUrl(link);
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.setWebViewClient(new HelloWebViewClient(name -> {
            Uri uri = null;
            if (type.equals("mp3") || type.equals("podcast")){

                uri = Uri.parse("https://host2.rj-mw1.com/media/" + type + "/" + quality + "/" + name +  ".mp3");
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS , name +".mp3");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setTitle(name);

                long dmr = downloadManager.enqueue(request);
                Toast.makeText(this, getString(R.string.start_download), Toast.LENGTH_LONG).show();
            }
            else if (type.equals("music_video")){

                uri = Uri.parse("https://host2.rj-mw1.com/media/" + type + "/hq/" + name +  ".mp4");

                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS , name +".mp4");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setTitle(name);

                long dmr = downloadManager.enqueue(request);
                Toast.makeText(this, getString(R.string.start_download), Toast.LENGTH_LONG).show();
            }
        }));
    }

    private static class HelloWebViewClient extends WebViewClient {

        private EventStrtrName eventStrtrName;

        public HelloWebViewClient(EventStrtrName eventStrtrName) {
            this.eventStrtrName = eventStrtrName;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            eventStrtrName.strname(name(view.getUrl()));
            return true;
        }

        public String name(String name){
            String[] uri = name.split("/");
            String a = uri[5];
            return a.substring(0 , a.lastIndexOf("?"));
        }

        public interface EventStrtrName{
            void strname(String name);
        }
    }

}