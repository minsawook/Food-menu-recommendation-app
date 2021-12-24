package com.example.a2017261037;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.a2017261037.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    EditText emailEt;
    EditText passwordEt;

    Button emailLoginBtn;
    Button joinBtn;
    Button googleLoginBtn;
    ProgressBar loading_Pb;
    final  int REQ_GOOGLE_SIGNIN = 1234;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = auth.getCurrentUser(); //로그인 정보 저장
        updateUI(user);

        emailEt = findViewById(R.id.email_et);
        passwordEt = findViewById(R.id.password_et);
        emailLoginBtn = findViewById(R.id.email_login_btn);
        joinBtn = findViewById(R.id.join_btn);
        loading_Pb = findViewById(R.id.loading_pb);
        googleLoginBtn = findViewById(R.id.google_login_btn);
        // Configure Google Sign In

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1072232802533-2rqb393mf2l375d7383stqcu7r9lt8hl.apps.googleusercontent.com")
                .requestEmail()
                .build();

        GoogleSignInClient client = GoogleSignIn.getClient(this,gso);

        googleLoginBtn.setOnClickListener(new View.OnClickListener() { //구글로그인
            @Override
            public void onClick(View view) {
                Intent intent = client.getSignInIntent();
                startActivityForResult(intent, REQ_GOOGLE_SIGNIN);
            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,JoinActivity.class);
                startActivity(intent);
            }
        });


        emailLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();
                if(email.equals("") || password.equals("") ){
                    Toast.makeText(getApplicationContext(), "입력되지않음", Toast.LENGTH_SHORT).show();
                }
               else  login(email,password);
            }

            public void login(String email, String password) {
                loading_Pb.setVisibility(View.VISIBLE);
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loading_Pb.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            FirebaseUser user =  auth.getCurrentUser();
                            String email = user.getEmail();
                            Toast.makeText(MainActivity.this,"로그인 완료",Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        }
                        else{
                            String message = task.getException().getMessage();
                            Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_GOOGLE_SIGNIN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {}
        };
    }
    public void  updateUI(FirebaseUser user){
        if(user != null){
            Intent intent = new Intent(MainActivity.this,SecondActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user =auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            updateUI(null);
                        }
                    }
                });
    }
}