// FindPeopleAdapter.java

package com.software.zero.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.software.util.share_preference.EncryptedPrefsHelper;
import com.software.zero.R;
import com.software.zero.response.data.FindPeopleData;

import java.util.List;
import java.util.Objects;

public class FindPeopleAdapter extends RecyclerView.Adapter<FindPeopleAdapter.ViewHolder> {
    private List<FindPeopleData.SearchMessage> list;
    private OnAddUserListener listener;
    private EncryptedPrefsHelper encryptedPrefsHelper;

    public interface OnAddUserListener {
        void addUser(String phoneNumber, ViewHolder holder);
    }

    public void setOnAddUserListener(OnAddUserListener listener) {
        this.listener = listener;
    }

    public void updateAdapter(List<FindPeopleData.SearchMessage> newList) {
        if (list == null) {
            list = newList;
            if (newList != null) {
                notifyItemRangeInserted(0, newList.size());
            }
            return;
        }

        if (newList == null) {
            int oldSize = list.size();
            list.clear();
            notifyItemRangeRemoved(0, oldSize);
            return;
        }

        // 使用 DiffUtil 计算差异并局部刷新
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new SearchMessageDiffCallback(list, newList));
        list.clear();
        list.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        encryptedPrefsHelper = EncryptedPrefsHelper.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FindPeopleData.SearchMessage searchMessage = list.get(position);
        try {
            byte[] decode = Base64.decode(searchMessage.getProfile_picture(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
            holder.imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            holder.imageView.setImageResource(R.drawable.ic_boy); // 设置默认图片防止崩溃
            e.printStackTrace();
        }
        holder.textView.setText(searchMessage.getUser_name());
        holder.button.setOnClickListener(v -> {
            listener.addUser(list.get(position).getPhone_number(), holder);
        });
        if(encryptedPrefsHelper.getBoolean(searchMessage.getPhone_number())) {
            holder.button.setVisibility(GONE);
            holder.requested.setVisibility(VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public Button button;
        public TextView requested;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_profile);
            textView = itemView.findViewById(R.id.tv_search_result);
            button = itemView.findViewById(R.id.bt_add);
            requested = itemView.findViewById(R.id.tv_requested);
        }
    }

    // DiffUtil.Callback 实现类
    static class SearchMessageDiffCallback extends DiffUtil.Callback {
        private final List<FindPeopleData.SearchMessage> oldList;
        private final List<FindPeopleData.SearchMessage> newList;

        public SearchMessageDiffCallback(List<FindPeopleData.SearchMessage> oldList,
                                         List<FindPeopleData.SearchMessage> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return Objects.equals(oldList.get(oldItemPosition).getPhone_number(),
                    newList.get(newItemPosition).getPhone_number());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return Objects.equals(oldList.get(oldItemPosition), newList.get(newItemPosition));
        }
    }
}
