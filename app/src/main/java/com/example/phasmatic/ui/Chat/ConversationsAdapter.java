package com.example.phasmatic.ui.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phasmatic.R;
import com.example.phasmatic.data.model.Conversation;
import com.example.phasmatic.data.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Conversation c = list.get(position);

        holder.imgUser.setImageResource(R.drawable.baseline_face_24);

        String otherUid = currentUid.equals(c.leftUser_id)
                ? c.rightUser_id
                : c.leftUser_id;

        holder.txtLastMessage.setText(c.lastMessage != null ? c.lastMessage : "");
        holder.txtTime.setText(c.timeLastMessage != null ? c.timeLastMessage : "");

        if (otherUid == null || otherUid.isEmpty()) {
            holder.txtName.setText("User");
            return;
        }

        holder.txtName.setText("Loading...");

        FirebaseDatabase.getInstance(
                        "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
                ).getReference("users")
                .child(otherUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        holder.txtName.setText(
                                user != null && user.getFullName() != null
                                        ? user.getFullName()
                                        : "User"
                        );

                        String url = snapshot.child("profileImageUrl").getValue(String.class);

                        if (url != null && !url.isEmpty()) {
                            String displayUrl = url + "?t=" +
                                    (c.timeLastMessage != null ? c.timeLastMessage : "");

                            Glide.with(holder.imgUser.getContext())
                                    .load(displayUrl)
                                    .placeholder(R.drawable.baseline_face_24)
                                    .error(R.drawable.baseline_face_24)
                                    .into(holder.imgUser);
                        } else {
                            holder.imgUser.setImageResource(R.drawable.baseline_face_24);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        holder.txtName.setText("User");
                        holder.imgUser.setImageResource(R.drawable.baseline_face_24);
                    }
                });

        holder.itemView.setOnClickListener(v -> listener.onClick(c));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtName, txtLastMessage, txtTime;
        ImageView imgUser;

        VH(@NonNull View itemView) {
            super(itemView);
            txtName        = itemView.findViewById(R.id.txtName);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            txtTime        = itemView.findViewById(R.id.txtTime);
            imgUser      = itemView.findViewById(R.id.imgUser);
        }
    }
}