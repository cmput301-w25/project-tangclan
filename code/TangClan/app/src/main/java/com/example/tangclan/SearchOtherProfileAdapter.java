package com.example.tangclan;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchOtherProfileAdapter extends RecyclerView.Adapter<SearchOtherProfileAdapter.ViewHolder> {

    // creating a variable for array list and context.
    private ArrayList<Profile> ProfileArrayList;
    private Context context;
    private SelectProfileListener listener;

    public interface SelectProfileListener {
        void onItemClicked(Profile profile);
    }


    // creating a constructor for our variables.
    public SearchOtherProfileAdapter(ArrayList<Profile> ProfileArrayList, Context context,SelectProfileListener listener) {
        this.ProfileArrayList = ProfileArrayList;
        this.context=context;
        this.listener=listener;
    }



    public void filterList(ArrayList<Profile> filterlist) {
        // below line is to add our filtered
        // list in our profile array list.
        ProfileArrayList = filterlist;
        //notify adapter datalist has changed
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchOtherProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // below line is to inflate our layout.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item_foryou, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchOtherProfileAdapter.ViewHolder holder, int position) {
        // setting data to our views of recycler view.
        Profile model = ProfileArrayList.get(position);
        holder.ProfileUsernameForYouPage.setText(model.getUsername());
        holder.ProfileDisplayNameForYouPage.setText(model.getDisplayName());


        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                listener.onItemClicked(ProfileArrayList.get(holder.getAdapterPosition()));
//                Intent intent =new Intent(view.getContext(), ProfilePageActivity.class);
//                intent.putExtra("KeySearchProfile", ProfileArrayList.get(holder.getAdapterPosition()));
//                view.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return ProfileArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our views.
        private final TextView ProfileUsernameForYouPage;
        private final CardView cardview;
        private final TextView ProfileDisplayNameForYouPage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our views with their ids.
            ProfileUsernameForYouPage = itemView.findViewById(R.id.ProfileUsernameForYou);
            ProfileDisplayNameForYouPage = itemView.findViewById(R.id.ProfileDisplayNameForYou);
            cardview = itemView.findViewById(R.id.cardview);
        }
    }
}