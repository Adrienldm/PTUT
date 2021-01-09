package com.example.matches;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

public class StageCreationActivity extends AppCompatActivity {

    EditText stageTitreEditText;
    EditText dateStageDebutEditTextDate;
    EditText dateStageFinEditTextDate;
    EditText descStageEditText;
    EditText competencesEditTextMultiLine;
    Button stageButton;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID, departement, image;
    RadioButton info, gb, tc, mmi;
    DatePickerDialog picker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_creation);

        stageTitreEditText = (EditText) findViewById(R.id.stageTitreEditText);
        dateStageDebutEditTextDate = (EditText) findViewById(R.id.dateStageDebutEditTextDate);
        dateStageFinEditTextDate = (EditText) findViewById(R.id.dateStageFinEditTextDate);
        descStageEditText = (EditText) findViewById(R.id.descStageEditText);
        competencesEditTextMultiLine = (EditText) findViewById(R.id.competencesEditTextMultiLine);
        stageButton = (Button) findViewById(R.id.stageButton);
        info = (RadioButton) findViewById(R.id.info);
        gb = (RadioButton) findViewById(R.id.gb);
        tc = (RadioButton) findViewById(R.id.tc);
        mmi = (RadioButton) findViewById(R.id.mmi);
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
        dateStageDebutEditTextDate.setInputType(InputType.TYPE_NULL);
        dateStageDebutEditTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(StageCreationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dateStageDebutEditTextDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        dateStageFinEditTextDate.setInputType(InputType.TYPE_NULL);
        dateStageFinEditTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(StageCreationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dateStageFinEditTextDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        userID = firebaseAuth.getCurrentUser().getUid();
        final DocumentReference documentReference2 = firestore.collection("entreprise").document(userID);
        documentReference2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                image = documentSnapshot.getString("image_entreprise");
            }
        });

        stageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String Titre = stageTitreEditText.getText().toString().trim();
                final String dateDebut = dateStageDebutEditTextDate.getText().toString().trim();
                final String datefin = dateStageFinEditTextDate.getText().toString().trim();
                final String descStage = descStageEditText.getText().toString().trim();
                final String competences = competencesEditTextMultiLine.getText().toString().trim();


                if (departement == null) {
                    mmi.setError("you must chose a case");
                }

                if (TextUtils.isEmpty(Titre)) {
                    stageTitreEditText.setError("Title is required");
                    return;
                }

                if (TextUtils.isEmpty(dateDebut)) {
                    dateStageDebutEditTextDate.setError("date is required");
                    return;
                }
                if (TextUtils.isEmpty(datefin)) {
                    dateStageFinEditTextDate.setError("date is required");
                    return;
                }
                if (TextUtils.isEmpty(descStage)) {
                    descStageEditText.setError("description is required");
                    return;
                }
                if (TextUtils.isEmpty(competences)) {
                    competencesEditTextMultiLine.setError("skills is required");
                    return;
                }

                if (departement == null) {
                    mmi.setError("you must chose a case");
                    return;
                }


                DocumentReference documentReference = firestore.collection("offreStage").document(UUID.randomUUID().toString());
                Map<String, Object> user = new HashMap<>();
                user.put("Titre", Titre);
                user.put("compétenceStage", competences);
                user.put("dateDebut", dateDebut);
                user.put("dateFin", datefin);
                user.put("descriptionStage", descStage);
                user.put("idEntreprise", userID);
                user.put("Département choisi", departement);
                user.put("image_entreprise", image);

                documentReference.set(user).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d("TAG", "OnSucces: user profile is created for" + userID);
                        startregister2();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "OnFailure: " + e.toString());
                    }
                });

            }

        });
    }


    public void startregister2() {
        finish();
    }


}