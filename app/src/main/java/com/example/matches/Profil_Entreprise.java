package com.example.matches;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.annotation.Nullable;

public class Profil_Entreprise extends AppCompatActivity {
    TextView Nom, tel, mail, adresse;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userId;
    FirebaseStorage storage;
    ImageView profilepic;
    Button modif, retour;
    String img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil__entreprise);

        Nom = (TextView) findViewById(R.id.textView);
        tel = (TextView) findViewById(R.id.textView14);
        mail = (TextView) findViewById(R.id.textView13);
        adresse = (TextView) findViewById(R.id.textView4);
        profilepic = (ImageView) findViewById(R.id.profilepic);
        modif = (Button) findViewById(R.id.modif);
        retour = (Button) findViewById(R.id.retour);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        userId = firebaseAuth.getCurrentUser().getUid();
        Toast.makeText(getApplicationContext(), "image loading", Toast.LENGTH_LONG).show();

        DocumentReference documentReference = firestore.collection("entreprise").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                // if (documentSnapshot == null) throw new AssertionError();
                Nom.setText(documentSnapshot.getString("nom_entreprise"));
                tel.setText(documentSnapshot.getString("telephone_entreprise"));
                mail.setText(documentSnapshot.getString("email_entreprise"));
                adresse.setText(documentSnapshot.getString("adresse_entreprise"));
                img = documentSnapshot.getString("image_entreprise");

                downLoadWithBytes();
            }
        });

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MatchActivity.class);
                intent.putExtra("typeUser", "entreprise");
                startActivity(intent);
            }
        });

        modif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ModificationProfilEnterprise.class);
                startActivity(intent);
            }
        });
    }

    public void downLoadWithBytes() {
        // Create a storage reference from our app
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images").child("" + img);
        storageRef.getBytes(1920 * 1080 * 5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilepic.setImageBitmap(bitmap);

            }
        });


    }
}