package jjv.uem.com.aidu.UI;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import jjv.uem.com.aidu.Adapters.Service_Adapter_RV;
import jjv.uem.com.aidu.Model.Community;
import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.R;
import jjv.uem.com.aidu.util.CardAdapter;

public class CommunityServicesActivity extends AppCompatActivity {

    private ActionBar actBar;

    private FirebaseDatabase database;
    private ArrayList<Service> serviceList;
    private RecyclerView recyclerView;
    //private Service_Adapter_RV.OnItemClickListener l;
    private CardAdapter.OnItemClickListener l;
    private Service_Adapter_RV adapter;
    private CardAdapter cardAdapter;
    private Community community;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_services);
        initViews();

        getServices();
    }

    private void initViews() {
        actBar = getSupportActionBar();
        actBar.setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();
        community = i.getParcelableExtra(Communities.KEY_COMMUNITY);

        Typeface titleFont = Typeface.
                createFromAsset(getApplicationContext().getAssets(), "fonts/BubblerOne-Regular.ttf");
        SpannableString s = new SpannableString(community.getName());
        s.setSpan(titleFont, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        actBar.setTitle(s);

        //actBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1c313aeg")));
        recyclerView = (RecyclerView) findViewById(R.id.lstserviceList);


    }

    private void getServices() {

        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("services");
        Log.e("communities ser:",community.getKey());
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
                    Log.i("SERVICE GET:", s.toString());
                    serviceList.add(s);
                }
                l = initListener();
                //adapter = new Service_Adapter_RV(serviceList,l);
                cardAdapter = new CardAdapter(CommunityServicesActivity.this, serviceList, l);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(CommunityServicesActivity.this, 2);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new CommunityServicesActivity.GridSpacingItemDecoration(2, dpToPx(10), true));
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

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_chat) {
            Intent i = new Intent(this, ServiceSearch.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.communitiesservices_menu, menu);
        return true;
    }

    /*@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onBackPressed() {

            super.onBackPressed();
            finishAffinity();

    }*/

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
