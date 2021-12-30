package cn.pashiguoke.subscreen.receiver;

import static android.content.Context.DISPLAY_SERVICE;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.util.Log;
import android.view.Display;

import androidx.annotation.RequiresApi;

import cn.pashiguoke.subscreen.DisplayActivity;
import cn.pashiguoke.subscreen.service.SubScreenKeeper;

public class MYBroadcastReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case Intent.ACTION_BOOT_COMPLETED:
                context.startService(new Intent(context, SubScreenKeeper.class));
                break;
            case Intent.ACTION_SCREEN_ON:
//                Log.d("SubScreenKeeper", "onReceive" + "ON");
//                DisplayManager displayManager = (DisplayManager) context.getSystemService(DISPLAY_SERVICE);
//                Display[] displays = displayManager.getDisplays();
//                if(displays.length<2)
//                    break;
//                Display display = displays[1];
//                ActivityOptions options = ActivityOptions.makeBasic();
//                options.setLaunchDisplayId(display.getDisplayId());
//                Intent _intent = new Intent(context, DisplayActivity.class);
//                _intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                context.startActivity(_intent,options.toBundle());
                break;
        }
    }
}
