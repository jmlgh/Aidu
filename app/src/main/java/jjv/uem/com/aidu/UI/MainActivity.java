package jjv.uem.com.aidu.UI;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import jjv.uem.com.aidu.Adapters.Service_Adapter_RV;
import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.R;
import jjv.uem.com.aidu.util.CardAdapter;
import jjv.uem.com.aidu.util.Constants;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String USERNAME = "username" ;
    public static final String USERUID = "useruid";
    public static final String KEY_SERVICE = "KEY_SERVICE";
    public static final Object APP_VERSION = 1;

    GoogleApiClient mGoogleApiClient;
    private CardAdapter cardAdapter;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseDatabase database;
    private ArrayList<Service>serviceList;
    private RecyclerView recyclerView;
    //private Service_Adapter_RV.OnItemClickListener l;
    private CardAdapter.OnItemClickListener l;
    private Service_Adapter_RV adapter;
    private FloatingActionButton fabSearch, fabAddNew;
    private TextView navUserName, navUserEmail;
    private View headerView;
    private NavigationView navigationView;
    private FirebaseUser usuarioLogeado;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addOnConnectionFailedListener(this)
                .build();

        recyclerView = (RecyclerView) findViewById(R.id.lstLista);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    fabAddNew.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0 || dy < 0 && fabAddNew.isShown()){
                    fabAddNew.hide();
                }
            }
        });
        //serviceList = new ArrayList<>();

        // configuracion para el display de las service cards
        //cardAdapter = new CardAdapter(this, serviceList);

        try{
            Glide.with(this).load(R.drawable.boton_redondo_claro).into((ImageView)findViewById(R.id.card_dots));
        }catch(Exception e){
            e.printStackTrace();
        }
        // control de usuario, si no hay usuario activo abre el login
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                usuarioLogeado = firebaseAuth.getCurrentUser();
                // si existe el usuario
                if(usuarioLogeado != null){
                    try{
                        Log.d("USR: ", usuarioLogeado.getDisplayName());
                        Log.d("MAIL: ", usuarioLogeado.getEmail());




                    } catch (NullPointerException e){
                        Log.d("USR:" , "No display name for: " + usuarioLogeado.getEmail());
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
        getSupportActionBar().setTitle("");
        applyFont(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        // views para el panel lateral
        navUserName = (TextView) headerView.findViewById(R.id.nav_username);
        navUserEmail = (TextView) headerView.findViewById(R.id.nav_usermail);


        // configura el menu lateral con el nombre de usuario y el email
        if(auth.getCurrentUser() != null){
            navUserName.setText(auth.getCurrentUser().getDisplayName());
            navUserEmail.setText(auth.getCurrentUser().getEmail());
        }

        getServices();

        fabAddNew = (FloatingActionButton)findViewById(R.id.fab_new_service);
        fabAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(MainActivity.this,NewService.class);
                i.putExtra(USERNAME,auth.getCurrentUser().getDisplayName());
                i.putExtra(USERUID,auth.getCurrentUser().getUid());
                //finish();
                startActivity(i);
            }
        });

    }

    private void getServices() {
        // Acceso a BBDD Firebase
        if(auth.getCurrentUser() != null){
            database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("services");
            reference.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> iterator = dataSnapshot.getChildren();
                    serviceList = new ArrayList<>();
                    for (DataSnapshot ds : iterator){
                        Service s = ds.getValue(Service.class);
                        if(!s.getUserkey().equals(auth.getCurrentUser().getUid())&&
                                s.getState().equals(Constants.DISPONIBLE)){
                            serviceList.add(s);
                        }

                    }
                    l = initListener();
                    cardAdapter = new CardAdapter(MainActivity.this, serviceList, l);
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
                    recyclerView.setAdapter(cardAdapter);
                    //recyclerView.setHasFixedSize(true);
                    //recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    //recyclerView.setAdapter(adapter);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void applyFont(Toolbar toolbar) {
        Typeface titleFont = Typeface.
                createFromAsset(getApplicationContext().getAssets(), "fonts/BubblerOne-Regular.ttf");
        TextView tv;
        for(int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                tv = (TextView) view;
                  if(tv.getText().equals(getString(R.string.toolbar_title_services))){
                      Log.i(TAG, "i value : "+i);
                    tv.setTypeface(titleFont);
                    break;

                }
            }
        }
    }

    /*private Service_Adapter_RV.OnItemClickListener initListener(){
        Service_Adapter_RV.OnItemClickListener listener = new Service_Adapter_RV.OnItemClickListener() {
            @Override
            public void onItemClick(Service service) {
                Intent i = new Intent(getBaseContext(),ServiceView.class);
                i.putExtra(KEY_SERVICE,service.getServiceKey());
                startActivity(i);
            }
        };
        return listener;


    }*/

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


    @Override
    public void onStart() {
        // cuando se inicia la actividad se añade el listener
        super.onStart();
        auth.addAuthStateListener(authListener);
        mGoogleApiClient.connect();


    }

    @Override
    public void onStop() {
        // cuando finaliza la actividad se elimina el listener
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_services) {
            //finish();
            Intent i = new Intent(this, MyServices.class);
            startActivity(i);
        } else if (id == R.id.nav_communities) {
            Intent i = new Intent(this, Communities.class);
            startActivity(i);
        } else if (id == R.id.nav_chats) {
            Intent i = new Intent(this, Chats.class);
            startActivity(i);
        } else if (id == R.id.nav_home) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_about_us) {
            mostrarInfoAlert();
        } else if (id == R.id.nav_exit) {
            crearDialogo().show();
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
        if(id == R.id.action_service_search){
            Intent i = new Intent(this, ServiceSearch.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
            finishAffinity();
        }
    }

    private Dialog crearDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.msg_dialog_salir));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // firebase sign out
                FirebaseAuth.getInstance().signOut();
                auth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        mGoogleApiClient.disconnect();
                    }
                });
                // google sign out
                if(mGoogleApiClient.isConnected()){

                }
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private boolean mostrarInfoAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.alert_about))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setMessage( getString(R.string.app_version, APP_VERSION) +
                        "\n" +
                        "\n"+ getString(R.string.info_creacion) +
                        "\nJavier Martinez" +
                        "\nVictor Muñoz" +
                        "\nJavier Lozano");
        builder.create().show();
        return true;
    }
}
