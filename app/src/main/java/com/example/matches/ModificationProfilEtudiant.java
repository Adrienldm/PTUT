package com.example.matches;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
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

public class ModificationProfilEtudiant extends AppCompatActivity {
    final String randomkey = UUID.randomUUID().toString();
    public Uri imageUri;
    TextView Nom, Prenom, Age, tel, mail, adresse, description;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userId, departement;
    FirebaseStorage storage;
    ImageView profilepic;
    String img;
    private StorageReference storageReference;
    private Button modififer;
    private RadioButton info, gb, tc, mmi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modification_profil_etudiant);

        Nom = (TextView) findViewById(R.id.nom);
        Prenom = (TextView) findViewById(R.id.prenomE);
        Age = (TextView) findViewById(R.id.age);
        tel = (TextView) findViewById(R.id.tel);
        mail = (TextView) findViewById(R.id.mail);
        adresse = (TextView) findViewById(R.id.adresse);
        description = (TextView) findViewById(R.id.desc);
        profilepic = (ImageView) findViewById(R.id.image1);
        modififer = (Button) findViewById(R.id.modifier);
        info = (RadioButton) findViewById(R.id.info);
        gb = (RadioButton) findViewById(R.id.gb);
        tc = (RadioButton) findViewById(R.id.tc);
        mmi = (RadioButton) findViewById(R.id.mmi);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                departement = "info";
            }
        });
        tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                departement = "tc";
            }
        });
        mmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                departement = "mmi";
            }
        });
        gb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                departement = "gb";
            }
        });

        initAutocompletion();

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        userId = firebaseAuth.getCurrentUser().getUid();
        Toast.makeText(getApplicationContext(), "loading image", Toast.LENGTH_LONG).show();


        final DocumentReference documentReference = firestore.collection("etudiant").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                // if (documentSnapshot == null) throw new AssertionError();
                Nom.setText(documentSnapshot.getString("nom_etudiant"));
                tel.setText(documentSnapshot.getString("telephone_etudiant"));
                Prenom.setText(documentSnapshot.getString("prenom_etudiant"));
                Age.setText(documentSnapshot.getString("age_etudiant"));
                mail.setText(documentSnapshot.getString("email_etudiant"));
                adresse.setText(documentSnapshot.getString("adresse_etudiant"));
                description.setText(documentSnapshot.getString("description_etudiant"));
                img = documentSnapshot.getString("image_etudiant");
                departement = documentSnapshot.getString("departement_etudiant");
                downLoadWithBytes();
            }
        });
        modififer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DocumentReference documentReference2 = firestore.collection("etudiant").document(userId);
                documentReference2.update(
                        "nom_etudiant", Nom.getText().toString().trim(),
                        "telephone_etudiant", tel.getText().toString().trim(),
                        "prenom_etudiant", Prenom.getText().toString().trim(),
                        "age_etudiant", Age.getText().toString().trim(),
                        "email_etudiant", mail.getText().toString().trim(),
                        "adresse_etudiant", adresse.getText().toString().trim(),
                        "description_etudiant", description.getText().toString().trim(),
                        "image_etudiant", randomkey,
                        "departement_etudiant", departement
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


    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }


    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.setCancelable(false);
        pd.show();


        StorageReference riversRef = storageReference.child("images/" + randomkey);

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

    private void retour() {
        Intent intent = new Intent(this, profil.class);
        startActivity(intent);
    }
}