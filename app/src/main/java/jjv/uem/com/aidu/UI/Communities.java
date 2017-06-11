package jjv.uem.com.aidu.UI;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jjv.uem.com.aidu.Model.Community;
import jjv.uem.com.aidu.Model.User;
import jjv.uem.com.aidu.R;
import jjv.uem.com.aidu.util.CommunitiesCardAdapter;

public class Communities extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public static final String USERNAME = "username";
    public static final String USERUID = "useruid";
    public static final String KEY_COMMUNITY = "key_community";
    private static final String TAG = MainActivity.class.getSimpleName();
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
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities);

        auth = FirebaseAuth.getInstance();


        initViews();
        getCommunities();

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

    private void getCommunities() {
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
                    communitiesList = new ArrayList<>();
                    for (DataSnapshot ds : iterator) {
                        Community c = ds.getValue(Community.class);
                        if (c.getMembers().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                            //Log.i("SERVICE GET:",c.toString());
                            communitiesList.add(c);
                        }
                    }
                    l = initListener();
                    lc = initlongListener();
                    //adapter = new Service_Adapter_RV(serviceList,l);
                    cardAdapter = new CommunitiesCardAdapter(Communities.this, communitiesList, l, lc);
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
                Intent i = new Intent(Communities.this, CommunityServicesActivity.class);
                i.putExtra(KEY_COMMUNITY, item.getKey());
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
                CharSequence options[];
                if (item.getOwner().equals(auth.getCurrentUser().getUid())) {
                    options = new CharSequence[]{getString(R.string.action_moreInfo), getString(R.string.action_addmember), getString(R.string.action_delete)};
                } else {
                    options = new CharSequence[]{getString(R.string.action_moreInfo), getString(R.string.action_addmember), getString(R.string.action_leavecommunity)};
                }

                AlertDialog.Builder picker = new AlertDialog.Builder(Communities.this);
                picker.setTitle(getString(R.string.communities_Options_dialog));


                picker.setCancelable(true);
                picker.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {//more info
                            Intent i = new Intent(Communities.this, CommunityInfo.class);

                            i.putExtra(Communities.KEY_COMMUNITY, item.getKey());
                            startActivity(i);
                        } else if (which == 1) {
                            addnewDialog(item);
                        } else {
                            if (item.getOwner().equals(auth.getCurrentUser().getUid())) {
                                deletecommunitie(item).show();
                            } else {
                                leavecommunitie(item).show();
                            }

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

    public void addnewDialog(final Community community) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Communities.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_addnewmember, null);
        dialogBuilder.setView(dialogView);
        final EditText et_user = (EditText) dialogView.findViewById(R.id.et_dusername);
        dialogBuilder.setTitle(getString(R.string.community_services_add_new));
        //dialogBuilder.setMessage(getString(R.string.ins_pl));

        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        dialogBuilder.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String username = et_user.getText().toString();
                adduser(username, community);
            }
        });
        dialogBuilder.create().show();
    }

    private void adduser(String username, final Community community) {
        DatabaseReference reference = database.getReference("user");
        Query query = reference.orderByChild("displayName").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("services child: ", "" + dataSnapshot.getChildrenCount());
                Log.e("services child: ", "" + dataSnapshot.getKey());
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        User user = ds.getValue(User.class);
                        if (community.getMembers().contains(user.getKey())) {
                            Toast.makeText(Communities.this, getText(R.string.community_toast_user_exist), Toast.LENGTH_SHORT).show();
                        } else {
                            community.getMembers().add(user.getKey());
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            Map<String, Object> communit = community.toMap();
                            Map<String, Object> childUpdates = new HashMap<>();

                            childUpdates.put("/communities/" + community.getKey(), communit);
                            //childUpdates.put("/user-services/" + userUid + "/" + key, servic);
                            mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, getString(R.string.community_Services_owner_changed));
                                }
                            });

                            Toast.makeText(Communities.this, getText(R.string.community_toast_user_added), Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(Communities.this, R.string.community_toast_user_not_found, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    public void joincommunityDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Communities.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_addnewmember, null);
        dialogBuilder.setView(dialogView);
        final EditText et_user = (EditText) dialogView.findViewById(R.id.et_dusername);
        et_user.setHint(R.string.community_services_dialog_hint_Community_code);
        dialogBuilder.setTitle(getString(R.string.community_services_joinCommunity));
        //dialogBuilder.setMessage(getString(R.string.ins_pl));

        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        dialogBuilder.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String username = et_user.getText().toString();
                addtocommunity(username);
            }
        });
        dialogBuilder.create().show();
    }

    private void addtocommunity(String communitykey) {
        DatabaseReference reference = database.getReference("communities/" + communitykey);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("services child: ", "" + dataSnapshot.getValue());
                Log.e("services child: ", "" + dataSnapshot.getKey());
                if (dataSnapshot.getValue() != null) {

                        Community community = dataSnapshot.getValue(Community.class);
                        if (community.getMembers().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            Toast.makeText(Communities.this, getText(R.string.community_toast_Community_exist), Toast.LENGTH_SHORT).show();
                        } else {
                            community.getMembers().add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            Map<String, Object> communit = community.toMap();
                            Map<String, Object> childUpdates = new HashMap<>();

                            childUpdates.put("/communities/" + community.getKey(), communit);
                            //childUpdates.put("/user-services/" + userUid + "/" + key, servic);
                            mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, getString(R.string.community_Services_owner_changed));
                                }
                            });

                        }


                } else {
                    Toast.makeText(Communities.this, R.string.community_toast_community_not_found, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private Dialog leavecommunitie(final Community community) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Communities.this);
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.community_dialog_exit));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                community.getMembers().remove(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Map<String, Object> communit = community.toMap();
                Map<String, Object> childUpdates = new HashMap<>();

                childUpdates.put("/communities/" + community.getKey(), communit);
                //childUpdates.put("/user-services/" + userUid + "/" + key, servic);
                mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, getString(R.string.community_Services_owner_changed));
                    }
                });

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

    private Dialog deletecommunitie(final Community community) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Communities.this);
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.community_dialog_delete));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference reference = database.getReference("communities/" + community.getKey());
                reference.removeValue();
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


            CharSequence options[] = new CharSequence[]{getString(R.string.community_dialog_addnew), getString(R.string.community_dialog_join)};
            AlertDialog.Builder picker = new AlertDialog.Builder(Communities.this);
            picker.setTitle(getString(R.string.communities_Options_dialog));

            picker.setCancelable(true);
            picker.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {//more info
                        finish();
                        Intent i = new Intent(Communities.this, NewComunity.class);
                        i.putExtra(USERNAME, auth.getCurrentUser().getDisplayName());
                        i.putExtra(USERUID, auth.getCurrentUser().getUid());
                        startActivity(i);
                        finish();
                    } else {
                        joincommunityDialog();
                    }
                }
            });
            picker.show();


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
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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


}
