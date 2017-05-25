package jjv.uem.com.aidu.UI;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import jjv.uem.com.aidu.R;

public class ServiceSearch extends AppCompatActivity {

    private TextView tvDistance, tvPoints;
    private SeekBar sbPoints, sbDistance;
    private int distance = 1, points = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_search);

        // configura el titulo de la pantalla para que aparezca centrado
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout_service_search_title);

        initViews();

    }

    private void initViews() {
        tvDistance = (TextView)findViewById(R.id.tv_search_distance);
        tvPoints = (TextView)findViewById(R.id.tv_search_points);
        sbPoints = (SeekBar)findViewById(R.id.sb_search_points);
        sbDistance = (SeekBar)findViewById(R.id.sb_search_distance);

        tvPoints.setText(getString(R.string.label_search_points, points));
        tvDistance.setText(getString(R.string.label_search_distance, distance));

        // asign listeners
        sbPoints.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                points = progress;
                if(points == 0){
                    points = 1;
                }
                tvPoints.setText(getString(R.string.label_search_points, points));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance = progress;
                if(distance == 0){
                    distance = 1;
                }
                tvDistance.setText(getString(R.string.label_search_distance, distance));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
