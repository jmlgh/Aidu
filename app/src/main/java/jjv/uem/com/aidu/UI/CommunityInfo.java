package jjv.uem.com.aidu.UI;

import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import jjv.uem.com.aidu.Model.Community;
import jjv.uem.com.aidu.R;

public class CommunityInfo extends AppCompatActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {
    private Community community;
    private TextView tv_name, tv_description;
    private ImageView image;
    private Button btn_viewMembers;

    private GoogleMap mGoogleMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private LocationManager locManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_info);
        initVew();
    }

    private void initVew() {
        community = getIntent().getParcelableExtra(Communities.KEY_COMMUNITY);
        tv_description = (TextView)findViewById(R.id.tv_comdescription);
        tv_name = (TextView)findViewById(R.id.tv_comname);
        sbPoints = (Button)findViewById(R.id.btn_viewmembers);

    }

    private void initLocation() {
        locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
    }

    private void initMap() {
        if(mGoogleMap == null) {
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.communities_appbar)).getMapAsync(this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
