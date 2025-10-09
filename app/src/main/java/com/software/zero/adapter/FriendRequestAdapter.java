package com.software.zero.adapter;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.software.zero.R;
import com.software.zero.pojo.AddFriendMessage;

import java.util.Base64;
import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.MyViewHolder> {
    private List<AddFriendMessage> friendRequestList;
    private AdapterListener listener;

    public FriendRequestAdapter(List<AddFriendMessage> allRequest) {
        friendRequestList = allRequest;
    }

    public List<AddFriendMessage> getFriendRequestList() {
        return friendRequestList;
    }

    public interface AdapterListener {

        void acceptFriend(String phoneNumber, int position);
        void rejectFriend(String phoneNumber, int position);
    }

    public void setListener(AdapterListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendRequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_request, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestAdapter.MyViewHolder holder, int position) {
        AddFriendMessage data = friendRequestList.get(position);
        byte[] decodedString = Base64.getDecoder().decode(data.getProfile_picture());
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.iv_avatar.setImageBitmap(bitmap);
        holder.tv_username.setText(data.getUser_name());
        holder.btn_accept.setOnClickListener(v -> {
            listener.acceptFriend(friendRequestList.get(position).getPhone_number(), position);
        });
        holder.btn_reject.setOnClickListener(v -> {
            listener.rejectFriend(friendRequestList.get(position).getPhone_number(), position);
        });
    }

    @Override
    public int getItemCount() {
        return friendRequestList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_avatar;
        private TextView tv_username;
        private Button btn_accept;
        private Button btn_reject;
        
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            tv_username = itemView.findViewById(R.id.tv_username);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            btn_reject = itemView.findViewById(R.id.btn_reject);
        }

    }
}