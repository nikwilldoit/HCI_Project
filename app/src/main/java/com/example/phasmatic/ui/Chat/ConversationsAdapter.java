package com.example.phasmatic.ui.Chat;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.Conversation;
import com.example.phasmatic.data.model.User;

import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.VH> {

    public interface OnConversationClick {
        void onClick(Conversation conversation);
    }

    private final List<Conversation> list;
    private final String currentUid;
    private final OnConversationClick listener;

    public ConversationsAdapter(List<Conversation> list, String currentUid, OnConversationClick listener) {
        this.list = list;
        this.currentUid = currentUid;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new VH(v);
    }

    // emfanizei ton user sto chat
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Conversation c = list.get(position);



        holder.txtName.setText("PRODROMOS");
        holder.txtLastMessage.setText(c.lastMessage != null ? c.lastMessage : "");
        holder.txtTime.setText(c.timeLastMessage != null ? c.timeLastMessage : "");

        holder.itemView.setOnClickListener(v -> listener.onClick(c));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtName, txtLastMessage, txtTime;
        VH(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}
