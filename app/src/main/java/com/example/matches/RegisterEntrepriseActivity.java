package com.example.matches;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterEntrepriseActivity extends AppCompatActivity {
    EditText nom, prenom, adresseMail, motDePasse, telephone, adresse;
    Button register;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_entreprise);
        nom = (EditText) findViewById(R.id.nomE);
        prenom = (EditText) findViewById(R.id.prenomE);
        adresseMail = (EditText) findViewById(R.id.mailE);
        motDePasse = (EditText) findViewById(R.id.MDPE);
        telephone = (EditText) findViewById(R.id.telE);
        adresse = (EditText) findViewById(R.id.adresseE);
        register = (Button) findViewById(R.id.registerE);
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
                            Toast.makeText(RegisterEntrepriseActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            userID = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firestore.collection("entreprise").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("nom_entreprise", nom2);
                            user.put("prenom_entreprise", prenom2);
                            user.put("email_entreprise", email);
                            user.put("telephone_entreprise", tel);
                            user.put("adresse_entreprise", adres);
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
                            Toast.makeText(RegisterEntrepriseActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    public void startregister2() {
        Intent intent = new Intent(this, matchActivity.class);
        startActivity(intent);
    }

}