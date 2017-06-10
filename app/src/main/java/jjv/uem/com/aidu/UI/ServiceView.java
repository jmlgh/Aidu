package jjv.uem.com.aidu.UI;

import android.content.Intent;
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
    public static final String SERVICE_USERNAME ="username" ;
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

    Button btnLocation, btnsendMessage;
    TextView tvTypeService , tvUsername , tvPoints ,tvDateTime,tvDetails;
    ImageView iv_icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        btnLocation= (Button) findViewById(R.id.btn_location);
        btnsendMessage = (Button) findViewById(R.id.btn_talk);
        tvTypeService = (TextView) findViewById(R.id.tv_type_service);
        tvUsername = (TextView) findViewById(R.id.tv_username);
        tvPoints = (TextView) findViewById(R.id.tv_points_service);
        tvDateTime = (TextView) findViewById(R.id.tv_datetime);
        tvDetails = (TextView) findViewById(R.id.tv_description);
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
                    i.putExtra(SERVICE_USERNAME,service.getUserName());
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
                    Log.w("SERVICE SELECT:",service.toString());
                    tvTypeService.setText(service.getKind());
                    tvUsername.setText(service.getUserName());
                    tvPoints.setText(service.getPrice_points());
                    tvDateTime.setText(service.getHour() + " "+service.getDate());
                    tvDetails.setText(service.getTitle()+": "+service.getDescription());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        iv_icon.setImageResource(icons[service.getIcon()]);
                    }
                    photos= service.getPhotos();
                    adapter = new Images_Adapter(getBaseContext(), photos,false);
                    twv_photos.setAdapter(adapter);
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
