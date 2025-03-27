package com.example.tangclan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FollowRequestAdapter extends RecyclerView.Adapter<FollowRequestAdapter.ViewHolder> {

    private List<String> followRequests;
    private FollowingBook followingBook;

    public FollowRequestAdapter(List<String> followRequests, FollowingBook followingBook) {
        this.followRequests = followRequests;
        this.followingBook = followingBook;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follow_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String uid = followRequests.get(position);
        holder.textViewUsername.setText(uid);

        // Accept button logic
        holder.buttonAccept.setOnClickListener(v -> {
            followingBook.acceptFollowRequest(uid);
            followRequests.remove(uid);
            notifyDataSetChanged();
        });

        // Decline button logic
        holder.buttonDecline.setOnClickListener(v -> {
            followingBook.declineFollowRequest(uid);
            followRequests.remove(uid);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return followRequests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        Button buttonAccept, buttonDecline;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            buttonAccept = itemView.findViewById(R.id.buttonAccept);
            buttonDecline = itemView.findViewById(R.id.buttonDecline);
        }
    }
}
