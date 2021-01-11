package com.example.matches;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Personnalise l'adapter du recyclerview selon les besoins
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders>{
    private List<ChatObject> chatList;


    public ChatAdapter(List<ChatObject> matchesList){
        this.chatList = matchesList;
    }

    @Override
    public ChatViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(layoutParams);
        ChatViewHolders chatViewHolders = new ChatViewHolders(layoutView);

        return chatViewHolders;
    }
    /**
     * On ajoute tous les paramètres nécessaires au bon fonctionnement lors d'un ajout d'un message
     */
    @Override
    public void onBindViewHolder(ChatViewHolders holder, int position) {
        holder.messageTextView.setText(chatList.get(position).getMessage());
        if(chatList.get(position).getCurrentUser()){
            holder.messageTextView.setGravity(Gravity.END);
            holder.messageTextView.setTextColor(Color.parseColor("#404040"));
            holder.linearLayout.setBackgroundColor(Color.parseColor("#33DB79"));
        }else{
            holder.messageTextView.setGravity(Gravity.START);
            holder.messageTextView.setTextColor(Color.parseColor("#404040"));
            holder.linearLayout.setBackgroundColor(Color.parseColor("#F4F4F4"));
        }

    }

    @Override
    public int getItemCount() {
        return this.chatList.size();
    }

}
