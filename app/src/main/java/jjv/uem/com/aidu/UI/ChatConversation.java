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
import jjv.uem.com.aidu.Model.User;
import jjv.uem.com.aidu.R;
import jjv.uem.com.aidu.util.Constants;

public class ChatConversation extends AppCompatActivity {

    private FirebaseDatabase database;

    private String serviceKey;
    private String keyUserProp;
    private String userNameProp;
    private DatabaseReference ref;
    private Button btnEnviar,btnFinalizar,btnAceptar,btnDenegar;
    private EditText etMensaje;
    private FirebaseUser usuarioLogueado;
    private RecyclerView listaMensajes;
    private ChatAdapter chatAdapter;
    private String estate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);
        Intent i = getIntent();
        serviceKey =i.getStringExtra(ServiceView.SERVICE_KEY);
        userNameProp = i.getStringExtra(ServiceView.SERVICE_USERNAME);
        keyUserProp = i.getStringExtra(ServiceView.SERVICE_USER_KEY);
        estate = i.getStringExtra(ServiceView.SERVICE_STATE);
        etMensaje = (EditText) findViewById(R.id.et_msg);
        btnEnviar = (Button) findViewById(R.id.btn_enviar);
        btnAceptar = (Button) findViewById(R.id.btnAccept);
        btnDenegar = (Button) findViewById(R.id.btnDecline);
        btnFinalizar = (Button) findViewById(R.id.btnFinal);


        listaMensajes = (RecyclerView) findViewById(R.id.lista_msgs);

        initDatabase();

        //changeStateService(estate);

        setButtonsVisibility();



        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(usuarioLogueado.getUid().equals(keyUserProp)){
                    changeStateService(Constants.EN_CURSO);
                    estate = Constants.EN_CURSO;
                    setButtonsVisibility();
                    sendMessage(getString(R.string.service_message_confirm));
                }else{
                    changeStateService(Constants.ESPERA);
                    estate = Constants.ESPERA;
                    setButtonsVisibility();
                    sendMessage(getString(R.string.service_message_interested));
                }


            }
        });
        btnDenegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeStateService(Constants.DISPONIBLE);
                estate = Constants.DISPONIBLE;
                setButtonsVisibility();
                sendMessage(getString(R.string.service_denegated));
                ref.removeValue();

            }
        });

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStateService(Constants.FINALIZADO);
                estate = Constants.FINALIZADO;
                sendMessage(getString(R.string.service_finished));
                setButtonsVisibility();
                ref.removeValue();

                Intent i = new Intent(getBaseContext(),MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = etMensaje.getText().toString();
                sendMessage(mensaje);
                etMensaje.setText("");
                etMensaje.setHint(getString(R.string.chat_hint));
                etMensaje.clearFocus();
                esconderTeclado(ChatConversation.this);
            }
        });

        DatabaseReference dbref = database.getReference("services/"+serviceKey);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Service sModificado = dataSnapshot.getValue(Service.class);
                estate = sModificado.getState();
                setButtonsVisibility();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



    private void sendMessage(String mensaje) {
        String horaFormateada = formatearHora(new Date().getTime());
        MensajeChat msgChat = new MensajeChat(mensaje, usuarioLogueado.getDisplayName(), usuarioLogueado.getEmail(), horaFormateada);
        // push mensaje a la bd
        ref.push().setValue(msgChat);
    }

    private void setButtonsVisibility() {

        if(estate.equals(Constants.FINALIZADO)){
            btnAceptar.setVisibility(View.INVISIBLE);
            btnDenegar.setVisibility(View.INVISIBLE);
            btnFinalizar.setVisibility(View.INVISIBLE);
        }else{
            if(!usuarioLogueado.getUid().equals(keyUserProp)){

                if(estate.equals(Constants.DISPONIBLE)){
                    btnDenegar.setVisibility(View.INVISIBLE);
                    btnAceptar.setVisibility(View.VISIBLE);
                }else if(estate.equals(Constants.FINALIZADO)) {
                    btnDenegar.setVisibility(View.INVISIBLE);
                    btnAceptar.setVisibility(View.INVISIBLE);
                }else{
                    btnDenegar.setVisibility(View.VISIBLE);
                    btnAceptar.setVisibility(View.INVISIBLE);
                }

                btnFinalizar.setVisibility(View.INVISIBLE);

            }else {
                if (estate.equals(Constants.ESPERA)) {
                    btnDenegar.setVisibility(View.VISIBLE);
                    btnAceptar.setVisibility(View.VISIBLE);
                    btnFinalizar.setVisibility(View.INVISIBLE);
                } else if (estate.equals(Constants.EN_CURSO)) {
                    btnDenegar.setVisibility(View.VISIBLE);
                    btnAceptar.setVisibility(View.INVISIBLE);
                    btnFinalizar.setVisibility(View.VISIBLE);
                } else{
                    btnDenegar.setVisibility(View.INVISIBLE);
                    btnAceptar.setVisibility(View.INVISIBLE);
                    btnFinalizar.setVisibility(View.INVISIBLE);
                }
            }
        }




    }

    private void changeStateService(final String stat) {

        database = FirebaseDatabase.getInstance();
        estate=stat;


        DatabaseReference dbref = database.getReference("services/"+serviceKey);
        DatabaseReference dbrefUS = database.getReference("user-services/"+keyUserProp+"/"+serviceKey);
        ValueEventListener ve = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Service sModificado = dataSnapshot.getValue(Service.class);
                sModificado.setState(stat);
                if(stat.equals(Constants.FINALIZADO)){
                    updatePointsUser(sModificado.getUserkeyInterested(),sModificado.getPrice_points());
                    sModificado.setUserkeyInterested("");
                    dataSnapshot.getRef().removeValue();
                }else{
                    if(stat.equals(Constants.ESPERA)){
                        sModificado.setUserkeyInterested(usuarioLogueado.getUid());
                    }else if(stat.equals(Constants.DISPONIBLE)){
                        sModificado.setUserkeyInterested("");
                    }
                    dataSnapshot.getRef().setValue(sModificado);
                }

                Log.i("ESTADO SERVICIO 1" , "ESTATE : "+estate);
                Log.i("ESTADO SERVICIO 2" , "STAT : "+stat);
               // estate = sModificado.getState();

                Log.i("ESTADO SERVICIO 3" , sModificado.getState());
                Log.i("ESTADO SERVICIO 4" , estate);



                setButtonsVisibility();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        dbrefUS.addListenerForSingleValueEvent(ve);
        dbref.addListenerForSingleValueEvent(ve);


    }

    private void updatePointsUser(String userkeyInterested, final String price_points) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user/"+userkeyInterested);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userMod = dataSnapshot.getValue(User.class);
                userMod.addPoints(Integer.parseInt(price_points));
                dataSnapshot.getRef().setValue(userMod);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
