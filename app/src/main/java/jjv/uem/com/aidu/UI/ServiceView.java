package jjv.uem.com.aidu.UI;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

import jjv.uem.com.aidu.Adapters.Images_Adapter;
import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.R;

public class ServiceView extends AppCompatActivity {
    public static final String LATITUDE = "latitud" ;
    public static final String LONGITUDE = "longitud";
    public static final String SERVICE_KEY = "serviceKey";
    public static final String SERVICE_USER_KEY ="userkey" ;
    public static final String SERVICE_KIND ="kind" ;
    public static final String SERVICE_STATE ="estado" ;
    public static final int LONGITUD_TITLE = 15;

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private Service service;
    private TwoWayView twv_photos;
    private int[]icons={
            R.drawable.cocina,
            R.drawable.transporte,
            R.drawable.educacion,
            R.drawable.limpieza,
            R.drawable.tecnologia,
            R.drawable.compras,
            R.drawable.mascotas,
            R.drawable.plantas,
            R.drawable.otros
    };

    private Button btnLocation, btnsendMessage;
    private TextView tvServiceTitle , tvUsername , tvPoints ,tvDateTime,tvDetails;
    private ImageView iv_icon, mainPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        btnLocation= (Button) findViewById(R.id.btn_location);
        mainPhoto = (ImageView)findViewById(R.id.image_photo_service);
        btnsendMessage = (Button) findViewById(R.id.btn_talk);
        //tvTypeService = (TextView) findViewById(R.id.tv_type_service);
        tvUsername = (TextView) findViewById(R.id.tv_username);
        tvPoints = (TextView) findViewById(R.id.tv_points_service);
        tvDateTime = (TextView) findViewById(R.id.tv_datetime);
        tvDetails = (TextView) findViewById(R.id.tv_description);
        tvServiceTitle = (TextView)findViewById(R.id.tv_service_title);
        iv_icon = (ImageView) findViewById(R.id.iv_icon_service);
        twv_photos = (TwoWayView) findViewById(R.id.twv_photos_service);

        setTypeFace();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        Intent i = getIntent();
        String servicekey = i.getStringExtra(MainActivity.KEY_SERVICE);
        getService(servicekey);



        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),LocationActivity.class);
                i.putExtra(LONGITUDE,service.getLongitude());
                i.putExtra(LATITUDE,service.getLatitude());
                startActivity(i);
            }
        });


        btnsendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(service.getUserkey().equals(auth.getCurrentUser().getUid())){
                    Toast.makeText(getBaseContext(),R.string.error_service_yours,Toast.LENGTH_SHORT).show();
                }else{
                    Intent i = new Intent(getBaseContext(),ChatConversation.class);
                    i.putExtra(SERVICE_KEY,service.getServiceKey());
                    i.putExtra(SERVICE_USER_KEY,service.getUserkey());
                    i.putExtra(SERVICE_KIND,service.getKind());
                    i.putExtra(SERVICE_STATE,service.getState());
                    startActivity(i);
                }

            }
        });

    }

    private void getService(String servicekey) {
        if(auth.getCurrentUser() != null){
            DatabaseReference reference = database.getReference("services/"+servicekey);
            reference.addValueEventListener(new ValueEventListener() {

                ArrayList<String> photos;
                Images_Adapter adapter;
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    service = dataSnapshot.getValue(Service.class);
                    if(service != null){
                        Log.w("SERVICE SELECT:",service.toString());
                        //tvTypeService.setText(service.getTitle());
                        tvUsername.setText(getString(R.string.user_name, service.getUserName()));
                        tvPoints.setText(getString(R.string.service_points, service.getPrice_points()));
                        tvDateTime.setText(service.getDate() + " - "+service.getHour());
                        String title = service.getTitle();
                        if(title.length()>LONGITUD_TITLE){
                            title = service.getTitle().substring(0,LONGITUD_TITLE)+"...";
                        }
                        tvServiceTitle.setText(title);
                        tvDetails.setText(service.getDescription());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            iv_icon.setImageResource(icons[service.getIcon()]);
                        }
                        photos= service.getPhotos();
                        adapter = new Images_Adapter(getBaseContext(), photos,false);
                        twv_photos.setAdapter(adapter);
                        Glide.with(getBaseContext()).load(service.getPhotos().get(0)).into(mainPhoto);
                    }
                    else{
                        Intent i = new Intent(getBaseContext(), MainActivity.class);
                        Toast.makeText(ServiceView.this, "Service finished. Your points have been transferred", Toast.LENGTH_SHORT).show();
                        startActivity(i);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    private void setTypeFace() {
        Typeface bubblerFont = Typeface.createFromAsset(getAssets(), "fonts/BubblerOne-Regular.ttf");
        btnsendMessage.setTypeface(bubblerFont);
        btnLocation.setTypeface(bubblerFont);
    }
}
