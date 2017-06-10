package jjv.uem.com.aidu.UI;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import jjv.uem.com.aidu.Adapters.ChatAdapter;
import jjv.uem.com.aidu.Model.MensajeChat;
import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.R;

public class ChatConversation extends AppCompatActivity {

    private FirebaseDatabase database;
    private String serviceKey;
    private String keyUserProp;
    private String userNameProp;
    private DatabaseReference ref;
    private Button btnEnviar;
    private EditText etMensaje;
    private FirebaseUser usuarioLogueado;
    private RecyclerView listaMensajes;
    private ChatAdapter chatAdapter;
    private Service sModificado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);
        Intent i = getIntent();
        serviceKey =i.getStringExtra(ServiceView.SERVICE_KEY);
        userNameProp = i.getStringExtra(ServiceView.SERVICE_USERNAME);
        keyUserProp = i.getStringExtra(ServiceView.SERVICE_USER_KEY);
        etMensaje = (EditText) findViewById(R.id.et_msg);
        btnEnviar = (Button) findViewById(R.id.btn_enviar);
        listaMensajes = (RecyclerView) findViewById(R.id.lista_msgs);

        initDatabase();
        String estate = "ESPERA";
        changeStateService(estate);



        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String horaFormateada = formatearHora(new Date().getTime());
                MensajeChat msgChat = new MensajeChat(etMensaje.getText().toString(), usuarioLogueado.getDisplayName(), usuarioLogueado.getEmail(), horaFormateada);
                // push mensaje a la bd
                ref.push().setValue(msgChat);
                etMensaje.setText("");
                etMensaje.setHint(getString(R.string.chat_hint));
                etMensaje.clearFocus();
                esconderTeclado(ChatConversation.this);
            }
        });


    }

    private void changeStateService(final String estate) {

        database = FirebaseDatabase.getInstance();


        DatabaseReference dbref = database.getReference("services/"+serviceKey);
        DatabaseReference dbrefUS = database.getReference("user-services/"+keyUserProp+"/"+serviceKey);
        ValueEventListener ve = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Service sModificado = dataSnapshot.getValue(Service.class);
                sModificado.setState(estate);
                dataSnapshot.getRef().setValue(sModificado);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        dbrefUS.addListenerForSingleValueEvent(ve);
        dbref.addListenerForSingleValueEvent(ve);


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
        ref = database.getReference("Chat/"+serviceKey);

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

}
