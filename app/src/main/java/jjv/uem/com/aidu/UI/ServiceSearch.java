package jjv.uem.com.aidu.UI;

import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import jjv.uem.com.aidu.R;

public class ServiceSearch extends AppCompatActivity implements OnMapReadyCallback {

    private TextView tvDistance, tvPoints;
    private SeekBar sbPoints, sbDistance;
    private int distance = 1, points = 1;
    private GoogleMap mGoogleMap;
    private LocationManager locManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_search);

        // configura el titulo de la pantalla para que aparezca centrado
        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.abs_layout_service_search_title);

        initViews();
        try{
            initLocation();
            initMap();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void initLocation() {
        locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
    }

    private void initMap() {
        if(mGoogleMap == null) {
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        try{
            float zoomLevel = 16;
            mGoogleMap.setMyLocationEnabled(true);
            //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom());
        }catch(SecurityException e){

        }

    }
}
