package com.psgk.doubescreen;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DisplayActivity extends AppCompatActivity {
    Display[] displays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);


        ListView appList = findViewById(R.id.appListView);

        DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        displays = displayManager.getDisplays();

        PackageManager pm = getPackageManager();
        @SuppressLint("QueryPermissionsNeeded") List<ApplicationInfo> infos = pm.getInstalledApplications(PackageManager.FLAG_PERMISSION_WHITELIST_INSTALLER);
        AppAdapter appAdapter = new AppAdapter(this,displays[1]);
        for(int i=0;i<infos.size();i++){
            if((infos.get(i).flags&ApplicationInfo.FLAG_SYSTEM)==0)
                appAdapter.AppendText(infos.get(i));

        }
        appAdapter.notifyDataSetChanged();

        appList.setAdapter(appAdapter);
    }
}

class AppAdapter extends BaseAdapter {
    private Context context;
    private List<ApplicationInfo> contents;
    private Display display;
    public  AppAdapter(Context context,Display display){
        this.context=context;
        this.contents = new ArrayList<>();
        this.display = display;
    }
    public void AppendText(ApplicationInfo text){
        this.contents.add(text);
    }
    @Override
    public int getCount() {
        return this.contents.size();
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return contents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position>=contents.size())
            return null;
        View view = View.inflate(context,R.layout.layout_applist,null);
        TextView nameView = view.findViewById(R.id.name);
        TextView packageView = view.findViewById(R.id.packagename);
        ImageView iconView = view.findViewById(R.id.iconview);
        nameView.setText(getItem(position).loadLabel(context.getPackageManager()));
        packageView.setText(getItem(position).packageName);
        iconView.setImageDrawable(context.getPackageManager().getApplicationIcon(getItem(position)));

        view.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                ActivityOptions options = ActivityOptions.makeBasic();
                options.setLaunchDisplayId(display.getDisplayId());
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(getItem(position).packageName);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent,options.toBundle());
            }
        });

        return view;
    }
}
