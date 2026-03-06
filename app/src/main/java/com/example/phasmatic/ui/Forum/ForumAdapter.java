package com.example.phasmatic.ui.Forum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.ForumReview;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ForumVH> {

    public interface OnReviewClickListener {
        void onReviewClick(ForumReview review);
    }

    private final List<ForumReview> items;
    private final OnReviewClickListener listener;

    private final DatabaseReference forumRef;
    private final DatabaseReference reviewLikesRef;
    private final String userId;

    public ForumAdapter(List<ForumReview> items,
                        String userId,
                        OnReviewClickListener listener) {
        this.items = items;
        this.listener = listener;
        this.userId = userId;

        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        forumRef = db.getReference("forum_reviews");
        reviewLikesRef = db.getReference("review_likes");
    }

    @NonNull
    @Override
    public ForumVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forum_review, parent, false);
        return new ForumVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ForumVH h, int pos) {
        ForumReview r = items.get(pos);

        String title;
        if (r.type != null) {
            if ("erasmus".equalsIgnoreCase(r.type)) {
                title = "Erasmus · " + r.university;
            } else if ("master".equalsIgnoreCase(r.type)) {
                title = "Master · " + r.university;
            } else {
                title = r.university;
            }
        } else {
            title = r.university;
        }
        h.txtTitle.setText(title != null ? title : "");

        h.txtUser.setText(r.user_name != null ? "by " + r.user_name : "");
        h.ratingBar.setRating(r.rating);
        h.txtText.setText(r.text != null ? r.text : "");
        h.txtLikes.setText(String.valueOf(r.likes));

        if (r.id != null && userId != null) {
            reviewLikesRef.child(r.id).child(userId).get()
                    .addOnSuccessListener(snap -> {
                        boolean liked = Boolean.TRUE.equals(snap.getValue(Boolean.class));
                        h.btnLike.setTag(liked);
                        h.btnLike.setImageResource(
                                liked ? R.drawable.heartfull : R.drawable.heartempty
                        );
                    });
        } else {
            h.btnLike.setTag(false);
            h.btnLike.setImageResource(R.drawable.heartempty);
        }

        h.btnLike.setOnClickListener(v -> {
            if (r.id == null || userId == null) return;

            Boolean tag = (Boolean) v.getTag();
            boolean liked = tag != null && tag;

            if (liked) {
                //no like
                v.setTag(false);
                h.btnLike.setImageResource(R.drawable.heartempty);
                r.likes = Math.max(0, r.likes - 1);
                h.txtLikes.setText(String.valueOf(r.likes));

                forumRef.child(r.id).child("likes").setValue(r.likes);
                reviewLikesRef.child(r.id).child(userId).setValue(null);
            } else {
                //like
                v.setTag(true);
                h.btnLike.setImageResource(R.drawable.heartfull);
                r.likes++;
                h.txtLikes.setText(String.valueOf(r.likes));

                forumRef.child(r.id).child("likes").setValue(r.likes);
                reviewLikesRef.child(r.id).child(userId).setValue(true);
            }
        });

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onReviewClick(r);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ForumVH extends RecyclerView.ViewHolder {

        TextView txtTitle, txtUser, txtText, txtLikes;
        RatingBar ratingBar;
        ImageButton btnLike;

        ForumVH(@NonNull View v) {
            super(v);
            txtTitle = v.findViewById(R.id.txtTitle);
            txtUser = v.findViewById(R.id.txtUser);
            txtText = v.findViewById(R.id.txtText);
            ratingBar = v.findViewById(R.id.ratingBar);
            btnLike = v.findViewById(R.id.btnLike);
            txtLikes = v.findViewById(R.id.txtLikes);
        }
    }
}
