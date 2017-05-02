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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


import jjv.uem.com.aidu.R;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String ERROR_PWD_INCORRECTA = "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password.";
    private static final String ERROR_MAIL_NO_EXISTENTE = "com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.";
    private static final String ERROR_USR_INCORRECTO = "com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.";
    private static final String ERROR_FORMATO = "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.";
    private static final int RC_SIGN_IN = 9001;


    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private TextView tvLogo, tvRegister, tvAltLogin;
    private EditText etUserName, etPwd;
    private ImageView ivGoogleSignInButton;
    private Button btnLogin;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setTypeFace();
        initFirebase();
        initGoogleSignIn();
    }

    private void initGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* Fragment Activity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void initViews() {
        etUserName = (EditText) findViewById(R.id.et_username);
        etPwd = (EditText) findViewById(R.id.et_password);
        tvLogo = (TextView) findViewById(R.id.r_tv_logo);
        tvRegister = (TextView) findViewById(R.id.tv_register);
        tvAltLogin = (TextView) findViewById(R.id.tv_signinalt);

        ivGoogleSignInButton = (ImageView) findViewById(R.id.iv_google_signin_button);

        ivGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.iv_google_signin_button:
                        signInWithGoogle();
                        break;
                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
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
        tvAltLogin.setTypeface(bubblerFont);
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

    private void signInWithGoogle(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                            }
                        }
                    });
        }
    }

    private void loginWithGoogleAccount(GoogleSignInAccount googleAccount) {
        // consigue un token que configure una credencial de la cuenta de google para la cuenta firebase
        AuthCredential credential = GoogleAuthProvider.getCredential(googleAccount.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent i = new Intent(Login.this, MainActivity.class);
                            startActivity(i);
                        }else{
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("GOOGLE_L:" , "Status: " + requestCode);
        // Resultado devuelto de GoogleSignInApi.getSignInIntent(...)
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult loginResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            int statusCode = loginResult.getStatus().getStatusCode();

            if(loginResult.isSuccess()){
                // GoogleSignIn satisfactorio, inicial el Login con Firebase
                GoogleSignInAccount googleAccount = loginResult.getSignInAccount();
                loginWithGoogleAccount(googleAccount);
            }
        }
    }

}
