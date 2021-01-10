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
    TextView Nom, tel, mail, adresse, titreText, date, descriptionText, competenceText;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userId;
    FirebaseStorage storage;
    ImageView profilepic;
    Button modif, retour;
    String img, id, titre, dateDebut, dateFin, description, competences;


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
        titreText = findViewById(R.id.titre);
        date = findViewById(R.id.date);
        descriptionText = findViewById(R.id.description);
        competenceText = findViewById(R.id.competence);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        userId = firebaseAuth.getCurrentUser().getUid();
        Toast.makeText(getApplicationContext(), "image loading", Toast.LENGTH_LONG).show();

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            id = null;
        } else {
            id = extras.getString("id");
            titre = extras.getString("nomstage");
            dateDebut = extras.getString("datedebut");
            dateFin = extras.getString("datefin");
            description = extras.getString("description");
            competences = extras.getString("competence");
        }

        if (!userId.equals(id)) {
            modif.setVisibility(View.INVISIBLE);
            titreText.setText(titre);
            date.setText("Stage du " + dateDebut + " au " + dateFin);
            descriptionText.setText("Desciption du stage: \n" + description);
            competenceText.setText("Competences requise: \n" + competences);
        }


        DocumentReference documentReference = firestore.collection("entreprise").document(id);
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
                finish();
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