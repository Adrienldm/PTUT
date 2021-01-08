package com.example.matches;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import javax.annotation.Nullable;

public class MatchActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userId, typeUser;
    Button disconnectButton, profilButton, stage;
    SwipePlaceHolderView mSwipeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchs);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.get("typeUser") != null) {
                typeUser = extras.getString("typeUser");
            }
        } else {
            Log.e("Test1", "59546536549854235462428951352");
        }
        String typeUser = getIntent().getStringExtra("typeUser");

        mSwipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);

        stage = (Button) findViewById(R.id.stage);
        disconnectButton = findViewById(R.id.disconnectButton);
        profilButton = findViewById(R.id.profilButton);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        stage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), StageCreationActivity.class);
                startActivity(intent);
            }
        });
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.swipe_view_like)
                        .setSwipeOutMsgLayoutId(R.layout.swipe_view_dislike));

        if (typeUser.equals("etudiant")) {
            firestore.collection("entreprise")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ContentProfil profil;
                                Log.e("Test1", "tesr");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.e("Test", "tesr");
                                    profil = new ContentProfil(document.getString("nom_entreprise"), document.getString("adresse_entreprise"), document.getString("image_entreprise"));
                                    mSwipeView.addView(new ProfilCard(profil, mSwipeView));
                                }
                            } else {
                                Log.d("e", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        else{
            stage.setVisibility(View.VISIBLE);
            firestore.collection("etudiant")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ContentProfil profil;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    profil = new ContentProfil(document.getString("nom_etudiant"), document.getString("age_etudiant"), document.getString("adresse_etudiant"), document.getString("image_etudiant"));
                                    mSwipeView.addView(new ProfilCard(profil, mSwipeView));
                                }
                            } else {
                                Log.d("e", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }



        findViewById(R.id.dislikeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });

        findViewById(R.id.likeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
            }
        });

        disconnectButton.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View view){

        Intent intent = new Intent(view.getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        startActivity(intent);
    }
    });

        profilButton.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View view){
        final DocumentReference documentReference = firestore.collection("entreprise").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String a = documentSnapshot.getString("nom_entreprise");
                if (a != null) {
                    Intent intent = new Intent(getApplicationContext(), Profil_Entreprise.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), profil.class);
                    startActivity(intent);
                }

            }
        });
    }
    });

}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }

}