package cn.pashiguoke.subscreen;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.pashiguoke.subscreen.service.SubScreenKeeper;

public class MainActivity extends AppCompatActivity {
    View personal_bg;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        personal_bg = findViewById(R.id.mainBack);

        initBackground();
        RequestPermissions();

        // 显示器管理的
        DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        Display[] displays = displayManager.getDisplays();
        ((TextView)findViewById(R.id.infText)).setText("不出意外副屏已经开起来了");
        if(displays.length<2){
            ((TextView)findViewById(R.id.infText)).setText("未检测到副屏");
        }

        // 启动服务
        startService(new Intent(this,SubScreenKeeper.class));

    }
    // 申请权限
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void RequestPermissions(){
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET
        };
        for (String permission:permissions) {
            while (checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED){
                Log.d("TAG", "RequestPermissions: "+permission);
                requestPermissions(new String[]{permission},0);
            }

        }
    }
    // 初始化设置背景
    private void initBackground(){

        // 换背景的
        if(new File(getCacheDir()+"/background").exists()){
            personal_bg.setBackground(Drawable.createFromPath(getCacheDir()+"/background"));
        }else{
            personal_bg.setBackgroundResource(R.drawable.background);
        }
        new DownloadImageTask().execute("https://picsum.photos/720/1280");
        if(!new File(getCacheDir()+"/time.html").exists()){
            try {
                FileWriter fw = new FileWriter(getCacheDir()+"/time.html");
                fw.write("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>时间</title>\n" +
                        "    <style>\n" +
                        "        *{\n" +
                        "            margin: 0px;\n" +
                        "            padding: 0px;\n" +
                        "            --color-number: #000;\n" +
                        "        }\n" +
                        "        body{\n" +
                        "            width: 100vw;\n" +
                        "            height: 100vh;\n" +
                        "            overflow: hidden;\n" +
                        "            background: url(\"https://picsum.photos/126/294\");\n" +
                        "        }\n" +
                        "        .show{\n" +
                        "            width: 100vw;\n" +
                        "            height: 100vh;\n" +
                        "            display: flex;\n" +
                        "            flex-direction: column;\n" +
                        "            justify-content: center;\n" +
                        "            align-items: center;\n" +
                        "        }\n" +
                        "        .time{\n" +
                        "            font-size: 1em;\n" +
                        "            font-weight: 700;\n" +
                        "            text-shadow: 0px 0px 10vw #eee;\n" +
                        "            background-color: #0ee8;\n" +
                        "        }\n" +
                        "        .date{\n" +
                        "            font-size: .5em;\n" +
                        "            text-shadow: 0px 0px 10vw #eee;\n" +
                        "            background-color: #e006;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "    <div class=\"show\">\n" +
                        "        <div class=\"time\">11:11</div>\n" +
                        "        <div class=\"date\">11:11</div>\n" +
                        "    </div>\n" +
                        "    <script>\n" +
                        "        setInterval(function(){\n" +
                        "            document.querySelector(\".time\").innerText = new Date().toLocaleTimeString();\n" +
                        "            document.querySelector(\".date\").innerText = new Date().toLocaleDateString();\n" +
                        "        },1000);\n" +
                        "    </script>\n" +
                        "</body>\n" +
                        "</html>");
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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