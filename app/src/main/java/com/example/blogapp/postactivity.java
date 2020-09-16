package com.example.blogapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class postactivity extends AppCompatActivity implements View.OnClickListener {
    Button uploadbutton;
    ImageButton backbutton, imageselect;
    EditText postedit, descriptionedit;

    Uri imageuri = null;
    StorageReference store_refer;
    DatabaseReference data_refer;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postactivity);

        imageselect = (ImageButton) findViewById(R.id.imageselectID);
        postedit = (EditText) findViewById(R.id.postID);
        descriptionedit = (EditText) findViewById(R.id.descriptionID);
        uploadbutton = (Button) findViewById(R.id.uploadbuttonID);
        backbutton = (ImageButton) findViewById(R.id.backbuttonID);

        imageselect.setOnClickListener(this);
        backbutton.setOnClickListener(this);
        uploadbutton.setOnClickListener(this);

        store_refer = FirebaseStorage.getInstance().getReference();
        data_refer = FirebaseDatabase.getInstance().getReference().child("Data");
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backbuttonID) {

            finish();

        } else if (view.getId() == R.id.imageselectID) {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);

        } else if (view.getId() == R.id.uploadbuttonID) {

            startposting();

        }
    }

    private void startposting() {

        final String post_title = postedit.getText().toString();
        final String descript = descriptionedit.getText().toString();

        if (!TextUtils.isEmpty(post_title) && !TextUtils.isEmpty(descript) && imageuri != null) {
            progressDialog.setMessage("Uploading");
            progressDialog.show();

            StorageReference filepath = store_refer.child("Blog Images").child(imageuri.getLastPathSegment());
            filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> urltask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urltask.isSuccessful()) ;
                    Uri downloadurl = urltask.getResult();
                    DatabaseReference newData_refer = data_refer.push();
                    newData_refer.child("Title").setValue(post_title);
                    newData_refer.child("Description").setValue(descript);
                    newData_refer.child("ImageURL").setValue(downloadurl.toString());

                    progressDialog.dismiss();
                    Toast toast = Toast.makeText(postactivity.this, "Uploaded", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    startActivity(new Intent(postactivity.this, MainActivity.class));
                    finish();

                }
            });
        } else {
            Toast toast = Toast.makeText(this, "Error Found", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {

            imageuri = data.getData();
            imageselect.setImageURI(imageuri);

        }
    }
}