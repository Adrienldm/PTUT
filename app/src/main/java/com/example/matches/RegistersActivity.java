package com.example.matches;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RegistersActivity extends AppCompatActivity implements OnMapReadyCallback {
    final String randomkey = UUID.randomUUID().toString();
    final String randomPDFkey = UUID.randomUUID().toString();
    final String randomPDFkey2 = UUID.randomUUID().toString();
    Button register, cvButton, motivButton;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID, departement;
    public Uri imageUri;
    EditText nom, prenom, adresseMail, motDePasse, telephone, adresse, age, description;
    final static int SELECT_PICTURE = 1;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    LatLng myPosition = new LatLng(48.0667 , -0.7667);
    private SeekBar distanceSeekBar;
    private TextView distanceTextView;
    private MapView mapView;
    private GoogleMap gMap;
    Circle circle;
    private ImageView img1;
    private RadioButton info, gb, tc, mmi;
    private int i = 0;


    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        img1 = (ImageView) findViewById(R.id.image1);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        cvButton = (Button) findViewById(R.id.cvButton);
        motivButton = (Button) findViewById(R.id.motivButton);
        info = (RadioButton) findViewById(R.id.info);
        gb = (RadioButton) findViewById(R.id.gb);
        tc = (RadioButton) findViewById(R.id.tc);
        mmi = (RadioButton) findViewById(R.id.mmi);
        nom = (EditText) findViewById(R.id.nom);
        prenom = (EditText) findViewById(R.id.prenomE);
        adresseMail = (EditText) findViewById(R.id.mail);
        motDePasse = (EditText) findViewById(R.id.MDP);
        telephone = (EditText) findViewById(R.id.tel);
        adresse = (EditText) findViewById(R.id.adresse);
        initAutocompletion();
        age = (EditText) findViewById(R.id.age);
        register = (Button) findViewById(R.id.modifier);

        description = (EditText) findViewById(R.id.desc);
        distanceSeekBar = (SeekBar) findViewById(R.id.distanceSeekBar);
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });
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


        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = adresseMail.getText().toString().trim();
                String motDP = motDePasse.getText().toString().trim();
                final String nom2 = nom.getText().toString().trim();
                final String prenom2 = prenom.getText().toString().trim();
                final String tel = telephone.getText().toString().trim();
                final String adres = adresse.getText().toString().trim();
                final String age2 = age.getText().toString().trim();
                final String description2 = description.getText().toString().trim();


                if (departement == null) {
                    mmi.setError("you must chose a case");
                }
                if (TextUtils.isEmpty(prenom2)) {
                    prenom.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(nom2)) {
                    nom.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    adresseMail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(motDP)) {
                    motDePasse.setError("Password is required");
                    return;
                }
                if (TextUtils.isEmpty(tel)) {
                    telephone.setError("Phone is required");
                    return;
                }
                if (TextUtils.isEmpty(adres)) {
                    adresse.setError("Adress is required");
                    return;
                }
                if (TextUtils.isEmpty(age2)) {
                    age.setError("Age is required");
                    return;
                }
                if (TextUtils.isEmpty(description2)) {
                    age.setError("you need to add a description");
                    return;
                }
                if (motDP.length() < 6) {
                    motDePasse.setError("Password Must be >= 6 Caracters");
                    return;
                }
                //enregister un utilisateur avec un email et un mdp
                firebaseAuth.createUserWithEmailAndPassword(email, motDP).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //lui ajouter DES ATTIBUTS dans la firebase avec firebaseauth et son id
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistersActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            userID = firebaseAuth.getCurrentUser().getUid();


                            DocumentReference documentReference = firestore.collection("etudiant").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("nom_etudiant", nom2);
                            user.put("prenom_etudiant", prenom2);
                            user.put("email_etudiant", email);
                            user.put("telephone_etudiant", tel);
                            user.put("adresse_etudiant", adres);
                            user.put("age_etudiant", age2);
                            user.put("description_etudiant", description2);
                            user.put("image_etudiant", randomkey);
                            user.put("departement_etudiant", departement);
                            user.put("cv_etudiant", randomPDFkey + ".pdf");
                            user.put("motiv_etudiant", randomPDFkey2 + ".pdf");
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG", "OnSucces: user profile is created for" + userID);
                                    startregister2();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", "OnFailure: " + e.toString());
                                }
                            });

                        } else {
                            Toast.makeText(RegistersActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChangedValue = i * (i / 140);
                int value = (progressChangedValue * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                distanceTextView.setText("" + progressChangedValue);
                distanceTextView.setX(seekBar.getX() + value + seekBar.getThumbOffset() / 2 - 10);
                distanceTextView.setY(seekBar.getY());
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

    private void selectPDFFile() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF File"), 2);
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
            img1.setImageURI(imageUri);
            uploadPicture();
        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uploadPDFFile(data.getData());
        }

    }

    private void uploadPDFFile(Uri data) {
        String name;
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
                if (i == 1) name = randomPDFkey;
                else if (i == 2) name = randomPDFkey;
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


    public void startregister2() {
        Intent intent = new Intent(this, matchActivity.class);
        startActivity(intent);
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

}
