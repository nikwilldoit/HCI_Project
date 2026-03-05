package com.example.phasmatic.ui.Forum;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.phasmatic.R;
import com.example.phasmatic.data.model.ForumReview;


import java.util.ArrayList;
import java.util.List;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ForumVH> {

    private final List<ForumReview> items;

    public ForumAdapter(List<ForumReview> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ForumVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forum_review, parent, false);
        return new ForumVH(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ForumVH h, int pos) {
        ForumReview r = items.get(pos);
        String title = (r.type.equals("erasmus") ? "Erasmus · " : "Master · ") + r.university;
        h.txtTitle.setText(title);
        h.txtUser.setText("by " + r.userName);
        h.ratingBar.setRating(r.rating);
        h.txtText.setText(r.text);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ForumVH extends RecyclerView.ViewHolder {
        TextView txtTitle, txtUser, txtText;
        RatingBar ratingBar;
        ForumVH(@NonNull View v) {
            super(v);
            txtTitle = v.findViewById(R.id.txtTitle);
            txtUser = v.findViewById(R.id.txtUser);
            txtText = v.findViewById(R.id.txtText);
            ratingBar = v.findViewById(R.id.ratingBar);
        }
    }
}
