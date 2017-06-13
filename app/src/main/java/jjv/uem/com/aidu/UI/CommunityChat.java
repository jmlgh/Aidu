package jjv.uem.com.aidu.UI;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import jjv.uem.com.aidu.Adapters.ChatAdapter;
import jjv.uem.com.aidu.Model.Community;
import jjv.uem.com.aidu.Model.MensajeChat;
import jjv.uem.com.aidu.R;


public class CommunityChat extends AppCompatActivity {
    private String communitykey;
    private FirebaseDatabase database;
    private Button btnEnviar;
    private EditText etMensaje;
    private RecyclerView listaMensajes;
    private DatabaseReference ref;
    private FirebaseUser usuarioLogueado;
    private ChatAdapter chatAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_chat);

        Intent i = getIntent();
        communitykey = i.getStringExtra(Communities.KEY_COMMUNITY);
        etMensaje = (EditText) findViewById(R.id.et_msg_com);
        btnEnviar = (Button) findViewById(R.id.btn_enviar_com);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listaMensajes = (RecyclerView) findViewById(R.id.lista_msgs_com);


        initDatabase();

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = etMensaje.getText().toString();
                sendMessage(mensaje);
                etMensaje.setText("");
                etMensaje.setHint(getString(R.string.chat_hint));
                etMensaje.clearFocus();
                esconderTeclado(CommunityChat.this);
            }
        });

    }


    private void sendMessage(String mensaje) {
        String horaFormateada = formatearHora(new Date().getTime());
        MensajeChat msgChat = new MensajeChat(mensaje, usuarioLogueado.getDisplayName(), usuarioLogueado.getEmail(), horaFormateada);
        // push mensaje a la bd
        ref.push().setValue(msgChat);
    }


    public static void esconderTeclado(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String formatearHora(long hora){
        String horaFormateada;
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        cal.setTimeInMillis(hora);

        horaFormateada = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        return horaFormateada;
    }


    private void initDatabase() {
        database = FirebaseDatabase.getInstance();
        usuarioLogueado = FirebaseAuth.getInstance().getCurrentUser();
        //construimos la referenecia
        ref = database.getReference("Chat-Community/"+communitykey);

        if(usuarioLogueado != null){
            ref.addChildEventListener(new ChildEventListener() {
                ArrayList<MensajeChat> mensajes = new ArrayList<>();
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // recupera el mensaje y lo añade en la UI
                    MensajeChat msg = dataSnapshot.getValue(MensajeChat.class);
                    mensajes.add(msg);
                    chatAdapter = new ChatAdapter(mensajes);
                    chatAdapter.notifyDataSetChanged();
                    // sin esta linea el layout no muestra nada
                    listaMensajes.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    // para que el recycler no suba al primer elemento
                    listaMensajes.scrollToPosition(mensajes.size()-1);
                    listaMensajes.setAdapter(chatAdapter);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    MensajeChat msg = dataSnapshot.getValue(MensajeChat.class);
                    mensajes.add(msg);
                    chatAdapter = new ChatAdapter(mensajes);
                    chatAdapter.notifyDataSetChanged();
                    // sin esta linea el recycler view  no muestra nada
                    listaMensajes.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    // para que el recycler no suba al primer elemento
                    listaMensajes.scrollToPosition(mensajes.size()-1);
                    listaMensajes.setAdapter(chatAdapter);

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
