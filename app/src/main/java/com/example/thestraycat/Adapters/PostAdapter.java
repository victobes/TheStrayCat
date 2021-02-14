package com.example.thestraycat.Adapters;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thestraycat.Models.Post;
import com.example.thestraycat.Activities.PostActivity;
import com.example.thestraycat.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;


// Адаптер заметок пользователей.

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mContext;
    List<Post> postList;

    public PostAdapter(Context mContext, List<Post> postList) {
        this.mContext = mContext;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.note_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.postViewTitle.setText(postList.get(position).getTitle());
        Glide.with(mContext).load(postList.get(position).getPicture()).into(holder.postViewImage);
        Glide.with(mContext).load(postList.get(position).getUserPhoto()).into(holder.postViewProfileImage);

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView postViewTitle;
        ImageView postViewImage;
        ShapeableImageView postViewProfileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            postViewTitle = itemView.findViewById(R.id.note_view_title);
            postViewImage = itemView.findViewById(R.id.note_view_image);
            postViewProfileImage = itemView.findViewById(R.id.note_view_profile_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PostActivity.class);
                    int position = getAdapterPosition();

                    intent.putExtra("noteTitle", postList.get(position).getTitle());
                    intent.putExtra("noteImage", postList.get(position).getPicture());
                    intent.putExtra("noteDescription", postList.get(position).getDescription());
                    intent.putExtra("noteKey", postList.get(position).getNoteKey());
                    intent.putExtra("noteUserPhoto", postList.get(position).getUserPhoto());
                    intent.putExtra("noteUserName", postList.get(position).getUserName());

                    long timestamp  = (long) postList.get(position).getTimeStamp();
                    intent.putExtra("postDate",timestamp) ;

                    mContext.startActivity(intent);

                }
            });
        }
    }
}