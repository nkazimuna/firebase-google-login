package com.example.google;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class LoggedIn extends AppCompatActivity {
    Button btn_logout;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    private String [] mIntentData ;
    TextView email, name, phone;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        image = findViewById(R.id.image);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        Intent i =  getIntent();
        mIntentData = getIntent().getExtras().getStringArray("userInfo");

        name.setText(mIntentData[0]);
        email.setText(mIntentData[1]);
        phone.setText(mIntentData[2]);

        //Setting up profile image
//        String number = mIntentData[3];
//        Integer result = Integer.valueOf(number);
//        image.setImageResource(result);

        btn_logout = findViewById(R.id.logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
            }
        });
    }

    void Logout(){
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> {
                    Intent intent = new Intent(this,MainActivity.class);
                    startActivity(intent);
                });
    }
}
