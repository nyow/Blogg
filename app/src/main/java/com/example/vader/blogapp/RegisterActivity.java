package com.example.vader.blogapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vader.blogapp.Activities.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.*;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    static int PReqCode=1;
    static int REQUESCODE=1;
    Uri pickedImgUri;


    @BindView(R.id.regName)
    EditText regName;
    @BindView(R.id.textInputLayout)
    TextInputLayout textInputLayout;
    @BindView(R.id.regMail)
    EditText regMail;
    @BindView(R.id.textInputLayout2)
    TextInputLayout textInputLayout2;
    @BindView(R.id.regPass)
    EditText regPass;
    @BindView(R.id.textInputLayout4)
    TextInputLayout textInputLayout4;
    @BindView(R.id.regPass2)
    EditText regPass2;
    @BindView(R.id.textInputLayout3)
    TextInputLayout textInputLayout3;
    @BindView(R.id.regbtn)
    Button regbtn;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.guideline2)
    Guideline guideline2;
    @BindView(R.id.regUserPhoto)
    ImageView regUserPhoto;
    //TextInputLayout inputLayout = (TextInputLayout) findViewById(R.id.textInputLayout);

    private FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        //textInputLayout.setError("First name is required"); // show error
        //inputLayout.setError(null);
        progressBar.setVisibility(View.INVISIBLE);
        mAuth=FirebaseAuth.getInstance();
    }


    @OnClick({R.id.regUserPhoto, R.id.regbtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.regUserPhoto:
                if(Build.VERSION.SDK_INT>=22)
                {
                    checkandrequestforpermission();
                }
                else
                {
                    opengallery();
                }

                break;
            case R.id.regbtn:
                if (regName.getText().toString().isEmpty())
                    textInputLayout.setError("First name is required");
                else
                    textInputLayout.setError(null);
                if (regMail.getText().toString().isEmpty())
                    textInputLayout2.setError("E-Mail is required");
                else
                    textInputLayout2.setError(null);
                if (regPass.getText().toString().isEmpty())
                    textInputLayout4.setError("Password is required");
                else
                    textInputLayout4.setError(null);
                if (regPass2.getText().toString().isEmpty())
                    textInputLayout3.setError("Confirm Password is required");
                else if (!(regPass2.getText().toString().equals(regPass.getText().toString())))
                    textInputLayout3.setError("Password Mismatched");
                else {
                    textInputLayout3.setError(null);
                    regbtn.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    final String username = regName.getText().toString();
                    final String password = regPass.getText().toString();
                    final String password2 = regPass2.getText().toString();
                    final String email = regMail.getText().toString();


                    createUserAcc(email,username,password);



                }







                break;
        }
    }

    private void createUserAcc(String email, final String username, String password) {

//        mAuth.createUserWithEmailAndPassword(email,password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//
//                    }
//                })
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Acc Created", Toast.LENGTH_SHORT).show();
                            UpdateuserInfo(username,pickedImgUri,mAuth.getCurrentUser());
                        }
                        else
                        {
                            //acc creation is failed
                            Toast.makeText(RegisterActivity.this, "Acc creation is Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            regbtn.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }

                });
    }

    private void UpdateuserInfo(final String username, Uri pickedImgUri, final FirebaseUser currentUser) {


        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("user_photo");
        final StorageReference imageFilePath = mStorage.child(Objects.requireNonNull(pickedImgUri.getLastPathSegment()));
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                //image has been uploaded
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //uri contain user image uri

                        UserProfileChangeRequest profileupdate=new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .setPhotoUri(uri)
                                .build();



                        currentUser.updateProfile(profileupdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(RegisterActivity.this, "Registaration is Completed", Toast.LENGTH_SHORT).show();
                                            updateUI();
                                        }
                                    }
                                });

                    }
                });

            }
        });


    }

    private void updateUI() {


        //new home page ham
        Intent homeActivity=new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(homeActivity);



    }

    private void opengallery()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);

    }
    private void checkandrequestforpermission()
    {
        if(ContextCompat.checkSelfPermission(RegisterActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Please Accept for required permission", Toast.LENGTH_SHORT).show();

            }
            else
            {
                ActivityCompat.requestPermissions(RegisterActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},PReqCode);
            }

        }
        else
        {
            opengallery();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((resultCode==RESULT_OK) &&(requestCode==REQUESCODE)&&(data!=null))
        {
            pickedImgUri=data.getData();
            Toast.makeText(this, pickedImgUri.toString(), Toast.LENGTH_SHORT).show();
            regUserPhoto.setImageURI(pickedImgUri);
        }
    }
}
