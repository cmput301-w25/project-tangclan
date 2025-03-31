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
    private handleFollowRequest listener;

    public interface handleFollowRequest {
        void onRequestHandled(int position, int mode);
    }

    /**
     * Constructor with listener for request handling
     * @param followRequests List of follow request UIDs
     * @param followingBook FollowingBook instance
     * @param listener Callback for handling requests
     */
    public FollowRequestAdapter(List<String> followRequests, FollowingBook followingBook, handleFollowRequest listener) {
        this.followRequests = followRequests;
        this.followingBook = followingBook;
        this.listener = listener;
    }

    /**
     * Constructor without listener (maintains backward compatibility)
     * @param followRequests List of follow request UIDs
     * @param followingBook FollowingBook instance
     */
    public FollowRequestAdapter(List<String> followRequests, FollowingBook followingBook) {
        this(followRequests, followingBook, null);
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
            if (listener != null) {
                listener.onRequestHandled(position, 1);  // accept mode
            } else {
                // Fallback to direct handling if no listener
                followingBook.acceptFollowRequest(uid);
                followRequests.remove(uid);
                notifyDataSetChanged();
            }
        });

        // Decline button logic
        holder.buttonDecline.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRequestHandled(position, 2);  // decline mode
            } else {
                // Fallback to direct handling if no listener
                followingBook.declineFollowRequest(uid);
                followRequests.remove(uid);
                notifyDataSetChanged();
            }
        });

        holder.textViewUsername.setOnClickListener(v -> {
            listener.onRequestHandled(position, 3);  // view profile mode
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