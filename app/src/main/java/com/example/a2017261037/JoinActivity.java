package com.example.a2017261037;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class JoinActivity<crea> extends AppCompatActivity {
    EditText emailEt;
    EditText passwordEt;
    EditText passwordConfirmEt;

    ProgressBar loadingPb;
    Button joinBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        emailEt = findViewById(R.id.email_et);
        passwordEt = findViewById(R.id.password_et);
        passwordConfirmEt = (findViewById(R.id.password_confirm_et));
        joinBtn = findViewById(R.id.join_btn);
        loadingPb = findViewById(R.id.loading_pb);

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();
                String passwordConfirm = passwordConfirmEt.getText().toString();

                if(email.equals("")){
                    Toast.makeText(JoinActivity.this,"이메일을 입력해 주세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length()<6){
                    Toast.makeText(JoinActivity.this,"비밀번호를 6글자 이상으로 해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                if( !password.equals(passwordConfirm)){
                    Toast.makeText(JoinActivity.this,"앞의 비밀번호와 다릅니다.",Toast.LENGTH_SHORT).show();
                    passwordConfirmEt.setText("");
                    passwordConfirmEt.requestFocus();
                    return;
                }

                login(email,password);
            }

            public void login(String email, String password) {
                loadingPb.setVisibility(View.VISIBLE);
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loadingPb.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            Toast.makeText(JoinActivity.this,"회원가입에 성공함",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            String message = task.getException().getMessage();
                            Toast.makeText(JoinActivity.this,message,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
