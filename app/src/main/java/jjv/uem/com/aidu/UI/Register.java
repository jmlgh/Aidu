package jjv.uem.com.aidu.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.Model.User;
import jjv.uem.com.aidu.R;
import jjv.uem.com.aidu.util.Constants;

public class Register extends AppCompatActivity {

    private static final String ERROR_FORMAT = "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.";
    private static final String ERROR_MAIL_EXISTENTE = "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.";

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private TextView tvLogo;
    private EditText etEmail, etUserName, etPwd;
    private Button btnRegister;
    private String password;
    private String TAG = Register.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initFirebase();
        setTypeFace();
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser usuarioActual = firebaseAuth.getCurrentUser();
                if(usuarioActual != null){
                    Log.d("LOGEO: ", "OK! -> " + usuarioActual.getEmail());
                    // si el usuario se loguea correctamente termina esta actividad
                    // y abre MainActivity
                    finish();
                    Intent i = new Intent(Register.this, Tutorial.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }else{
                    Log.d("LOGEO: ", "ERROR!");
                }
            }
        };
    }

    private void initViews() {
        tvLogo = (TextView) findViewById(R.id.r_tv_logo);
        etEmail = (EditText) findViewById(R.id.r_et_email);
        etUserName = (EditText) findViewById(R.id.r_et_username);
        etPwd = (EditText) findViewById(R.id.r_et_password);
        btnRegister = (Button) findViewById(R.id.btn_register);
    }

    private void setTypeFace() {
        Typeface bubblerFont = Typeface.createFromAsset(getAssets(), "fonts/BubblerOne-Regular.ttf");
        tvLogo.setTypeface(bubblerFont);
        etUserName.setTypeface(bubblerFont);
        etEmail.setTypeface(bubblerFont);
        etPwd.setTypeface(bubblerFont);
        btnRegister.setTypeface(bubblerFont);
    }

    public void createNewAccount(View v){
        final String userEmail = etEmail.getText().toString();
        final String displayName = etUserName.getText().toString();
        password = etPwd.getText().toString();

        if (userEmail.equals("") || displayName.equals("") || password.equals("")) {
            Toast.makeText(this, getString(R.string.register_toast_enterallfields), Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6){
            Toast.makeText(this, getString(R.string.register_toast_password_length), Toast.LENGTH_SHORT).show();
        } else {
            auth.createUserWithEmailAndPassword(userEmail, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                if (task.getException().toString().equals(ERROR_FORMAT)) {
                                    //Log.d(TAG, "createUserWithEmail:onComplete:" + task.getException());
                                    Toast.makeText(getBaseContext(), R.string.register_toast_bad_format_email,
                                            Toast.LENGTH_SHORT).show();
                                } else if (task.getException().toString().equals(ERROR_MAIL_EXISTENTE)) {
                                    //Log.d(TAG, "createUserWithEmail:onComplete:" + task.getException());
                                    Toast.makeText(getBaseContext(), R.string.register_toast_email_in_use,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    //Log.d(TAG, "createUserWithEmail:onComplete:" + task.getException());
                                    Toast.makeText(getBaseContext(), R.string.auth_failed,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // actualiza y guarda el nick/nombre que ha escrito el usuario en su perfil de Firebase
                                FirebaseUser user = auth.getCurrentUser();
                                UserProfileChangeRequest displayNameUpdate = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(displayName)
                                        .build();

                                user.updateProfile(displayNameUpdate);
                                //FirebaseAuth.getInstance().signOut();
                                auth.signInWithEmailAndPassword(userEmail, password);
                                saveUserOnDataBase(user,displayName,password,getBaseContext());

                                Toast.makeText(Register.this, "User created", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public static void saveUserOnDataBase(final FirebaseUser user, String displayName, String password, final Context context
    ) {
        Map<String, Object> childUpdates = new HashMap<>();
        User u = new User(user,password,displayName);
        Map<String, Object> userMap = u.toMap();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        childUpdates.put("/user/" + user.getUid() ,  userMap);
        mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(context, context.getResources().getText(R.string.new_user_created), Toast.LENGTH_SHORT).show();

            }
        });
    }


    //asigna el listener a la instancia de auth
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    // quita el listener de la instancia
    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null){
            auth.removeAuthStateListener(authStateListener);
        }
    }

}
