package com.psgk.doubescreen;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Display display;
    View personal_bg;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //改透明状态栏的
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        // 换背景的
        personal_bg = findViewById(R.id.mainBack);
        if(new File(getCacheDir()+"/background").exists()){
            personal_bg.setBackground(Drawable.createFromPath(getCacheDir()+"/background"));
        }else{
            personal_bg.setBackgroundResource(R.drawable.background);
        }
        new DownloadImageTask().execute("https://picsum.photos/720/1280");

        // 显示器管理的
        DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        Display[] displays = displayManager.getDisplays();
        ((TextView)findViewById(R.id.infText)).setText("随意点击即可开启副屏内容");
        if(displays.length<2){
            ((TextView)findViewById(R.id.infText)).setText("未检测到副屏");
        }
        else {
            display = displays[1];
            personal_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityOptions options = ActivityOptions.makeBasic();
                    options.setLaunchDisplayId(display.getDisplayId());
                    Intent intent = new Intent(MainActivity.this,DisplayActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.this.startActivity(intent,options.toBundle());
                }
            });
        }

    }

    private Drawable loadImageFromNetwork(String imageUrl) {
        Drawable drawable = null;
        try {
            drawable = Drawable.createFromStream(new URL(imageUrl).openStream(), null);

        } catch (Exception e) {
            Log.d("MainActivity", e.getMessage());
        }
        if (drawable == null) {
            Log.d("MainActivity", "null drawable");
        } else {
            Log.d("MainActivity", "not null drawable");
        }

        return drawable;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Drawable> {

        protected Drawable doInBackground(String... urls) {
            return loadImageFromNetwork(urls[0]);
        }

        @SuppressLint("WrongThread")
        protected void onPostExecute(Drawable result) {
            try {
                // 缓存到本地下次打开使用
                File pic = new File(getCacheDir() + "/background");
                if (pic.exists()) {
                    pic.delete();
                }else{
                    //personal_bg.setBackground(result);
                }

                FileOutputStream fo = new FileOutputStream(pic);
                ((BitmapDrawable) result).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fo);
                fo.close();

            }catch (Exception e){}

        }
    }
}