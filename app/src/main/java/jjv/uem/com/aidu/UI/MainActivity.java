package jjv.uem.com.aidu.UI;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import jjv.uem.com.aidu.Adapters.Service_Adapter_RV;
import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.R;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private ArrayList<Service>services = new ArrayList<>();
    private RecyclerView recyclerView;
    private Service_Adapter_RV.OnItemClickListener l;
    private Service_Adapter_RV adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // control de usuario, si no hay usuario activo abre el login
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // si existe el usuario
                if(user != null){
                    try{
                        Log.d("USR: ", user.getDisplayName());
                    } catch (NullPointerException e){
                        Log.d("USR:" , "No display name for: " + user.getEmail());
                    }
                }
                // si el usuario no existe abre la pantalla de Login
                else {
                    Intent i = new Intent(MainActivity.this, Login.class);
                    startActivity(i);
                    finish();
                }
            }
        };

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


    @Override
    public void onStart() {
        // cuando se inicia la actividad se añade el listener
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        // cuando finaliza la actividad se elimina el listener
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    /* TEMPORAL!! BORRAR!!! */
    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
    }
}
