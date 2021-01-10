package com.example.matches;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

/**
 * Intent d'ouverture de l'application
 * Elle permet de ce connecter avec ses identifants
 * Elle permet  aussi de passerelle à l'incription
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public TextView identifiantTextView;
    public TextView passwordTextView;
    public Button validationConnexionButton;
    public FirebaseAuth fAuth;
    public ProgressBar progressBar;
    public TextView register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        validationConnexionButton = (Button) findViewById(R.id.validationConnexionButton);
        identifiantTextView = (TextView) findViewById(R.id.identifiantTextView);
        passwordTextView = (TextView) findViewById(R.id.passwordTextView);
        fAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        register = (TextView) findViewById(R.id.modifier);


        /**
         * lors de l'appui sur le texte "Pas encore inscrit? clique ici" ouvre l'intent(page) EntreprisOuEtudiant
         */
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EntrepriseOuEtudiant.class));
            }
        });


        /**
         * Lors de l'appui sur le boutton "CONNEXION" ou vas vérifier si les champs de text login et password ne sont pas vide sinon on met une erreur qui vas afficher a l'ecran qu'on doit ecrire dans ces champs
         * Puis on demande a la base de donnée si le login et le password rentrés sont correct et si c'est vrai on fait un toast qui alerte l'utilisateur qui est connecté et on ouvre l'intent(page) MatchActivity
         * Sinon on alerte l'utilisateur avec un taost "Mauvais identifiant ou mot de passe"
         */
        validationConnexionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                            startActivity(new Intent(getApplicationContext(), MatchActivity.class));
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

}