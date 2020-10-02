package com.example.haltution.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


import com.example.haltution.R;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginPage extends Activity {
    private EditText inputEmail;
    private EditText PassWord;
    private FirebaseAuth mAuth;
    private ProgressDialog progressBar;
    private Button submit;
    private CheckBox showPassword;
    private TextView createNew;
    private TextView forgetPw;

    private SignInButton signInGoogle;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG = "LoginPage";
    private Button signOutGoogle;
    private int RC_SIGN_IN = 1001;

    private CallbackManager mCallbackManager;
    private TextView textViewUser;
    private LoginButton signInFacebook;
    private static final String TAG_1 = "FacebookAuthentication";
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputEmail = findViewById(R.id.login_Email);
        submit = findViewById(R.id.login_loginButton);
        progressBar = new ProgressDialog(this);
        showPassword = findViewById(R.id.login_showPassWord);
        PassWord = findViewById(R.id.login_Password);
        createNew = findViewById(R.id.login_createNew);
        forgetPw = findViewById(R.id.login_forgetPassword);

        signInGoogle = findViewById(R.id.sign_in_google);
        signOutGoogle = findViewById(R.id.sign_out_google);


        mAuth = FirebaseAuth.getInstance();


        //Show Password if checkbox is true
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PassWord.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else if (!isChecked) {
                    PassWord.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }


        });


        //forget passWord Page
        forgetPw.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ForgetPassWord.class);
                startActivity(i);
            }
        });

        //signUp Page
        createNew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), signUp.class);
                startActivity(i);
            }
        });


        //login button onClick action
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = PassWord.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setMessage("Login..");
                progressBar.show();

                //authenticate user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.hide();
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        PassWord.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginPage.this, "Authentication failed Check Email And PassWord", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    progressBar.hide();
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent i = new Intent(getApplicationContext(), afterlogin.class);
                                                        startActivity(i);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(LoginPage.this, "First Verified your Email Account then Try Again", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        signOutGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleSignInClient.signOut();
                Toast.makeText(LoginPage.this, "You are logged out", Toast.LENGTH_SHORT).show();
            }
        });

        FacebookSdk.sdkInitialize(getApplicationContext());

        textViewUser = findViewById(R.id.text_user);
        signInFacebook = findViewById(R.id.sign_in_facebook);
        mCallbackManager = CallbackManager.Factory.create();
        signInFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG_1, "onSuccess" + loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG_1, "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG_1, "onSuccess" + error);
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    updateUI(user);
                }
                else {
                    updateUI(null);
                }
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken==null){
                    mAuth.signOut();
                }
            }
        };


    }


    public void  handleFacebookToken(AccessToken token){
        Log.d(TAG_1, "handleFacebookToken" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG_1, "sign in with credential: successful");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUIforF(user);
                }else{
                    Log.d(TAG_1, "sign in with credential: failure", task.getException());
                    Toast.makeText(LoginPage.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
                    updateUIforF(null);

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        mCallbackManager.onActivityResult(requestCode, resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void updateUIforF(FirebaseUser user){
        if(user != null){
            textViewUser.setText(user.getDisplayName());

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!=null){
            mAuth.removeAuthStateListener((authStateListener));
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }




    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(LoginPage.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(account);
        } catch (ApiException e) {
            Toast.makeText(LoginPage.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginPage.this, "Successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    Toast.makeText(LoginPage.this, "Failed", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {
            String username = account.getDisplayName();
            String userEmail = account.getEmail();
            Uri userphoto = account.getPhotoUrl();
            Toast.makeText(LoginPage.this, username + "  " + userEmail, Toast.LENGTH_SHORT).show();
        }
    }


}