package jjv.uem.com.aidu.UI;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import jjv.uem.com.aidu.R;

public class Login extends AppCompatActivity {

    private static final String ERROR_PWD_INCORRECTA = "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password.";
    private static final String ERROR_MAIL_NO_EXISTENTE = "com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.";
    private static final String ERROR_USR_INCORRECTO = "com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.";
    private static final String ERROR_FORMATO = "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.";

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private TextView tvLogo, tvRegister;
    private EditText etUserName, etPwd;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setTypeFace();
        initFirebase();
    }

    private void initViews() {
        etUserName = (EditText) findViewById(R.id.et_username);
        etPwd = (EditText) findViewById(R.id.et_password);
        tvLogo = (TextView) findViewById(R.id.r_tv_logo);
        tvRegister = (TextView) findViewById(R.id.tv_register);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
            }
        });

        btnLogin = (Button) findViewById(R.id.btn_login);
    }

    private void setTypeFace() {
        Typeface bubblerFont = Typeface.createFromAsset(getAssets(), "fonts/BubblerOne-Regular.ttf");
        tvLogo.setTypeface(bubblerFont);
        tvRegister.setTypeface(bubblerFont);
        etUserName.setTypeface(bubblerFont);
        etPwd.setTypeface(bubblerFont);
        btnLogin.setTypeface(bubblerFont);
    }

    private void initFirebase() {
        // inicializa el listner para la autentificacion
        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser usuarioActual = firebaseAuth.getCurrentUser();
                if(usuarioActual != null){
                    Log.d("LOGEO: ", "OK! -> " + usuarioActual.getEmail());
                    finish();
                }else{
                    Log.d("LOGEO: ", "ERROR!");
                }
            }
        };
    }

    public void login(View v){
        String user = etUserName.getText().toString(), pwd = etPwd.getText().toString();
        if(user.equals("") || pwd.equals("")){
            Toast.makeText(this, getText(R.string.login_toast_creden), Toast.LENGTH_SHORT).show();
        } else {
            auth.signInWithEmailAndPassword(user, pwd)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("LOGIN-COMPLETE: ", "" + task.isSuccessful());

                            if (!task.isSuccessful()) {
                                if (task.getException().toString().equals(ERROR_USR_INCORRECTO)){
                                    //Log.d(TAG, "createUserWithEmail:onComplete:" + task.getException());
                                    Toast.makeText(getBaseContext(), R.string.incorrect_email,
                                            Toast.LENGTH_SHORT).show();
                                }else if (task.getException().toString().equals(ERROR_MAIL_NO_EXISTENTE)){
                                    //Log.d(TAG, "createUserWithEmail:onComplete:" + task.getException());
                                    Toast.makeText(getBaseContext(), R.string.incorrect_email,
                                            Toast.LENGTH_SHORT).show();
                                } else if (task.getException().toString().equals(ERROR_PWD_INCORRECTA)){
                                    //Log.d(TAG, "createUserWithEmail:onComplete:" + task.getException());
                                    Toast.makeText(getBaseContext(), R.string.incorrect_pwd,
                                            Toast.LENGTH_SHORT).show();
                                }else if (task.getException().toString().equals(ERROR_FORMATO)){
                                    //Log.d(TAG, "createUserWithEmail:onComplete:" + task.getException());
                                    Toast.makeText(getBaseContext(), R.string.wrong_mail_format,
                                            Toast.LENGTH_SHORT).show();
                                }else{
                                    //Log.d(TAG, "createUserWithEmail:onComplete:" + task.getException());
                                    Toast.makeText(getBaseContext(), R.string.auth_failed,
                                            Toast.LENGTH_SHORT).show();
                                }
                                // si all es correcto inicia MainActivity
                            }else{
                                Intent i = new Intent(Login.this, MainActivity.class);
                                startActivity(i);
                            }
                        }
                    });
        }
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
