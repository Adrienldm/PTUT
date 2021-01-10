package com.example.matches;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class ModificationProfilEtudiant extends AppCompatActivity implements OnMapReadyCallback{
    final String randomkey = UUID.randomUUID().toString();
    public Uri imageUri;
    TextView Nom, Prenom, Age, tel, mail, adresse, description;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userId, departement;
    FirebaseStorage storage;
    ImageView profilepic;
    String img;
    LatLng myPosition = new LatLng(48.0667, -0.7667);;
    private SeekBar distanceSeekBar;
    private TextView distanceTextView;
    private MapView mapView;
    private GoogleMap gMap;
    Circle circle;
    final String randomPDFkey = UUID.randomUUID().toString();
    private StorageReference storageReference;
    final String randomPDFkey2 = UUID.randomUUID().toString();
    private Button modififer;
    private RadioButton info, gb, tc, mmi;
    Uri cvUrl, motivUrl;
    private int i = 0;
    private DatabaseReference databaseReference;
    private Button cvButton, motivButton;
    private String cvNom, motivNom;


    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

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
        distanceSeekBar = (SeekBar) findViewById(R.id.distanceSeekBar);
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);
        profilepic = (ImageView) findViewById(R.id.image1);
        modififer = (Button) findViewById(R.id.modifier);
        info = (RadioButton) findViewById(R.id.info);
        gb = (RadioButton) findViewById(R.id.gb);
        tc = (RadioButton) findViewById(R.id.tc);
        mmi = (RadioButton) findViewById(R.id.mmi);
        cvButton = (Button) findViewById(R.id.cvButton);
        motivButton = (Button) findViewById(R.id.motivButton);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

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
                cvNom = documentSnapshot.getString("cv_etudiant");
                motivNom = documentSnapshot.getString("motiv_etudiant");
                downLoadWithBytes();
                cvUrl = Uri.parse(cvNom);
                motivUrl = Uri.parse(motivNom);
            }
        });

        modififer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DocumentReference documentReference2 = firestore.collection("etudiant").document(userId);

                final String email = mail.getText().toString().trim();
                final String nom2 = Nom.getText().toString().trim();
                final String prenom2 = Prenom.getText().toString().trim();
                final String tel2 = tel.getText().toString().trim();
                final String adres = adresse.getText().toString().trim();
                final String age2 = Age.getText().toString().trim();
                final String description2 = description.getText().toString().trim();

                if (TextUtils.isEmpty(prenom2)) {
                    Prenom.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(nom2)) {
                    Nom.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    mail.setError("Email is required");
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
                if (TextUtils.isEmpty(age2)) {
                    Age.setError("Age is required");
                    return;
                }
                if (TextUtils.isEmpty(description2)) {
                    description.setError("you need to add a description");
                    return;
                }
                documentReference2.update(
                        "nom_etudiant", Nom.getText().toString().trim(),
                        "telephone_etudiant", tel.getText().toString().trim(),
                        "prenom_etudiant", Prenom.getText().toString().trim(),
                        "age_etudiant", Age.getText().toString().trim(),
                        "email_etudiant", mail.getText().toString().trim(),
                        "adresse_etudiant", adresse.getText().toString().trim(),
                        "description_etudiant", description.getText().toString().trim(),
                        "image_etudiant", img,
                        "departement_etudiant", departement,
                        "cv_etudiant", cvUrl.toString(),
                        "motiv_etudiant", motivUrl.toString()
                );
                retour();
            }


        });
        cvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = 1;
                selectPDFFile();
            }
        });

        motivButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = 2;
                selectPDFFile();
            }
        });

        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChangedValue = i * (i / 140);
                int value = (progressChangedValue * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                distanceTextView.setText("" + progressChangedValue);

                circle.setRadius(progressChangedValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            myPosition = place.getLatLng();
            adresse.setText(place.getAddress());
            newPlace(myPosition);
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        } else if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profilepic.setImageURI(imageUri);
            uploadPicture();
        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uploadPDFFile(data.getData());
        }

    }

    private void uploadPDFFile(Uri data) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("uploading ...");
        progressDialog.show();
        StorageReference reference = storageReference.child("uploads/" + System.currentTimeMillis() + ".pdf");
        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isComplete()) ;
                Uri url = uri.getResult();

                String name = null;
                if (i == 1) {
                    name = randomPDFkey;
                    cvUrl = url;
                } else if (i == 2) {
                    name = randomPDFkey;
                    motivUrl = url;
                }
                uploadPDF uploadPDF = new uploadPDF(name, url.toString());
                databaseReference.child(databaseReference.push().getKey()).setValue(uploadPDF);
                Toast.makeText(getApplicationContext(), "File Uploaded", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded " + (int) progress);
            }
        });
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

    private void selectPDFFile() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF File"), 2);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMinZoomPreference(7);
        newPlace(myPosition);

    }

    public void newPlace(LatLng newPosition){
        gMap.clear();
        gMap.moveCamera(CameraUpdateFactory.newLatLng(newPosition));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(newPosition);

        gMap.addMarker(markerOptions);

        circle = gMap.addCircle(new CircleOptions().center(new LatLng(newPosition.latitude, newPosition.longitude)).strokeColor(Color.RED));
    }

    private void retour() {
        finish();
    }
}