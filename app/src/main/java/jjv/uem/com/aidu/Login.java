package jjv.uem.com.aidu;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // inicializa el listner para la autentificacion
        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser usuarioActual = firebaseAuth.getCurrentUser();
                if(usuarioActual != null){
                    Log.d("LOGEO: ", "OK!");
                }else{
                    Log.d("LOGEO: ", "ERROR!");
                }
            }
        };
    }

    private void login(String user, String pwd){

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

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
