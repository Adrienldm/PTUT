package com.example.matches;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

/**
 * Intent ModificationProfilEnterprise permettant la modification du profil de l'entreprise
 */
public class ModificationProfilEnterprise extends AppCompatActivity {

    final String randomkey = UUID.randomUUID().toString();
    public Uri imageUri;
    TextView Nom, tel, adresse;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userId;
    FirebaseStorage storage;
    ImageView profilepic;
    String img;
    private StorageReference storageReference;
    private Button modififer;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modification_profil_enterprise);

        Nom = (TextView) findViewById(R.id.nomE);
        tel = (TextView) findViewById(R.id.telE);

        adresse = (TextView) findViewById(R.id.adresseE);
        profilepic = (ImageView) findViewById(R.id.image1);
        modififer = (Button) findViewById(R.id.modif);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        userId = firebaseAuth.getCurrentUser().getUid();
        Toast.makeText(getApplicationContext(), "loading image", Toast.LENGTH_LONG).show();

        initAutocompletion();

        profilepic.setOnClickListener(new View.OnClickListener() {
            /**
             * lors de l'appuis sur l'image ouverture de choosePicture();
             *
             * @param view
             */
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });


        final DocumentReference documentReference = firestore.collection("entreprise").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            /**
             * recuperation de attrubut de l'utilisateur dans la base données
             *
             * @param documentSnapshot
             * @param e
             */
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                // if (documentSnapshot == null) throw new AssertionError();
                Nom.setText(documentSnapshot.getString("nom_entreprise"));
                tel.setText(documentSnapshot.getString("telephone_entreprise"));
                adresse.setText(documentSnapshot.getString("adresse_entreprise"));
                img = documentSnapshot.getString("image_entreprise");
                downLoadWithBytes();
            }
        });

        modififer.setOnClickListener(new View.OnClickListener() {
            /**
             * Modification des données dans la base de donées et appel de la fonction retour(); en vérifiant que les champ ne sont pas vides
             *
             * @param view
             */
            @Override
            public void onClick(View view) {
                final DocumentReference documentReference2 = firestore.collection("entreprise").document(userId);

                final String nom2 = Nom.getText().toString().trim();
                final String tel2 = tel.getText().toString().trim();
                final String adres = adresse.getText().toString().trim();

                if (TextUtils.isEmpty(nom2)) {
                    Nom.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(tel2)) {
                    tel.setError("Phone is required");
                    return;
                }
                if (TextUtils.isEmpty(adres)) {
                    adresse.setError("Adress is required");
                    return;
                }
                documentReference2.update(
                        "nom_entreprise", Nom.getText().toString().trim(),
                        "telephone_entreprise", tel.getText().toString().trim(),

                        "adresse_entreprise", adresse.getText().toString().trim(),
                        "image_entreprise", img
                );
                retour();
            }


        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            adresse.setText(place.getAddress());
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        } else if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profilepic.setImageURI(imageUri);
            uploadPicture();
        }

    }

    /**
     * ouverture du dossier du telephone pour choisir une image
     */
    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    /**
     * telegargement de l'image dans la base de données
     */
    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.setCancelable(false);
        pd.show();


        StorageReference riversRef = storageReference.child("images/" + randomkey);
        img = randomkey;

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded.", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progressPercent = (100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        pd.setMessage("Percentage " + (int) progressPercent + "%");
                    }
                });
    }


    /**
     * initialisation de l'autocomplexion pour l'adresse de l'utilisateur
     */
    protected void initAutocompletion() {
        // autocompletion
        Places.initialize(getApplicationContext(), "AIzaSyDA6Tx1FjGwf_joDz7L12GyKi1nK8NC21s");
        adresse.setFocusable(false);
        adresse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                        Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(getApplicationContext());
                startActivityForResult(intent, 100);
            }
        });

    }


    /**
     * telegargement de l'image a partir de la base donnée
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

    /**
     * fermeture de l'Intent
     */
    private void retour() {
        finish();
    }

}