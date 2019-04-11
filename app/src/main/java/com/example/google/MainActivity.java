package com.example.google;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    static final int GOOGLE_SIGN = 123;
    FirebaseAuth mAuth;
    Button btn_login, btn_logout;
    TextView text;
    ImageView image;
    ProgressBar progressBar;
    GoogleSignInClient mGoogleSignInClient;

    private final String web_client_id = "866341742744-ifoj37anocr0uec5kcjmui4tr3hepevp.apps.googleusercontent.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_login = findViewById(R.id.login);
        btn_logout = findViewById(R.id.logout);
        text = findViewById(R.id.text);
        image = findViewById(R.id.image);
        progressBar = findViewById(R.id.progress_circular);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder()
              .requestIdToken(web_client_id)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

//        btn_logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Logout();
//            }
//        });
//
//        btn_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SignInGoogle();
//            }
//        });

        btn_login.setOnClickListener(v -> SignInGoogle());
        btn_logout.setOnClickListener(v ->Logout());

        if (mAuth.getCurrentUser() !=null){
            FirebaseUser user = mAuth.getCurrentUser();
            updateUI(user);
        }
        FirebaseAuth.getInstance().signOut();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    void SignInGoogle(){
        progressBar.setVisibility(View.VISIBLE);
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent,GOOGLE_SIGN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN){
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null)
                    firebaseAuthWithGoogle(account);

            }catch (ApiException e){
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        Log.d("TAG","firebaseAuthWithGoogle : " + account.getId());

        AuthCredential credential = GoogleAuthProvider
                .getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task->{
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.d("TAG", "Successful sign in");
                    Toast.makeText(this,"Signed in",Toast.LENGTH_SHORT).show();

                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);//Todo: remove if has crashes
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.d("TAG", "Unsuccessful sign in", task.getException());
                    Toast.makeText(this,"Failed to sign in 0_0",Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
                });
    }

    private void updateUI(FirebaseUser user) {

        if (user != null){
            String name = user.getDisplayName();
            String email = user.getEmail();
            String phone = user.getPhoneNumber();
            String photo = String.valueOf(user.getPhotoUrl());

            /*Picasso.get().load(photo).into(image);
            text.append(name  + "\n");
            text.append("Info : " + "\n");
            text.append(email + "\n");
            text.append(phone + "\n");
            btn_logout.setVisibility(View.VISIBLE);
            btn_login.setVisibility(View.INVISIBLE);*/




            String[] userInfo = {name,
                                email,
                                phone,
                                photo};

            Intent intent = new Intent(this, LoggedIn.class);
            intent.putExtra("userInfo",userInfo);
            startActivity(intent);

        }
        else {

            text.setText(getString(R.string.firebase_login));
            Picasso.get().load(R.drawable.ic_firebase_logo).into(image);
            btn_logout.setVisibility(View.INVISIBLE);
            btn_login.setVisibility(View.VISIBLE);

//            Intent intent = new Intent(getApplicationContext(),LoggedIn.class);
//            startActivity(intent);
        }

    }

    void Logout(){
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> {
            updateUI(null);
                });
    }
}
