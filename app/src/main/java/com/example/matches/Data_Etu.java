package com.example.matches;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Data_Etu {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String email, motDP, prenom2, tel, nom2, adres, age2, description2, randomkey;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    String userID;

    public Data_Etu(String email, String motDP, String nom2, String prenom2, String tel, String adres, String age2, String description2, String randomkey) {
        this.email = email;
        this.motDP = motDP;
        this.prenom2 = prenom2;
        this.tel = tel;
        this.adres = adres;
        this.age2 = age2;
        this.description2 = description2;
        this.randomkey = randomkey;
        this.nom2 = nom2;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public void createUser() {
        //enregister un utilisateur avec un email et un mdp
        firebaseAuth.createUserWithEmailAndPassword(email, motDP).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //lui ajouter DES ATTIBUTS dans la firebase avec firebaseauth et son id
                if (task.isSuccessful()) {
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
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("ee", "c'est bon");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("ee", "" + e.getCause());
                        }
                    });
                }
            }
        });
    }


}
