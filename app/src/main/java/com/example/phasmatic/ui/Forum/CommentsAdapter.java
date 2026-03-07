package com.example.phasmatic.ui.Forum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.ReviewComment;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentVH> {

    private final List<ReviewComment> items;

    public CommentsAdapter(List<ReviewComment> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CommentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review_comment, parent, false);
        return new CommentVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentVH h, int pos) {
        ReviewComment c = items.get(pos);
        h.txtCommentUser.setText(c.user_name != null ? c.user_name : "User");
        h.txtCommentText.setText(c.comment_text != null ? c.comment_text : "");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CommentVH extends RecyclerView.ViewHolder {

        TextView txtCommentUser, txtCommentText;

        CommentVH(@NonNull View v) {
            super(v);
            txtCommentUser = v.findViewById(R.id.txtCommentUser);
            txtCommentText = v.findViewById(R.id.txtCommentText);
        }
    }
}
