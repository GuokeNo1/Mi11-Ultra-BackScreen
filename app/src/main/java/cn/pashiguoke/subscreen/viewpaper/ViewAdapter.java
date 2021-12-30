package cn.pashiguoke.subscreen.viewpaper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import cn.pashiguoke.subscreen.AppListFragment;
import cn.pashiguoke.subscreen.DisplayActivity;
import cn.pashiguoke.subscreen.MainFragment;
import cn.pashiguoke.subscreen.MirrorActivity;
import cn.pashiguoke.subscreen.R;


public class ViewAdapter extends FragmentStateAdapter {

    private Fragment[] fragments;
    public ViewAdapter(FragmentActivity context) {
        super(context);
        fragments = new Fragment[]{
                MainFragment.newInstance(),
                AppListFragment.newInstance(context.getDrawable(R.drawable.mirror),"镜子",MirrorActivity.class),
                AppListFragment.newInstance(context.getDrawable(R.drawable.applist),"应用列表",DisplayActivity.class),
        };
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return fragments.length;
    }
}
