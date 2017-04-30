package jjv.uem.com.aidu.UI;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import jjv.uem.com.aidu.Adapters.Service_Adapter_RV;
import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.R;
import jjv.uem.com.aidu.util.TextViewCustom;


public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private ArrayList<Service>services = new ArrayList<>();
    private RecyclerView recyclerView;
    private Service_Adapter_RV.OnItemClickListener l;
    private Service_Adapter_RV adapter;

    private static final String TAG = MainActivity.class.getSimpleName();

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
        applyFont(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

    private void applyFont(Toolbar toolbar) {
        Typeface titleFont = Typeface.
                createFromAsset(getApplicationContext().getAssets(), "fonts/BubblerOne-Regular.ttf");
        TextView tv;
        for(int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                tv = (TextView) view;
                  if(tv.getText().equals(toolbar.getTitle())){
                      Log.i(TAG, "i value : "+i);
                    tv.setTypeface(titleFont);
                    break;
                }
            }
        }
    }

    private Service_Adapter_RV.OnItemClickListener initListener(){
        Service_Adapter_RV.OnItemClickListener listener = new Service_Adapter_RV.OnItemClickListener() {
            @Override
            public void onItemClick(Service item) {
                //TODO LISTENER DEL RECYCLER VIEW

                Intent i = new Intent(getBaseContext(),ServiceView.class);
                startActivity(i);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FirebaseAuth.getInstance().signOut();
            super.onBackPressed();
        }
    }

}
