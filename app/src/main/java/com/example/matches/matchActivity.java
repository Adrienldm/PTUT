package com.example.matches;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class matchActivity extends AppCompatActivity {

    public ImageView profilImageView;
    public TextView nomEntrepriseTextView;
    public TextView dernierMessageTextView;
    public TextView dateTextView;
    private DatabaseReference firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchs);
        profilImageView = (ImageView) findViewById(R.id.profilImageView);
        nomEntrepriseTextView = (TextView) findViewById(R.id.nomEntrepriseTextView);
        dernierMessageTextView = (TextView) findViewById(R.id.dernierMessageTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);


        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        nomEntrepriseTextView.setText(firebaseDatabase.getKey());

        ValueEventListener postListner = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e("???", String.valueOf(dataSnapshot));

                nomEntrepriseTextView.setText(firebaseDatabase.getKey());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("???", String.valueOf(databaseError));
            }
        };

    }
}
