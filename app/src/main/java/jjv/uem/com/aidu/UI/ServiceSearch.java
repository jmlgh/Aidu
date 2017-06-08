package jjv.uem.com.aidu.UI;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Random;

import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.R;
import jjv.uem.com.aidu.util.ServiceFilter;

public class ServiceSearch extends AppCompatActivity
                           implements OnMapReadyCallback, LocationListener,
                            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
                            GoogleMap.OnMarkerClickListener{

    private TextView tvDistance, tvPoints;
    private Spinner spCategories;
    private ArrayAdapter<String> categoryAdapter;
    private SeekBar sbPoints, sbDistance;
    private int distance = 1, points = 1;
    private GoogleMap mGoogleMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ArrayList<Service>serviceList;
    private ServiceFilter serviceFilter;
    private ArrayList<MarkerOptions> markerOptionsArrayList;
    private int currentSelectedCategoryIndex = 0;
    private Marker currentServiceMarker;
    //private ArrayList<Service>markerServiceList;

    private final String[] categoryList = {"All", "Cook", "Transport", "Education", "Housekeeping", "Technology",
                                  "Shopping", "Pets", "Plants", "Others"};

    private LocationManager locManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_search);

        // configura el titulo de la pantalla para que aparezca centrado
        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.abs_layout_service_search_title);

        //markerServiceList = new ArrayList<>();
        serviceFilter = new ServiceFilter();
        auth = FirebaseAuth.getInstance();
        getServices();
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

        // spinner
        spCategories = (Spinner)findViewById(R.id.sp_search_category);
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategories.setAdapter(categoryAdapter);

        // asign listeners
        sbPoints.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                points = progress;
                if(points == 0){
                    points = 1;
                }
                tvPoints.setText(getString(R.string.label_search_points, points));
                serviceFilter.setPoints(points);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // configura el service para mostrarlo en el mapa
                placeServiceMarkers(serviceFilter);
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
                serviceFilter.setDistance(distance);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                placeServiceMarkers(serviceFilter);
            }
        });

        spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != currentSelectedCategoryIndex){
                    serviceFilter.setCategory(categoryList[position]);
                    placeServiceMarkers(serviceFilter);
                    currentSelectedCategoryIndex = position;
                } else {
                    serviceFilter.setCategory(categoryList[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        }

    }

    private void placeServiceMarkers(ServiceFilter filter) {
        int distance = 0;
        LatLng pos = null;
        MarkerOptions serviceMarker = null;
        mGoogleMap.clear();

        markerOptionsArrayList = new ArrayList<>();
        // calculate distance from user
        for(Service service : serviceList){
            pos = new LatLng(service.getLatitude(), service.getLongitude());
            distance = (int)SphericalUtil.computeDistanceBetween(pos, mCurrLocationMarker.getPosition());
            // transform distance to km
            distance = distance / 1000;
            service.setDistanceFromUser(distance);
            Log.i("Marker:", distance+"");

            if(filter.getCategory().equals("All")){
                if(Integer.parseInt(service.getPrice_points()) >= filter.getPoints() && service.getDistanceFromUser() <= filter.getDistance()) {
                    serviceMarker = new MarkerOptions();
                    serviceMarker.position(pos);
                    serviceMarker.title(service.getTitle());
                    serviceMarker.icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor()));

                    markerOptionsArrayList.add(serviceMarker);
                }
            }else{
                if(filter.getCategory().equals(service.getCategory())){
                    if(Integer.parseInt(service.getPrice_points()) >= filter.getPoints() && service.getDistanceFromUser() <= filter.getDistance()) {
                        serviceMarker = new MarkerOptions();
                        serviceMarker.position(pos);
                        serviceMarker.title(service.getTitle());
                        serviceMarker.icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor()));

                        markerOptionsArrayList.add(serviceMarker);
                    }
                }
            }
        }

        // since we are clearing the map at the beginning of the method, show user current location again
        Location myLoc = mGoogleMap.getMyLocation();
        LatLng current = new LatLng(myLoc.getLatitude(),myLoc.getLongitude());

        mGoogleMap.addMarker(new MarkerOptions().position(current).title("YOU"));
        for(MarkerOptions markerOption : markerOptionsArrayList){
            currentServiceMarker = mGoogleMap.addMarker(markerOption);
            Log.i("MO:", markerOption.getTitle());
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause(){
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,8));


    }

    private float getMarkerColor(){
        // lista de colores para marcadores en el mapa
        float[] markerColors = {BitmapDescriptorFactory.HUE_GREEN, BitmapDescriptorFactory.HUE_MAGENTA, BitmapDescriptorFactory.HUE_ROSE,
                BitmapDescriptorFactory.HUE_VIOLET, BitmapDescriptorFactory.HUE_AZURE, BitmapDescriptorFactory.HUE_YELLOW,
                BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_CYAN, BitmapDescriptorFactory.HUE_ORANGE};
        Random colorRandom = new Random();

        int randColor =  colorRandom.nextInt(markerColors.length);
        float chosenColor = markerColors[randColor];
        return chosenColor;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        // get current location every 10 seconds
        //mLocationRequest.setInterval(10000);
        //mLocationRequest.setFastestInterval(300*1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ServiceSearch.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void getServices() {
        // Acceso a BBDD Firebase
        if(auth.getCurrentUser() != null){
            database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("services");
            reference.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> iterator = dataSnapshot.getChildren();
                    serviceList = new ArrayList<>();
                    for (DataSnapshot ds : iterator){
                        Service s = ds.getValue(Service.class);
                        Log.i("service search:",s.toString());
                        serviceList.add(s);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.equals(currentServiceMarker)){

        }
        return false;
    }
}
