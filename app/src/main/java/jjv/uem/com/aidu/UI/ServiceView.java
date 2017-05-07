package jjv.uem.com.aidu.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import jjv.uem.com.aidu.R;

public class ServiceView extends AppCompatActivity {
    Button btnLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        btnLocation= (Button) findViewById(R.id.btn_location);

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),LocationActivity.class);
                startActivity(i);
            }
        });


    }
}
