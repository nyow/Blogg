package com.example.vader.blogapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.Guideline;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vader.blogapp.R;
import com.example.vader.blogapp.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_photo)
    ImageView loginPhoto;
    @BindView(R.id.guideline)
    Guideline guideline;
    @BindView(R.id.login_mail)
    EditText loginMail;
    @BindView(R.id.login_password)
    EditText loginPassword;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.login_progress)
    ProgressBar loginProgress;
    FirebaseAuth mAuth;
    @BindView(R.id.opensignup)
    TextView opensignup;
    private Intent HomeActivityl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        HomeActivityl = new Intent(this, HomeActivity.class);
        loginProgress.setVisibility(View.INVISIBLE);
//        loginPhoto.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Intent intent=new Intent(getApplicationContext(), HomeActivity.class);
//                startActivity(intent);
//                return false;
//            }
//        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgress.setVisibility(View.VISIBLE);
                button.setVisibility(View.INVISIBLE);

                final String mail = loginMail.getText().toString();
                final String password = loginPassword.getText().toString();

                if (mail.isEmpty() || password.isEmpty())
                    Toast.makeText(LoginActivity.this, "Please Verify all the Fields", Toast.LENGTH_SHORT).show();
                else
                    signIn(mail, password);


            }
        });
    }

    private void signIn(String mail, String password) {

        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    loginProgress.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.VISIBLE);
                    UpdateUI();
                } else {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void UpdateUI() {
        startActivity(HomeActivityl);
        finish();
    }

    @OnClick(R.id.opensignup)
    public void onViewClicked() {
        Intent i=new Intent(this, RegisterActivity.class);
        startActivity(i);
    }
}
