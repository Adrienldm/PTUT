package com.example.matches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


/**
 * Intent(Page) EntrepriseOuEtudiant
 * son but est de differencier l'utilisatuer si c'est un étudiant un une entreprise
 */
public class EntrepriseOuEtudiant extends AppCompatActivity {
    private Button etudiant, entreprise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entreprise_ou_etudiant);
        etudiant = (Button) findViewById(R.id.Etudiant);
        entreprise = (Button) findViewById(R.id.Entreprise);

        /**
         * lors de l'appuis sur le boutton etudiant ou ouvre l'intent(page) de l'inscription de l'étudiant
         */
        etudiant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegistersActivity.class));
            }
        });

        /**
         * lors de l'appuis sur le boutton entreprise ou ouvre l'intent(page) de l'inscription de l'entreprise
         */
        entreprise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterEntrepriseActivity.class));
            }
        });
    }

}