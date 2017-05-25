package jjv.uem.com.aidu.UI;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.R;

public class ServiceView extends AppCompatActivity {
    public static final String LATITUDE = "latitud" ;
    public static final String LONGITUDE = "longitud";
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private Service service;

    Button btnLocation, btnsendMessage;
    TextView tvTypeService , tvUsername , tvPoints ,tvDateTime,tvDetails;


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





    }

    private void getService(String servicekey) {
        if(auth.getCurrentUser() != null){
            DatabaseReference reference = database.getReference("services/"+servicekey);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    service = dataSnapshot.getValue(Service.class);
                    Log.i("SERVICE SELECT:",service.toString());
                    tvTypeService.setText(service.getKind());
                    tvUsername.setText(service.getUserName());
                    tvPoints.setText(service.getPrice_points());
                    tvDateTime.setText(service.getHour() + " "+service.getDate());
                    tvDetails.setText(service.getTitle()+": "+service.getDescription());
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
