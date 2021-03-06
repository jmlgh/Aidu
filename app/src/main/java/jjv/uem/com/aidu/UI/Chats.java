package jjv.uem.com.aidu.UI;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import jjv.uem.com.aidu.Adapters.ChatServiceAdapter;
import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.R;

public class Chats extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser fUser;
    private FirebaseDatabase database;
    private ArrayList<Service> serviceList;

    private RecyclerView recyclerView;
    private ChatServiceAdapter.OnItemClickListener listenerChatService;
    private ChatServiceAdapter adapter;
    private TextView tv_chats_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        tv_chats_message = (TextView) findViewById(R.id.tv_message_chats);
        tv_chats_message.setVisibility(View.INVISIBLE);

        database = FirebaseDatabase.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        recyclerView = (RecyclerView)findViewById(R.id.list_service_chats);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getServices();
    }

    private ChatServiceAdapter.OnItemClickListener initListener() {
        ChatServiceAdapter.OnItemClickListener listener = new ChatServiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Service service) {
                // ABRIR VENTANA DE CHAT
                Intent i = new Intent(getBaseContext(),ChatConversation.class);
                i.putExtra(ServiceView.SERVICE_KEY,service.getServiceKey());
                i.putExtra(ServiceView.SERVICE_USER_KEY,service.getUserkey());
                i.putExtra(ServiceView.SERVICE_KIND,service.getKind());
                i.putExtra(ServiceView.SERVICE_STATE,service.getState());
                startActivity(i);
            }
        };
        return listener;
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
                        // si el usuario ha creado el servicio o ha aceptado hacerlo
                        // muestra una sala de chat
                        if(s.getUserkey().equals(fUser.getUid())
                                || s.getUserkeyInterested().equals(fUser.getUid())){
                            serviceList.add(s);
                        }
                        Log.i("chats R:",s.toString());
                    }

                    if(serviceList.size()<=0){
                        tv_chats_message.setVisibility(View.VISIBLE);
                    }else{
                        tv_chats_message.setVisibility(View.INVISIBLE);
                    }
                    ChatServiceAdapter.OnItemClickListener l = initListener();
                    ChatServiceAdapter chatCardAdapter = new ChatServiceAdapter(Chats.this, serviceList, l);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(chatCardAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
