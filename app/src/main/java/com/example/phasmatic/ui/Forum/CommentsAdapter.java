package com.example.phasmatic.ui.Forum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phasmatic.R;
import com.example.phasmatic.data.model.ReviewComment;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentVH> {

    public interface OnCommentUserClickListener {
        void onUserClick(ReviewComment comment);
    }

    private final List<ReviewComment> items;
    private final Map<String, String> nameMap;
    private final Map<String, String> academicMap;
    private final OnCommentUserClickListener userClickListener;

    private final DatabaseReference usersRef;

    public CommentsAdapter(List<ReviewComment> items,
                           Map<String, String> nameMap,
                           Map<String, String> academicMap,
                           DatabaseReference usersRef,
                           OnCommentUserClickListener userClickListener) {
        this.items = items;
        this.nameMap = nameMap != null ? nameMap : new HashMap<>();
        this.academicMap = academicMap != null ? academicMap : new HashMap<>();
        this.usersRef = usersRef;
        this.userClickListener = userClickListener;
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

        String name = nameMap.containsKey(c.user_id)
                ? nameMap.get(c.user_id)
                : (c.user_name != null ? c.user_name : "User");

        String academic = academicMap.get(c.user_id);
        if (academic == null) academic = c.academic_profile;

        h.txtCommentUser.setText(name);
        h.txtCommentAcademic.setText(academic != null ? academic : "");
        h.txtCommentText.setText(c.comment_text != null ? c.comment_text : "");

        //default eikona mexri na kanei load
        h.imgCommentUser.setImageResource(R.drawable.baseline_face_24);

        if (c.user_id != null && !c.user_id.isEmpty()) {
            usersRef.child(c.user_id).get().addOnSuccessListener(snapshot -> {
                String url = snapshot.child("profileImageUrl").getValue(String.class);
                if (url != null && !url.isEmpty()) {
                    String displayUrl = url + "?t=" + System.currentTimeMillis();
                    Glide.with(h.itemView.getContext())
                            .load(displayUrl)
                            .placeholder(R.drawable.baseline_face_24)
                            .error(R.drawable.baseline_face_24)
                            .into(h.imgCommentUser);
                } else {
                    h.imgCommentUser.setImageResource(R.drawable.baseline_face_24);
                }
            }).addOnFailureListener(e ->
                    h.imgCommentUser.setImageResource(R.drawable.baseline_face_24)
            );
        }

        h.imgCommentUser.setOnClickListener(v -> {
            if (userClickListener != null) userClickListener.onUserClick(c);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CommentVH extends RecyclerView.ViewHolder {

        ImageView imgCommentUser;
        TextView txtCommentUser, txtCommentAcademic, txtCommentText;

        CommentVH(@NonNull View v) {
            super(v);
            imgCommentUser = v.findViewById(R.id.imgCommentUser);
            txtCommentUser = v.findViewById(R.id.txtCommentUser);
            txtCommentAcademic = v.findViewById(R.id.txtCommentAcademic);
            txtCommentText = v.findViewById(R.id.txtCommentText);
        }
    }
}