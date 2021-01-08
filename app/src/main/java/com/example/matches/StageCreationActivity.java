package com.example.matches;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StageCreationActivity extends AppCompatActivity {

    EditText stageTitreEditText;
    EditText dateStageDebutEditTextDate;
    EditText dateStageFinEditTextDate;
    EditText descStageEditText;
    EditText competencesEditTextMultiLine;
    Button stageButton;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID, departement;
    RadioButton info, gb, tc, mmi;


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




                //userID = firebaseAuth.getCurrentUser().getUid();
                userID = firebaseAuth.getCurrentUser().getUid();

                DocumentReference documentReference = firestore.collection("offreStage").document(userID);
                Map<String, Object> user = new HashMap<>();
                user.put("Titre", Titre);
                user.put("compétenceStage", competences);
                user.put("dateDebut", dateDebut);
                user.put("dateFin", datefin);
                user.put("descriptionStage", descStage);
                user.put("idEntreprise", userID);
                user.put("Département choisi", departement);


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
        Intent intent = new Intent(this , MatchActivity.class);

        startActivity(intent);
    }


}