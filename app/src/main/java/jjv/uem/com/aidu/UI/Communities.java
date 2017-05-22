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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import jjv.uem.com.aidu.R;

public class Communities extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {



    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String USERNAME = "username" ;
    public static final String USERUID = "useruid";

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseDatabase database;

    private TextView navUserName, navUserEmail;
    private View headerView;
    NavigationView navigationView;
    private FirebaseUser usuarioLogeado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities);

        auth = FirebaseAuth.getInstance();
        initViews();


    }

    private void initViews() {
        //App bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.communities_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        applyFont(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(Communities.this);
        headerView = navigationView.getHeaderView(0);
        // views para el panel lateral
        navUserName = (TextView) headerView.findViewById(R.id.nav_username);
        navUserEmail = (TextView) headerView.findViewById(R.id.nav_usermail);


        // configura el menu lateral con el nombre de usuario y el email
        if(auth.getCurrentUser() != null){
            navUserName.setText(auth.getCurrentUser().getDisplayName());
            navUserEmail.setText(auth.getCurrentUser().getEmail());
        }
    }

    /*private void getServices() {
        // Acceso a BBDD Firebase
        if(auth.getCurrentUser() != null){
            database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("services");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> iterator = dataSnapshot.getChildren();
                    services = new ArrayList<Service>();
                    for (DataSnapshot ds : iterator){
                        Service s = ds.getValue(Service.class);
                        Log.i("SERVICE GET:",s.toString());
                        services.add(s);
                    }
                    l = initListener();
                    adapter = new Service_Adapter_RV(services,l);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    recyclerView.setAdapter(adapter);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }*/

    private void applyFont(Toolbar toolbar) {
        Typeface titleFont = Typeface.
                createFromAsset(getApplicationContext().getAssets(), "fonts/BubblerOne-Regular.ttf");
        TextView tv;
        for(int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                tv = (TextView) view;
                if(tv.getText().equals(getString(R.string.communities_activity_name))){
                    Log.i(TAG, "i value : "+i);
                    tv.setTypeface(titleFont);
                    break;

                }
            }
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_services) {
            finish();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_communities) {

        } else if (id == R.id.nav_chats) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_about_us) {

        } else if (id == R.id.nav_exit) {

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
        if (id == R.id.action_communiti_add) {
            finish();
            Intent i =new Intent(this,NewComunity.class);
            i.putExtra(USERNAME,auth.getCurrentUser().getDisplayName());
            i.putExtra(USERUID,auth.getCurrentUser().getUid());
            startActivity(i);
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.communities_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // firebase sign out
            finish();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);

        }
    }


}
