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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity implements View.OnClickListener {
    ImageButton backbutton;
    Button registerButton;
    EditText nameedit, mailedit, passwordedit;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    DatabaseReference datarefer_reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = (Button) findViewById(R.id.registerID);
        nameedit = (EditText) findViewById(R.id.nameregID);
        mailedit = (EditText) findViewById(R.id.mailregID);
        passwordedit = (EditText) findViewById(R.id.passwordregID);

        progressDialog = new ProgressDialog(this);
        datarefer_reg = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.registerID) {
            startregister();

        }
    }

    private void startregister() {
        final String mname = nameedit.getText().toString().trim();
        String mmail = mailedit.getText().toString().trim();
        String mpassword = passwordedit.getText().toString().trim();

        if (!TextUtils.isEmpty(mname) && !TextUtils.isEmpty(mmail) && !TextUtils.isEmpty(mpassword)) {
            progressDialog.setMessage("Uploading");
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(mmail, mpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String userID = mAuth.getCurrentUser().getUid();
                        DatabaseReference new_data_refer = datarefer_reg.child(userID);
                        new_data_refer.child("name").setValue(mname);
                        new_data_refer.child("image").setValue("default");

                        progressDialog.dismiss();

                        Toast toast = Toast.makeText(register.this, "Registered", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        Intent intent = new Intent(register.this, Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            });
        } else {
            Toast toast = Toast.makeText(register.this, "Empty Field", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}