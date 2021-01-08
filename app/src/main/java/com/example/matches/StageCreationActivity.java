package com.example.matches;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.opencensus.tags.Tag;

public class StageCreationActivity extends AppCompatActivity {

    EditText stageTitreEditText;
    EditText dateStageDebutEditTextDate;
    EditText dateStageFinEditTextDate;
    EditText descStageEditText;
    EditText competencesEditTextMultiLine;
    Button stageButton;
    Spinner spinnerDptVise;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_creation);

        stageTitreEditText = (EditText) findViewById(R.id.stageTitreEditText);
        dateStageDebutEditTextDate = (EditText) findViewById(R.id.dateStageDebutEditTextDate);
        dateStageFinEditTextDate = (EditText) findViewById(R.id.dateStageFinEditTextDate);
        descStageEditText = (EditText) findViewById(R.id.descStageEditText);
        competencesEditTextMultiLine = (EditText) findViewById(R.id.competencesEditTextMultiLine);
        spinnerDptVise = (Spinner) findViewById(R.id.snipperDptVise);
        stageButton = (Button) findViewById(R.id.stageButton);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        stageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String Titre = stageTitreEditText.getText().toString().trim();
                final String dateDebut = dateStageDebutEditTextDate.getText().toString().trim();
                final String datefin = dateStageFinEditTextDate.getText().toString().trim();
                final String descStage = descStageEditText.getText().toString().trim();
                final String competences = competencesEditTextMultiLine.getText().toString().trim();
                final String dptVise = spinnerDptVise.getSelectedItem().toString();

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
                if (TextUtils.isEmpty(dptVise)) {
                    //Pour permettre de créer une erreur lorsque un département n'est pas choisi
                    TextView errorText = (TextView) spinnerDptVise.getSelectedView();
                    errorText.setError("");
                    errorText.setText("Departement vise is required");
                    return;
                }

                // permet d'ajouter des éléments au Spinner
                String[] itemsSpinner = new String[]{"Informatique", "MMI", "TC", "Génie Biologie"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, itemsSpinner);
                spinnerDptVise.setAdapter(adapter);

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
                user.put("Département choisi", dptVise);


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


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            adresse.setText(place.getAddress());
        }
        else if (resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
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
    }*/

}