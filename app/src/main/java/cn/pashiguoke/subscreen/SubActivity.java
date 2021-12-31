package cn.pashiguoke.subscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.WindowManager;

import cn.pashiguoke.subscreen.viewpaper.ViewAdapter;

public class SubActivity extends SubBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        ViewPager2 vp = findViewById(R.id.VP);
        vp.setAdapter(new ViewAdapter(this));

    }
}