package com.example.matches;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.text.MessageFormat;

import javax.annotation.Nullable;

public class profil extends AppCompatActivity {
    TextView NomAge, tel, mail, adresse, description;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userId;
    FirebaseStorage storage;
    ImageView profilepic;

    String img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        NomAge = (TextView) findViewById(R.id.textView);
        tel = (TextView) findViewById(R.id.textView14);
        mail = (TextView) findViewById(R.id.textView13);
        adresse = (TextView) findViewById(R.id.textView4);
        description = (TextView) findViewById(R.id.textView6);
        profilepic = (ImageView) findViewById(R.id.profilepic);


        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        userId = firebaseAuth.getCurrentUser().getUid();


        DocumentReference documentReference = firestore.collection("etudiant").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                // if (documentSnapshot == null) throw new AssertionError();
                NomAge.setText(MessageFormat.format("{0} {1}, {2}", documentSnapshot.getString("nom_etudiant"), documentSnapshot.getString("prenom_etudiant"), documentSnapshot.getString("age_etudiant")));
                tel.setText(documentSnapshot.getString("telephone_etudiant"));
                mail.setText(documentSnapshot.getString("email_etudiant"));
                adresse.setText(documentSnapshot.getString("adresse_etudiant"));
                description.setText(documentSnapshot.getString("description_etudiant"));
                img = documentSnapshot.getString("image_etudiant");
                downLoadWithBytes();
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