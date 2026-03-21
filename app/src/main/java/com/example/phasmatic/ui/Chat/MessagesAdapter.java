package com.example.phasmatic.ui.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.Message;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USER  = 1;
    private static final int TYPE_OTHER = 2;

    private final List<Message> messages;
    private final String currentUid;

    public MessagesAdapter(List<Message> messages, String currentUid) {
        this.messages   = messages;
        this.currentUid = currentUid;
    }

    @Override
    public int getItemViewType(int position) {
        String senderId = messages.get(position).sender_id;
        return (currentUid != null && currentUid.equals(senderId)) ? TYPE_USER : TYPE_OTHER;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_chat_right, parent, false);
            return new UserVH(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_chat_left, parent, false);
            return new OtherVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message m = messages.get(position);
        if (holder instanceof UserVH) {
            ((UserVH) holder).txtMessageUser.setText(m.messageText);
        } else if (holder instanceof OtherVH) {
            ((OtherVH) holder).txtMessageOther.setText(m.messageText);
        }
    }

    @Override
    public int getItemCount() { return messages.size(); }

    static class UserVH extends RecyclerView.ViewHolder {
        TextView txtMessageUser;
        UserVH(@NonNull View itemView) {
            super(itemView);
            txtMessageUser = itemView.findViewById(R.id.txtMessageUser);
        }
    }

    static class OtherVH extends RecyclerView.ViewHolder {
        TextView txtMessageOther;
        OtherVH(@NonNull View itemView) {
            super(itemView);
            txtMessageOther = itemView.findViewById(R.id.txtMessageOther);
        }
    }
}
