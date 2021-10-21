package me.lazy_assedninja.app.ui.store.post;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;

import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.PostItemBinding;
import me.lazy_assedninja.app.ui.base.BaseListAdapter;
import me.lazy_assedninja.app.vo.Post;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class PostAdapter extends BaseListAdapter<Post, PostItemBinding> {

    protected PostAdapter(ExecutorUtils executorUtils) {
        super(executorUtils, new DiffUtil.ItemCallback<Post>() {
            @Override
            public boolean areItemsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
                return oldItem.getCreateTime().equals(newItem.getCreateTime());
            }
        });
    }

    @Override
    protected PostItemBinding createBinding(ViewGroup parent) {
        return DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.post_item,
                parent,
                false
        );
    }

    @Override
    protected void bind(PostItemBinding binding, Post item) {
        binding.setPost(item);
    }
}