package com.example.matches;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



import javax.annotation.Nullable;

/**
 * Intent(Page) MatchActivity
 * son but est de d'afficher le chat ainsi que toutes les intéractions éventuelles de l'utilisateur
 */

public class ChatActivity extends AppCompatActivity {
    private RecyclerView ownRecyclerView;
    private ChatAdapter adapter;
    private RecyclerView.LayoutManager ownLayoutManager;

    private EditText sendEditText;

    private Button sendButton;

    private String userId, matchId, chatId;
    private DatabaseReference databaseUser, databaseChat;
    private ArrayList<ChatObject> resultsChat = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        /**
         * A chaque fois que l'activité se lance, on regarde si l'utilisateur a déjà un id dans la base de données
         * de l'application ( qui est différente du storage utilisé précedemment)
         */
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean alreadyRegistered = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (userSnapshot.getValue() == userId) {
                        alreadyRegistered = true;
                    }
                }
                if (alreadyRegistered == false) {
                    createUser();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });

        /**
         * On récupère les différentes données nécessaires
         */
        databaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");
        databaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ChatId");
        getIdChat();
        adapter = new ChatAdapter(getDataSetChat());
        ownRecyclerView = findViewById(R.id.recyclerView);
        ownRecyclerView.setNestedScrollingEnabled(false);
        ownRecyclerView.setHasFixedSize(false);
        ownLayoutManager = new LinearLayoutManager(ChatActivity.this);
        ownRecyclerView.setLayoutManager(ownLayoutManager);
        ownRecyclerView.setAdapter(adapter);
        sendEditText = findViewById(R.id.message);
        sendButton = findViewById(R.id.send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    /**
     * Permet d'envoyer les messages vers la base de données grâce à un objet Message
     */
    private void sendMessage() {
        String sendMessageText = sendEditText.getText().toString();

        if (!sendMessageText.isEmpty()) {
            DatabaseReference newMessageDb = databaseChat.push();

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", userId);
            newMessage.put("text", sendMessageText);
            newMessageDb.setValue(newMessage);
        }
        sendEditText.setText(null);
    }

    /**
     * A chaque fois que l'activité se lance ou que une donnée est ajoutée, on récupère l'id du chat
     */
    private void getIdChat() {
        databaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatId = dataSnapshot.getValue().toString();
                    databaseChat = databaseChat.child(chatId);
                    getChatMessages();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    /**
     * A chaque fois que l'activité se lance ou que une donnée est ajoutée, on récupère les messages du chat
     */
    private void getChatMessages() {
        databaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    String message = null;
                    String ownerMessage = null;

                    if (dataSnapshot.child("text").getValue() != null) {
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if (dataSnapshot.child("createdByUser").getValue() != null) {
                        ownerMessage = dataSnapshot.child("createdByUser").getValue().toString();
                    }

                    if (message != null && ownerMessage != null) {
                        Boolean currentUserBoolean = false;
                        if (ownerMessage.equals(userId)) {
                            currentUserBoolean = true;
                        }
                        ChatObject newMessage = new ChatObject(message, currentUserBoolean);
                        resultsChat.add(newMessage);
                        adapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * On crée un utilisateur dans la Realtime Database de Firabse selon si c'est une entreprise ou un étudiant
     * Architecture : Users
     *                  Id_Utilisateur
     *                      Nom
     *                      ChatId
     */
    public void createUser() {
        FirebaseDatabase.getInstance().getReference().child("Users").push().setValue(userId);
        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("entreprise").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.getString("nom_entreprise") == null) {
                    final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("etudiant").document(userId);
                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            matchId = "Q2l4ix1SeohqdVXJ9Dyy0DmBFDj2";
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                            Map<String, Object> map = new HashMap<>();
                            map.put("Nom", documentSnapshot.getString("nom_etudiant"));
                            map.put("ChatId", matchId + userId);
                            ref.updateChildren(map);
                        }
                    });

                } else {
                    matchId = "34meU5WHrnh8KyGU1INTk2btmLh1";
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                    Map<String, Object> map = new HashMap<>();
                    map.put("Nom", documentSnapshot.getString("nom_entreprise"));
                    map.put("ChatId", userId + matchId);
                    ref.updateChildren(map);
                }
            }

        });
    }


    private List<ChatObject> getDataSetChat() {
        return resultsChat;
    }

}

