package jjv.uem.com.aidu.Adapters;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import jjv.uem.com.aidu.UI.Login;
import jjv.uem.com.aidu.UI.MainActivity;
import jjv.uem.com.aidu.UI.Tutorial;
import jjv.uem.com.aidu.util.TutorialFragment;

/**
 * Created by javi_ on 11/06/2017.
 */

public class TutorialAdapter extends FragmentPagerAdapter {

    public TutorialAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:

                return TutorialFragment.newInstance(Color.parseColor("#4E342E"), position); // Brown
            case 1:
                return TutorialFragment.newInstance(Color.parseColor("#03A9F4"), position); // blue
            case 2:

                return TutorialFragment.newInstance(Color.parseColor("#37474F"), position); // grey
            case 3:
                return TutorialFragment.newInstance(Color.parseColor("#FF5252"), position); // accent (red)
            default:
                return TutorialFragment.newInstance(Color.parseColor("#FF5252"), position); // accent (red)
        }
    }

    @Override
    public int getCount() {
        return 6;
    }
}
