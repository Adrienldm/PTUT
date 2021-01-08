package com.example.matches;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public TextView identifiantTextView;
    public TextView passwordTextView;
    public TextView nomEntrepriseTextView;
    public TextView dernierMessageTextView;
    public TextView messageEditTextView;
    public Button validationConnexionButton;
    public FirebaseAuth fAuth;
    public ProgressBar progressBar;
    public TextView register;

    public void startregister() {
        Intent intent = new Intent(this, EntrepriseOuEtudiant.class);
        startActivity(intent);
    }

    public void startregister2(boolean value) {
        Intent intent = new Intent(this, MatchActivity.class);
        String typeUser;
        if(value){
            typeUser = "entreprise";
        }
        else{
            typeUser = "etudiant";
        }
        intent.putExtra("typeUser", typeUser);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        validationConnexionButton = (Button) findViewById(R.id.validationConnexionButton);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //redefinition des textview vers les fichier xml
        identifiantTextView = (TextView) findViewById(R.id.identifiantTextView);
        passwordTextView = (TextView) findViewById(R.id.passwordTextView);
        fAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        register = (TextView) findViewById(R.id.modifier);

        //go to register page


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startregister();
            }
        });


        //login btn
        validationConnexionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //prendre l'instance de la firebase
                final FirebaseDatabase database = FirebaseDatabase.getInstance();

                String login = identifiantTextView.getText().toString().trim();
                String password = passwordTextView.getText().toString().trim();

                if (TextUtils.isEmpty(login)) {
                    identifiantTextView.setError("login is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordTextView.setError("password is required");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //identification de l'user avec son login et son password
                fAuth.signInWithEmailAndPassword(login, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    //quand c'est fait
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //si son login et son mdp sont bon et dans la firebase
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "succeful login", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            startregister2(searchTypeUser(fAuth.getCurrentUser().getUid()));


                        } else {
                            Toast.makeText(MainActivity.this, "Mauvais identifiant ou mot de passe ", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }

            ;
        });


    }

    @Override
    public void onClick(View v) {
        this.setContentView(R.layout.activity_matchs);
    }

    public boolean searchTypeUser(String userId){
        // false veut dire que l'utilisateur est une entreprise
        final boolean[] result = new boolean[1];
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("entreprise")
                .child(userId) // Create a reference to the child node directly
                .addListenerForSingleValueEvent(new ValueEventListener() {


                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This callback will fire even if the node doesn't exist, so now check for existence
                        if (dataSnapshot.exists()) {
                            result[0] = false;
                        } else {
                            result[0] = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Error", String.valueOf(databaseError));
                    }

                });

        return result[0];

    }
}