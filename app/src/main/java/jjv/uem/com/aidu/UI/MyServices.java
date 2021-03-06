package jjv.uem.com.aidu.UI;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.R;
import jjv.uem.com.aidu.util.CardAdapter;

public class MyServices extends AppCompatActivity {
    private FirebaseAuth auth ;
    private RecyclerView recyclerView;
    public static final String KEY_SERVICE = "KEY_SERVICE";
    private FirebaseDatabase database;
    private TextView tv_message_my_services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_services);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        tv_message_my_services = (TextView) findViewById(R.id.tv_message_my_services);
        tv_message_my_services.setVisibility(View.INVISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.MyServicesList);

        getServices();

    }

    private void getServices() {
        // Acceso a BBDD Firebase
        if(auth.getCurrentUser() != null){
            database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("user-services/"
                    +auth.getCurrentUser().getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> iterator = dataSnapshot.getChildren();
                    ArrayList<Service> serviceList = new ArrayList<>();
                    for (DataSnapshot ds : iterator){
                        Service s = ds.getValue(Service.class);
                        serviceList.add(s);
                    }
                    if(serviceList.size()<=0){
                        tv_message_my_services.setVisibility(View.VISIBLE);
                    }else{
                        tv_message_my_services.setVisibility(View.INVISIBLE);
                    }

                    CardAdapter.OnItemClickListener l = initListener();
                    CardAdapter cardAdapter = new CardAdapter(MyServices.this, serviceList, l);
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MyServices.this, 2);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    //recyclerView.addItemDecoration(new MainActivity.GridSpacingItemDecoration(2, dpToPx(10), true));
                    recyclerView.setAdapter(cardAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private CardAdapter.OnItemClickListener initListener(){
        CardAdapter.OnItemClickListener listener = new CardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Service item) {
                Intent i = new Intent(getBaseContext(),ServiceView.class);
                i.putExtra(KEY_SERVICE,item.getServiceKey());
                startActivity(i);
            }
        };

        return listener;
    }


}
