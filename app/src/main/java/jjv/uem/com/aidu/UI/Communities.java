package jjv.uem.com.aidu.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import jjv.uem.com.aidu.Model.Community;
import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.R;
import jjv.uem.com.aidu.util.CardAdapter;
import jjv.uem.com.aidu.util.CommunitiesCardAdapter;

public class Communities extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String USERNAME = "username";
    public static final String USERUID = "useruid";
    public static final String KEY_COMMUNITY = "key_community";


    private FirebaseAuth auth;
    private FirebaseDatabase database;

    private TextView navUserName, navUserEmail;
    private View headerView;
    private NavigationView navigationView;
    private RecyclerView cummunitiesrecicler;
    private FirebaseUser usuarioLogeado;
    private ArrayList<Community> communitiesList;
    private CommunitiesCardAdapter cardAdapter;
    private CommunitiesCardAdapter.OnItemClickListener l;
    private CommunitiesCardAdapter.OnItemLongClickListener lc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities);

        auth = FirebaseAuth.getInstance();


        initViews();
        getServices();

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
        cummunitiesrecicler = (RecyclerView) findViewById(R.id.lstCommunities);

        // configura el menu lateral con el nombre de usuario y el email
        if (auth.getCurrentUser() != null) {
            navUserName.setText(auth.getCurrentUser().getDisplayName());
            navUserEmail.setText(auth.getCurrentUser().getEmail());
        }
    }

    private void getServices() {
        // Acceso a BBDD Firebase
        if (auth.getCurrentUser() != null) {
            database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("communities");
            //Query query = reference.orderByChild("members").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> iterator = dataSnapshot.getChildren();
                    Log.e("Comunidad: ", " " + dataSnapshot.getChildrenCount());
                    communitiesList = new ArrayList<>();
                    for (DataSnapshot ds : iterator) {
                        Community c = ds.getValue(Community.class);
                        //Log.i("SERVICE GET:",c.toString());
                        communitiesList.add(c);
                        Log.e("Comunidad: ", c.getKey());
                    }
                    l = initListener();
                    lc = initlongListener();
                    //adapter = new Service_Adapter_RV(serviceList,l);
                    cardAdapter = new CommunitiesCardAdapter(Communities.this, communitiesList, l,lc);
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(Communities.this, 2);
                    cummunitiesrecicler.setLayoutManager(layoutManager);
                    cummunitiesrecicler.setItemAnimator(new DefaultItemAnimator());
                    cummunitiesrecicler.addItemDecoration(new Communities.GridSpacingItemDecoration(2, dpToPx(10), true));
                    cummunitiesrecicler.setAdapter(cardAdapter);
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

    private CommunitiesCardAdapter.OnItemClickListener initListener() {
        CommunitiesCardAdapter.OnItemClickListener listener = new CommunitiesCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Community item) {
            Intent i = new Intent(Communities.this,CommunityServicesActivity.class);
                i.putExtra(KEY_COMMUNITY,item);
                startActivity(i);
                finish();

            }
        };

        return listener;
    }

    private CommunitiesCardAdapter.OnItemLongClickListener initlongListener() {
        CommunitiesCardAdapter.OnItemLongClickListener listener = new CommunitiesCardAdapter.OnItemLongClickListener() {
            @Override
            public void OnItemLongClickListener(final Community item) {
                CharSequence options[] = new CharSequence[]{getString(R.string.action_moreInfo), getString(R.string.action_addmember),getString(R.string.action_delete)};

                AlertDialog.Builder picker = new AlertDialog.Builder(Communities.this);
                picker.setTitle(getString(R.string.communities_Options_dialog));


                picker.setCancelable(true);
                picker.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {//more info
                            Intent i = new Intent(Communities.this,CommunityInfo.class);

                            i.putExtra(Communities.KEY_COMMUNITY,item);
                            startActivity(i);
                        } else if (which == 1) {//add member

                        } else {//delete community

                        }
                    }
                });
                picker.show();
            }
        };

        return listener;
    }

    private void applyFont(Toolbar toolbar) {
        Typeface titleFont = Typeface.
                createFromAsset(getApplicationContext().getAssets(), "fonts/BubblerOne-Regular.ttf");
        TextView tv;
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                tv = (TextView) view;
                if (tv.getText().equals(getString(R.string.communities_activity_name))) {
                    Log.i(TAG, "i value : " + i);
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

        } else if (id == R.id.nav_home) {
            finish();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);

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
            Intent i = new Intent(this, NewComunity.class);
            i.putExtra(USERNAME, auth.getCurrentUser().getDisplayName());
            i.putExtra(USERUID, auth.getCurrentUser().getUid());
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


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

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

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }



}
