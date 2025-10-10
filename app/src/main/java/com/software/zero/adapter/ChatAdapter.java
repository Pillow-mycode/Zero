package com.software.zero.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.software.zero.R;
import com.software.zero.pojo.ChatHistory;
import com.software.zero.pojo.PeopleMessage;
import com.software.zero.response.data.FriendRequestData;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private PeopleMessage leftData, rightData;
    private Bitmap left_picture, right_picture;
    
    // 定义不同的视图类型
    private static final int TYPE_LEFT = 1;
    private static final int TYPE_RIGHT = 2;

    public ChatAdapter(PeopleMessage me, PeopleMessage other) {
        leftData = other;
        rightData = me;
        String profilePicture1 = leftData.getProfile_picture();
        byte[] decode1 = Base64.getDecoder().decode(profilePicture1);
        left_picture = BitmapFactory.decodeByteArray(decode1, 0, decode1.length);

        String profilePicture2 = rightData.getProfile_picture();
        byte[] decode2 = Base64.getDecoder().decode(profilePicture2);
        right_picture = BitmapFactory.decodeByteArray(decode2, 0, decode2.length);

    }

    public PeopleMessage getLeftData() {
        return leftData;
    }

    public void setLeftData(PeopleMessage leftData) {
        this.leftData = leftData;
    }

    public PeopleMessage getRightData() {
        return rightData;
    }

    public void setRightData(PeopleMessage rightData) {
        this.rightData = rightData;
    }

    private List<ChatHistory> list = new ArrayList<>();

    public ChatAdapter() {
    }

    public ChatAdapter(List<ChatHistory> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_LEFT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new LeftViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new RightViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatHistory chatHistory = list.get(position);
        if (holder instanceof LeftViewHolder) {
            // 绑定左侧消息数据
            ((LeftViewHolder) holder).tvMessage.setText(chatHistory.getMessage_content());
            ((LeftViewHolder) holder).avatar.setImageBitmap(left_picture);
        } else if (holder instanceof RightViewHolder) {
            // 绑定右侧消息数据
            ((RightViewHolder) holder).tvMessage.setText(chatHistory.getMessage_content());
            ((RightViewHolder) holder).avatar.setImageBitmap(right_picture);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatHistory chatHistory = list.get(position);
        if(chatHistory.getMine() == true) {
            return TYPE_RIGHT;
        } else return TYPE_LEFT;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // 添加新消息的方法
    public void addMessage(ChatHistory chatHistory) {
        list.add(chatHistory);
        notifyItemInserted(list.size() - 1);
    }

    public void loadMessage(List<ChatHistory> list) {
        this.list = list;
        notifyItemInserted(list.size() - 1);
    }

    // 左侧消息的 ViewHolder
    public static class LeftViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;
        private ImageView avatar;
        
        public LeftViewHolder(@NonNull View itemView) {
            super(itemView);
            // 遵循 ViewHolder 构造函数规范，先调用 super(itemView)
            tvMessage = itemView.findViewById(R.id.tvMessage);
            avatar = itemView.findViewById(R.id.avatar_left);
        }
    }
    
    // 右侧消息的 ViewHolder
    public static class RightViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;
        private ImageView avatar;
        
        public RightViewHolder(@NonNull View itemView) {
            super(itemView);
            // 遵循 ViewHolder 构造函数规范，先调用 super(itemView)
            tvMessage = itemView.findViewById(R.id.tvMessage);
            avatar = itemView.findViewById(R.id.avatar_right);

        }
    }
}