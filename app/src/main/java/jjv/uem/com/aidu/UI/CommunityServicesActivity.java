package jjv.uem.com.aidu.UI;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jjv.uem.com.aidu.Adapters.Service_Adapter_RV;
import jjv.uem.com.aidu.Model.Community;
import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.Model.User;
import jjv.uem.com.aidu.R;
import jjv.uem.com.aidu.util.CardAdapter;
import jjv.uem.com.aidu.util.Constants;

public class CommunityServicesActivity extends AppCompatActivity {
    private static final String TAG = NewComunity.class.getSimpleName();
    private ActionBar actBar;

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private ArrayList<Service> serviceList;
    private RecyclerView recyclerView;
    //private Service_Adapter_RV.OnItemClickListener l;
    private CardAdapter.OnItemClickListener l;
    private Service_Adapter_RV adapter;
    private CardAdapter cardAdapter;

    private Menu menu;
    private Community community;
    private User member;
    private ArrayList<String> membersNames;
    private ArrayList<User> members;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_services);
        Intent i = getIntent();
        String communityKey = getIntent().getStringExtra(Communities.KEY_COMMUNITY);
        database = FirebaseDatabase.getInstance();
        getComunity(communityKey);


    }

    private void initViews() {
        actBar = getSupportActionBar();
        actBar.setDisplayHomeAsUpEnabled(true);

        Log.e("crear actividad", "" + community.getName() + " " + community.getOwner());

        Typeface titleFont = Typeface.
                createFromAsset(getApplicationContext().getAssets(), "fonts/BubblerOne-Regular.ttf");
        SpannableString s = new SpannableString(community.getName());
        s.setSpan(titleFont, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        actBar.setTitle(s);

        //actBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1c313aeg")));
        recyclerView = (RecyclerView) findViewById(R.id.lstserviceList);
        getServices();


    }

    private void getServices() {


        DatabaseReference reference = database.getReference("services");
        Log.e("communities ser:", community.getKey());
        Query query = reference.orderByChild("community").equalTo(community.getKey());
        query.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("services child: ", "" + dataSnapshot.getChildrenCount());
                Log.e("services child: ", "" + dataSnapshot.getKey());
                Iterable<DataSnapshot> iterator = dataSnapshot.getChildren();
                serviceList = new ArrayList<>();
                for (DataSnapshot ds : iterator) {

                    Service s = ds.getValue(Service.class);
                    if(s.getState().equals(Constants.DISPONIBLE)) {
                        Log.i("SERVICE GET:", s.toString());
                        serviceList.add(s);
                    }
                }
                l = initListener();
                //adapter = new Service_Adapter_RV(serviceList,l);
                cardAdapter = new CardAdapter(CommunityServicesActivity.this, serviceList, l);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(CommunityServicesActivity.this, 2);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                //recyclerView.addItemDecoration(new CommunityServicesActivity.GridSpacingItemDecoration(2, dpToPx(10), true));
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

    private CardAdapter.OnItemClickListener initListener() {
        CardAdapter.OnItemClickListener listener = new CardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Service item) {
                Intent i = new Intent(getBaseContext(), ServiceView.class);
                i.putExtra(MainActivity.KEY_SERVICE, item.getServiceKey());
                startActivity(i);
            }
        };

        return listener;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        if (community.getOwner().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            getMenuInflater().inflate(R.menu.communitiesservices_menu_admin, menu);
        } else {
            getMenuInflater().inflate(R.menu.communitiesservices_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_chat) {
            Log.d("accion ", "chat");


        }*/
        if (id == R.id.action_moreInfo) {
            Log.d("accion ", "mas informacion");

            Intent i = new Intent(CommunityServicesActivity.this, CommunityInfo.class);

            i.putExtra(Communities.KEY_COMMUNITY, community.getKey());
            startActivity(i);
        }

        if (id == R.id.action_addmember) {
            addnewDialog();

        }
        if (id == R.id.action_change_admin) {
            recoverMembers();

        }

        if (id == R.id.action_delete) {
            deletecommunitie(community).show();

        }
        if (id == R.id.action_leavecommunity) {
            leavecommunitie(community).show();

        }

        return super.onOptionsItemSelected(item);
    }


    /*@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onBackPressed() {

            super.onBackPressed();
            finishAffinity();

    }*/


    @Override
    public void onBackPressed() {

        // firebase sign out
        finish();
        Intent i = new Intent(this, Communities.class);
        startActivity(i);


    }

    public void recoverMembers() {
        final ArrayList<String> membersk = community.getMembers();
        membersNames = new ArrayList<>();
        members = new ArrayList<>();
        for (int i = 0; i < membersk.size(); i++) {
            member = new User();
            DatabaseReference reference = database.getReference("user/" + membersk.get(i));
            final int finalI = i;
            reference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.w("User recovered:", dataSnapshot.getKey() + " " + membersk.get(finalI));
                    member = dataSnapshot.getValue(User.class);
                    Log.w("User recovered:", member.getDisplayName());
                    members.add(member);
                    membersNames.add(member.getDisplayName());
                    if (finalI == membersk.size() - 1) changeAdmin();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }

    }

    public void changeAdmin() {


        final CharSequence membersn[] = membersNames.toArray(new CharSequence[membersNames.size()]);
        AlertDialog.Builder picker = new AlertDialog.Builder(CommunityServicesActivity.this);
        picker.setTitle(getString(R.string.community_Services_Change_admin));
        picker.setCancelable(true);
        picker.setItems(membersn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("onclick", "pulsado: " + which);
                if (members.get(which).getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Toast.makeText(getApplicationContext(), getText(R.string.community_services_already_admin), Toast.LENGTH_LONG).show();
                    } else {
                    crearDialogo(members.get(which)).show();
                }
            }
        });
        picker.show();


    }

    public void getComunity(final String key) {

        DatabaseReference reference = database.getReference("communities/" + key);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                community = dataSnapshot.getValue(Community.class);
                initViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private Dialog crearDialogo(final User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CommunityServicesActivity.this);
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.community_Services_areSure, user.getDisplayName()));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                community.setOwner(user.getKey());
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

                Toast.makeText(CommunityServicesActivity.this, getText(R.string.community_Services_owner_changed), Toast.LENGTH_SHORT).show();
                menu.clear();
                getMenuInflater().inflate(R.menu.communitiesservices_menu, menu);
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


    public void addnewDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CommunityServicesActivity.this);
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
                adduser(username);
            }
        });
        dialogBuilder.create().show();
    }

    private void adduser(String username) {
        DatabaseReference reference = database.getReference("user");
        Query query = reference.orderByChild("email").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("services child: ", "" + dataSnapshot.getChildrenCount());
                Log.e("services child: ", "" + dataSnapshot.getKey());
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        User user = ds.getValue(User.class);
                        if (community.getMembers().contains(user.getKey())) {
                            Toast.makeText(CommunityServicesActivity.this, getText(R.string.community_toast_user_exist), Toast.LENGTH_SHORT).show();
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

                            Toast.makeText(CommunityServicesActivity.this, getText(R.string.community_toast_user_added), Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(CommunityServicesActivity.this, R.string.community_toast_user_not_found, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    private Dialog leavecommunitie(final Community community) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CommunityServicesActivity.this);
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.community_dialog_exit));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                Log.e("codigoss", community.getMembers().toString());
                Log.e("codigoss", FirebaseAuth.getInstance().getCurrentUser().getUid() + " " + community.getMembers().contains(FirebaseAuth.getInstance().getCurrentUser().getUid()));
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
                Intent i = new Intent(CommunityServicesActivity.this, Communities.class);
                startActivity(i);
                finish();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(CommunityServicesActivity.this);
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.community_dialog_delete));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference reference = database.getReference("communities/"+community.getKey());
                reference.removeValue();
                Intent i = new Intent(CommunityServicesActivity.this, Communities.class);
                startActivity(i);
                finish();
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
