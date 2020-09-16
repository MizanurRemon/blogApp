package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity implements View.OnClickListener {
    EditText lmail, lpassword;
    Button loginbutton;
    TextView registergo;

    FirebaseAuth mauth;
    DatabaseReference datareferlog;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lmail = (EditText) findViewById(R.id.loginmailID);
        lpassword = (EditText) findViewById(R.id.loginpasswordID);
        loginbutton = (Button) findViewById(R.id.loginButtonID);
        registergo = (TextView) findViewById(R.id.registerclickID);

        mauth = FirebaseAuth.getInstance();

        datareferlog = FirebaseDatabase.getInstance().getReference().child("Users");
        datareferlog.keepSynced(true);

        progressDialog = new ProgressDialog(this);

        loginbutton.setOnClickListener(this);
        registergo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.registerclickID){
            startActivity(new Intent(Login.this, register.class));
        }
        else if(view.getId()==R.id.loginButtonID){
            checklogin();
        }
    }

    private void checklogin() {
        String loginmail = lmail.getText().toString().trim();
        String loginpassword = lpassword.getText().toString().trim();

        if (!TextUtils.isEmpty(loginmail) && !TextUtils.isEmpty(loginpassword)){
            progressDialog.setMessage("Logging In");
            progressDialog.show();
            mauth.signInWithEmailAndPassword(loginmail, loginpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        userExistenceCheck();

                    }

                }
            });
        }else {
            Toast toast = Toast.makeText(Login.this, "Empty Field", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0 ,0);
            toast.show();
        }
    }

    private void userExistenceCheck() {
        final String userID = mauth.getCurrentUser().getUid();

        datareferlog.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(userID)){
                    progressDialog.dismiss();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    Intent setupIntent = new Intent(Login.this, SetupActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}