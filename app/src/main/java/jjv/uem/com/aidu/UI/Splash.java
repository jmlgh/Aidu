package jjv.uem.com.aidu.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import jjv.uem.com.aidu.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Splash extends AppCompatActivity {

    private ImageView logo;
    private TextView title;
    private Animation animRotate,animFadeIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = (ImageView) findViewById(R.id.logo);
        title = (TextView) findViewById(R.id.title_splash);
        title.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.INVISIBLE);
        openApp();

        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        animRotate = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.rotate);


        logo.setAnimation(animRotate);
        logo.getAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                logo.setVisibility(View.VISIBLE);
                title.setAnimation(animFadeIn);
                title.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animation animation) {



            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });






    }
    private void openApp() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2800);

    }


}
