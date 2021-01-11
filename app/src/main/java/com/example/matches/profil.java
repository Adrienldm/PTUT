package com.example.matches;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import java.text.MessageFormat;

import javax.annotation.Nullable;

/**
 * Intent permettant l'affichage du profil de l'étudiant
 */

public class profil extends AppCompatActivity {
    TextView NomAge, tel, mail, adresse, description, departement;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userId, depar;
    FirebaseStorage storage;
    ImageView profilepic, cv, motiv;
    Button modif, retour;
    String img, cvNom, motinNom, id;

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
        modif = (Button) findViewById(R.id.modif);
        motiv = (ImageView) findViewById(R.id.motiv);
        cv = (ImageView) findViewById(R.id.cv);
        retour = (Button) findViewById(R.id.retour);
        departement = (TextView) findViewById(R.id.dep);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        userId = firebaseAuth.getCurrentUser().getUid();
        Toast.makeText(getApplicationContext(), "image loading", Toast.LENGTH_LONG).show();

        /**
         * recuperation de l'id de l'utilisateur envoyer
         */
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            id = null;
        } else {
            id = extras.getString("id");
        }

        if (!userId.equals(id)) {
            modif.setVisibility(View.INVISIBLE);
        }


        DocumentReference documentReference = firestore.collection("etudiant").document(id);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            /**
             * recuperation des donnée dans la base de donner et affichage
             *
             * @param documentSnapshot
             * @param e
             */
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                // if (documentSnapshot == null) throw new AssertionError();
                NomAge.setText(MessageFormat.format("{0} {1}, {2}", documentSnapshot.getString("nom_etudiant"), documentSnapshot.getString("prenom_etudiant"), documentSnapshot.getString("age_etudiant")));
                tel.setText(documentSnapshot.getString("telephone_etudiant"));
                mail.setText(documentSnapshot.getString("email_etudiant"));
                adresse.setText(documentSnapshot.getString("adresse_etudiant"));
                description.setText(documentSnapshot.getString("description_etudiant"));
                img = documentSnapshot.getString("image_etudiant");
                cvNom = documentSnapshot.getString("cv_etudiant");
                motinNom = documentSnapshot.getString("motiv_etudiant");
                depar = documentSnapshot.getString("departement_etudiant");
                switch (depar) {
                    case "info":
                        departement.setText("IUT informatique de laval");
                        break;
                    case "gb":
                        departement.setText("IUT génie biologique de Laval");
                        break;
                    case "mmi":
                        departement.setText("IUT Métier du multimédia et de l'informatique de laval");
                        break;
                    case "tc":
                        departement.setText("IUT Technique de commercialisation de Laval");
                        break;
                    default:
                        departement.setText("erreur bug doit être corriger ");
                }
                downLoadWithBytes();
            }
        });

        /**
         * appel de la fonction launchProfil
         */
        modif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lauchModif();
            }
        });

        /**
         * ouverture du pdf
         */
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setData(Uri.parse(cvNom));
                startActivity(intent);
            }
        });

        /**
         * ouverture du pdf
         */
        motiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(motinNom));
                startActivity(intent);
            }
        });

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * ouverture de l'intent permettant la modification du profil
     */
    public void lauchModif() {
        Intent intent = new Intent(this, ModificationProfilEtudiant.class);
        startActivity(intent);
    }

    /**
     * telegargement de l'image de profil
     */
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