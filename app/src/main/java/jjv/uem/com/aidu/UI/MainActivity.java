package jjv.uem.com.aidu.UI;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;


import java.util.ArrayList;

import jjv.uem.com.aidu.Adapters.Service_Adapter_RV;
import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.R;


public class MainActivity extends AppCompatActivity {

    private ArrayList<Service>services = new ArrayList<>();
    private RecyclerView recyclerView;
    private Service_Adapter_RV.OnItemClickListener l;
    private Service_Adapter_RV adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //App bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Services");

        recyclerView = (RecyclerView) findViewById(R.id.lstLista);
        Service s;
        for(int i = 0 ; i<30;i++){

            s= new Service();
            s.setTitle("Service " +i);
            services.add(s);
        }
        l = initListener();
        adapter = new Service_Adapter_RV(services,l);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        recyclerView.setAdapter(adapter);



    }

    private Service_Adapter_RV.OnItemClickListener initListener(){
        Service_Adapter_RV.OnItemClickListener listener = new Service_Adapter_RV.OnItemClickListener() {
            @Override
            public void onItemClick(Service item) {
                //TODO LISTENER DEL RECYCLER VIEW
            }
        };
        return listener;


    }


    }
