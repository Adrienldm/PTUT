package com.example.matches;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Personnalise le viewHolder en ajoutant les différents éléments comme le textview
 */
public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView messageTextView;
    public LinearLayout linearLayout;
    public ChatViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        linearLayout = itemView.findViewById(R.id.container);
        messageTextView = itemView.findViewById(R.id.message);
    }

    @Override
    public void onClick(View view) {
    }

}