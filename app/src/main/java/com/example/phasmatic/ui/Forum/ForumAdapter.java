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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ForumVH> {

    public interface OnReviewClickListener {
        void onReviewClick(ForumReview review);
    }

    private final List<ForumReview> items;
    private final OnReviewClickListener listener;

    private final DatabaseReference forumRef;
    private final DatabaseReference reviewLikesRef;
    //private final DatabaseReference reviewViewsRef;
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
        //reviewViewsRef = db.getReference("review_views");
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
        h.txtComments.setText(String.valueOf(r.comments));
        h.txtView.setText(String.valueOf(r.views));

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onReviewClick(r);
        });

        if (r.id == null || userId == null) return;

        String likeKey = r.id + "_" + userId;
        DatabaseReference likeRef = reviewLikesRef.child(likeKey);

        h.btnLike.setEnabled(false);
        h.btnLike.setImageResource(R.drawable.heartempty);
        h.btnLike.setTag(false);

        likeRef.get().addOnSuccessListener(snap -> {

            boolean liked = snap.exists();

            h.btnLike.setTag(liked);
            h.btnLike.setImageResource(
                    liked ? R.drawable.heartfull : R.drawable.heartempty
            );

            h.btnLike.setEnabled(true);
        });

        h.btnLike.setOnClickListener(v -> {

            v.setEnabled(false);

            Boolean tag = (Boolean) v.getTag();
            boolean liked = tag != null && tag;

            if (liked) {

                v.setTag(false);
                h.btnLike.setImageResource(R.drawable.heartempty);

                r.likes = Math.max(0, r.likes - 1);
                h.txtLikes.setText(String.valueOf(r.likes));

                forumRef.child(String.valueOf(r.id))
                        .child("likes")
                        .setValue(r.likes);

                likeRef.removeValue().addOnCompleteListener(task -> v.setEnabled(true));

            } else {

                v.setTag(true);
                h.btnLike.setImageResource(R.drawable.heartfull);

                r.likes++;
                h.txtLikes.setText(String.valueOf(r.likes));

                forumRef.child(String.valueOf(r.id))
                        .child("likes")
                        .setValue(r.likes);

                String likedAt = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                ).format(new Date());

                likeRef.child("review_id").setValue(r.id);
                likeRef.child("user_id").setValue(userId);
                likeRef.child("liked_at").setValue(likedAt)
                        .addOnCompleteListener(task -> v.setEnabled(true));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ForumVH extends RecyclerView.ViewHolder {

        TextView txtTitle, txtUser, txtText, txtLikes, txtComments, txtView;
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
            txtComments = v.findViewById(R.id.txtComments);
            txtView = v.findViewById(R.id.txtView);
        }
    }
}