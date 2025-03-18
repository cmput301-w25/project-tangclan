package com.example.tangclan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProfileRecyclerView extends RecyclerView.Adapter<ProfileRecyclerView.ViewHolder> {

    Context context;
    ArrayList<Profile> profileArrayList = new ArrayList<>();
    public ProfileRecyclerView(Context context, ArrayList<Profile> arrayList) {
        this.context = context;
        this.profileArrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.profileImg.setImageBitmap(profileArrayList.get(position).getPhotoBitmap());  // getPhoto is not yet a thing
        holder.displayNameView.setText(profileArrayList.get(position).getDisplayName());
        holder.usernameView.setText(profileArrayList.get(position).getUsername());

        holder.profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewOtherProfile.class);
                intent.putExtra("username",profileArrayList.get(position).getUsername());
                intent.putExtra("display name",profileArrayList.get(position).getDisplayName());
                intent.putExtra("profile image",profileArrayList.get(position).getPhotoByteArray());  // getPhoto needs ways to return bitmap or byte array maybe both at once
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImg;
        TextView displayNameView,usernameView;
        CardView profileCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.user_pfp);
            displayNameView = itemView.findViewById(R.id.user_displayname);
            usernameView = itemView.findViewById(R.id.user_username);
        }
    }
}
