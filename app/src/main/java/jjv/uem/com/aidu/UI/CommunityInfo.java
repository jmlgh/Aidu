package jjv.uem.com.aidu.UI;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jjv.uem.com.aidu.Model.Community;
import jjv.uem.com.aidu.Model.User;
import jjv.uem.com.aidu.R;

public class CommunityInfo extends AppCompatActivity implements OnMapReadyCallback  {
    private Community community;
    private String communityKey;
    private TextView tv_name, tv_description;
    private ImageView image;
    private Button btn_viewMembers;
    private ActionBar actBar;

    private GoogleMap mGoogleMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private LocationManager locManager;
    private FirebaseDatabase database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("info", "en ventana");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_info);
        communityKey = getIntent().getStringExtra(Communities.KEY_COMMUNITY);
        database = FirebaseDatabase.getInstance();
        getComunity(communityKey);

    }

    private void initVew() {
        actBar = getSupportActionBar();

        actBar.setDisplayHomeAsUpEnabled(true);

        tv_description = (TextView)findViewById(R.id.tv_comdescription);
        tv_name = (TextView)findViewById(R.id.tv_comname);
        image = (ImageView) findViewById(R.id.imgv_comphoto);
        btn_viewMembers = (Button)findViewById(R.id.btn_viewmembers);

        tv_name.setText(community.getName());
        //Picasso.with(CommunityInfo.this).load(community.getImage()).into(image);
        Glide.with(CommunityInfo.this).load(community.getImage()).into(image);
        tv_description.setText(community.getDescription());

        try{
            initMap();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void viewMembers(View v){

        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("user");
        //Query query = reference.orderByChild("community").equalTo(community.getKey());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> iterator = dataSnapshot.getChildren();
                CharSequence members[] = new CharSequence[community.getMembers().size()];
                User u;
                int i = 0;
                for (DataSnapshot ds : iterator) {
                    u = ds.getValue(User.class);
                    if (community.getMembers().contains(u.getKey())){
                    members[i] = u.getDisplayName();
                    i++;}
                }

                AlertDialog.Builder picker = new AlertDialog.Builder(CommunityInfo.this);
                picker.setTitle(getString(R.string.communities_members));
                picker.setCancelable(true);
                picker.setItems(members, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                picker.show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }


    private void initMap() {
        if(mGoogleMap == null) {
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.commap)).getMapAsync(this);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        LatLng location = new LatLng(community.getLatitude(),community.getLongitude());
        mGoogleMap.addMarker(new MarkerOptions().position(location).title("Service location"));
        //le pasamos la actualizacion de los parametros de locaclizacion y el zoom que queramos
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));

        //habilitamos opcion para mostrar nuestra posicion
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        //permitimos que aparezcan controles del zoom para el usuario sobre el map
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
    }




    public void getComunity(final String key){

            DatabaseReference reference = database.getReference("communities/"+key);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    community = dataSnapshot.getValue(Community.class);
                    initVew();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }


}
