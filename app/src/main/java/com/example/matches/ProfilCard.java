package com.example.matches;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@Layout(R.layout.profil_card)
/**
 * Class ProfilCard qui gere le remplissages des cartes
 */
public class ProfilCard {

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView nameAgeTxt;

    @View(R.id.locationNameTxt)
    private TextView locationNameTxt;

    private final ContentProfil profil;

    private SwipePlaceHolderView mSwipeView;
    private Bitmap image;
    private int compteur;
    MatchActivity matchActivity;

    /**
     * @param profil
     * @param swipeView
     * @param matchActivity2
     */
    public ProfilCard(ContentProfil profil, SwipePlaceHolderView swipeView, MatchActivity matchActivity2) {
        this.profil = profil;
        mSwipeView = swipeView;
        downLoadWithBytes();
        compteur = 0;
        matchActivity = matchActivity2;
    }

    /**
     * choix de la courte descripton en dessous de la photo
     */
    @Resolve
    private void onResolved() {

        if ((profil.getAge().length()) < 6) {
            nameAgeTxt.setText(profil.getNom() + ", " + profil.getAge() + " ans");
            locationNameTxt.setText(profil.getLocalisation());
        } else {
            nameAgeTxt.setText(profil.getNom());
            locationNameTxt.setText(profil.getAge() + " au " + profil.getLocalisation());
        }

    }

    /**
     * lancement du profil que l'utilisateur a choisi de regarde en detail
     */
    @Click(R.id.profileImageView)
    public void onProfileImageViewClick() {
        matchActivity.launch(profil.getId(), profil);
    }


    /**
     * telegargement de l'image de l'utilisateur
     */
    public void downLoadWithBytes() {
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference().child("images").child("" + profil.getImageProfil());
        storageRef.getBytes(1920 * 1080 * 5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    profileImageView.setImageBitmap(bitmap);
                }
            }
        });
    }
}
