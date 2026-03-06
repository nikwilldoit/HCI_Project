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

import java.util.List;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ForumVH> {

    public interface OnReviewClickListener {
        void onReviewClick(ForumReview review);
    }


    private final List<ForumReview> items;
    private final OnReviewClickListener listener;

    public ForumAdapter(List<ForumReview> items, OnReviewClickListener listener) {
        this.items = items;
        this.listener = listener;
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

        String title = (r.type.equals("erasmus") ? "Erasmus · " : "Master · ") + r.university;
        h.txtTitle.setText(title);
        h.txtUser.setText("by " + r.user_name);
        h.ratingBar.setRating(r.rating);
        h.txtText.setText(r.text);
        h.txtLikes.setText(String.valueOf(r.likes));

        h.btnLike.setImageResource(R.drawable.heartempty);
        h.btnLike.setTag(false);

        h.btnLike.setOnClickListener(v -> {
            boolean liked = (boolean) v.getTag();
            if (liked) {
                h.btnLike.setImageResource(R.drawable.heartempty);
                r.likes--;
                v.setTag(false);
            } else {
                h.btnLike.setImageResource(R.drawable.heartfull);
                r.likes++;
                v.setTag(true);
            }
            h.txtLikes.setText(String.valueOf(r.likes));
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
