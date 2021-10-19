package me.lazy_assedninja.app.ui.store.comment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;

import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.CommentItemBinding;
import me.lazy_assedninja.app.ui.base.BaseListAdapter;
import me.lazy_assedninja.app.vo.Comment;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class CommentAdapter extends BaseListAdapter<Comment, CommentItemBinding> {

    private final DataBindingComponent dataBindingComponent;

    protected CommentAdapter(ExecutorUtils executorUtils, DataBindingComponent dataBindingComponent) {
        super(executorUtils, new DiffUtil.ItemCallback<Comment>() {
            @Override
            public boolean areItemsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
                return oldItem.getCreateTime().equals(newItem.getCreateTime());
            }
        });
        this.dataBindingComponent = dataBindingComponent;
    }

    @Override
    protected CommentItemBinding createBinding(ViewGroup parent) {
        return DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.comment_item,
                parent,
                false,
                dataBindingComponent
        );
    }

    @Override
    protected void bind(CommentItemBinding binding, Comment item) {
        binding.setComment(item);
    }
}