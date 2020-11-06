package com.example.matches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class EntrepriseOuEtudiant extends AppCompatActivity {
    private Button etudiant, entreprise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entreprise_ou_etudiant);
        etudiant = (Button) findViewById(R.id.Etudiant);
        entreprise = (Button) findViewById(R.id.Entreprise);

        etudiant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etudiantlaunch();
            }
        });
        entreprise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entrepriselaunch();
            }
        });
    }

    public void etudiantlaunch() {
        Intent intent = new Intent(this, RegistersActivity.class);
        startActivity(intent);

    }

    public void entrepriselaunch() {
        Intent intent = new Intent(this, RegisterEntrepriseActivity.class);
        startActivity(intent);
    }



}