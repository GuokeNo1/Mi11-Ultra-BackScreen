package cn.pashiguoke.subscreen.service;

import android.app.ActivityOptions;
import android.app.Service;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import cn.pashiguoke.subscreen.DisplayActivity;
import cn.pashiguoke.subscreen.MainActivity;
import cn.pashiguoke.subscreen.R;
import cn.pashiguoke.subscreen.SubActivity;

public class SubScreenKeeper extends Service {
    private static String TAG = "SubScreenKeeper";
    int i = 0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        Display[] displays = displayManager.getDisplays();
        if(displays.length<2){
            stopService(new Intent(this,this.getClass()));
        }
        else {
            Display display = displays[1];
            ActivityOptions options = ActivityOptions.makeBasic();
            options.setLaunchDisplayId(display.getDisplayId());
            Intent intent = new Intent(this, SubActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent,options.toBundle());
        }
    }

    @Override
    public void onDestroy() {
    }
}
