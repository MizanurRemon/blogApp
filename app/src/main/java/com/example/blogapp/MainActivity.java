package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton addbutton;
    RecyclerView blogList;
    Button logout;
    private DatabaseReference databaseReference;
    FirebaseAuth blogauth;
    FirebaseAuth.AuthStateListener blogauthlistener;
    ProgressDialog progressDialog;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addbutton = (ImageButton) findViewById(R.id.addbuttonID);
        blogList = (RecyclerView) findViewById(R.id.recyclerviewID);
        logout = (Button) findViewById(R.id.logoutID);
        query = FirebaseDatabase.getInstance().getReference().child("Data");

        blogList.setHasFixedSize(true);
        blogList.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);

        blogauth = FirebaseAuth.getInstance();
        blogauthlistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };

        progressDialog = new ProgressDialog(this);

        addbutton.setOnClickListener(this);
        logout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addbuttonID) {
            startActivity(new Intent(MainActivity.this, postactivity.class));
        } else if (view.getId() == R.id.logoutID) {
            blogauth.signOut();
            startActivity(new Intent(MainActivity.this, Login.class));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        //userExistenceCheck();

        blogauth.addAuthStateListener(blogauthlistener);

        FirebaseRecyclerOptions<blog> options = new FirebaseRecyclerOptions.Builder<blog>().setQuery(query, blog.class).build();
        FirebaseRecyclerAdapter<blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<blog, BlogViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BlogViewHolder holder, int position, @NonNull blog model) {
                holder.setTitle(model.getTitle());
                holder.setDesc(model.getDescription());
                holder.setImage(getApplicationContext(), model.getImageURL());
            }

            @NonNull
            @Override
            public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_design_recyclerview, parent, false);
                return new BlogViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        blogList.setAdapter(firebaseRecyclerAdapter);
    }

    private void userExistenceCheck() {
        final String userID = blogauth.getCurrentUser().getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(userID)) {

                    Intent intent = new Intent(MainActivity.this, SetupActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mview;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void setTitle(String Title) {
            TextView post_title = (TextView) mview.findViewById(R.id.post_titleId);
            post_title.setText(Title);
        }

        public void setDesc(String Description) {
            TextView post_desc = (TextView) mview.findViewById(R.id.post_descriptionID);
            post_desc.setText(Description);
        }

        public void setImage(Context contx, String ImageURL) {
            ImageView post_image = (ImageView) mview.findViewById(R.id.imageID);
            Picasso.with(contx).load(ImageURL).into(post_image);
        }
    }




}
