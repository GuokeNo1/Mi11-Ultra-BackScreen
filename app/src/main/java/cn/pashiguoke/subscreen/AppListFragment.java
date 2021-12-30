package cn.pashiguoke.subscreen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AppListFragment extends Fragment {
    private Class<?> _Activity;
    private Drawable image;
    private String name;
    public AppListFragment(Drawable image,String name,Class<?> activity) {
        this.name = name;
        this.image = image;
        this._Activity = activity;
    }
    public static Fragment newInstance(Drawable image,String name, Class<?> activity)
    {
        return new AppListFragment(image,name,activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_app_list, container, false);
        ((ImageView)v.findViewById(R.id.imageView)).setImageDrawable(image);
        ((TextView)v.findViewById(R.id.titleView)).setText(name);
        v.findViewById(R.id.appClick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AppListFragment.this.getContext(), AppListFragment.this._Activity));
            }
        });
        return v;
    }
}