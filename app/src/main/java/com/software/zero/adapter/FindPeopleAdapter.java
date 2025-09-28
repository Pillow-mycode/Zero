// FindPeopleAdapter.java

package com.software.zero.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.software.zero.R;
import com.software.zero.response.data.FindPeopleData;

import java.util.List;
import java.util.Objects;

public class FindPeopleAdapter extends RecyclerView.Adapter<FindPeopleAdapter.ViewHolder> {
    private List<FindPeopleData.SearchMessage> list;

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
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_profile);
            textView = itemView.findViewById(R.id.tv_search_result);
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
