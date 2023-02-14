package com.erenuylar.firebaseapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erenuylar.firebaseapp.databinding.RecyleRowBinding;
import com.erenuylar.firebaseapp.model.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Postholder> {

    ArrayList<Post> postArrayList;

    public PostAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public Postholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyleRowBinding recyleRowBinding = RecyleRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Postholder(recyleRowBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Postholder holder, int position) {
        Picasso.get().load(postArrayList.get(position).downloadUrlProfile).into(holder.recyleRowBinding.imageViewProfile);
        holder.recyleRowBinding.nameText.setText(postArrayList.get(position).uName + " " + postArrayList.get(position).uSname);
        holder.recyleRowBinding.dateText.setText(postArrayList.get(position).date);
        Picasso.get().load(postArrayList.get(position).downloadUrl).into(holder.recyleRowBinding.imageViewPost);
        holder.recyleRowBinding.commentText.setText(postArrayList.get(position).comment);
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    public class Postholder extends RecyclerView.ViewHolder {
        RecyleRowBinding recyleRowBinding;

        public Postholder(RecyleRowBinding recyleRowBinding) {
            super(recyleRowBinding.getRoot());
            this.recyleRowBinding = recyleRowBinding;
        }
    }
}
