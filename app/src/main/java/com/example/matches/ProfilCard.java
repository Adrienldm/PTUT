package com.example.matches;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@Layout(R.layout.profil_card)
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

    public ProfilCard(ContentProfil profil, SwipePlaceHolderView swipeView) {
        this.profil = profil;
        mSwipeView = swipeView;
        downLoadWithBytes();
        compteur = 0;
    }

    @Resolve
    private void onResolved(){
        nameAgeTxt.setText(profil.getNom() + ", " + profil.getAge());
        locationNameTxt.setText(profil.getLocalisation());
    }



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
