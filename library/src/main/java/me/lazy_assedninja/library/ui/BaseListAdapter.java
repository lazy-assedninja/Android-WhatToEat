package me.lazy_assedninja.library.ui;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import me.lazy_assedninja.library.utils.ExecutorUtils;

/**
 * A generic RecyclerView adapter that uses Data Binding & DiffUtil.
 *
 * @param <T> Type of the items in the list
 * @param <V> The type of the ViewDataBinding
 */
public abstract class BaseListAdapter<T, V extends ViewDataBinding> extends ListAdapter<T, BaseViewHolder<V>> {

    protected BaseListAdapter(ExecutorUtils executorUtils, @NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(new AsyncDifferConfig.Builder<>(diffCallback)
                .setBackgroundThreadExecutor(executorUtils.diskIO())
                .build());
    }

    @NonNull
    @Override
    public BaseViewHolder<V> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        V binding = createBinding(parent);
        return new BaseViewHolder<>(binding);
    }

    protected abstract V createBinding(ViewGroup parent);

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<V> holder, int position) {
        bind(holder.binding, getItem(position));
        holder.binding.executePendingBindings();
    }

    protected abstract void bind(V binding, T item);
}
