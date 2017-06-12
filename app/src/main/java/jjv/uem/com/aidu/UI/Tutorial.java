package jjv.uem.com.aidu.UI;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jjv.uem.com.aidu.Adapters.TutorialAdapter;
import jjv.uem.com.aidu.R;
import jjv.uem.com.aidu.util.TutorialPageTransformer;

public class Tutorial extends AppCompatActivity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);


        mViewPager = (ViewPager)findViewById(R.id.tutorial_viewpager);

        // set adapter
        mViewPager.setAdapter(new TutorialAdapter(getSupportFragmentManager()));

        // set page transformer
        mViewPager.setPageTransformer(false, new TutorialPageTransformer());

    }
}
