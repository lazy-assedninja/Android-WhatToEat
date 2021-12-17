package me.lazy_assedninja.what_to_eat.ui.store;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.util.List;

import me.lazy_assedninja.library.util.ExecutorUtil;
import me.lazy_assedninja.what_to_eat.R;
import me.lazy_assedninja.what_to_eat.databinding.StoreItemBinding;
import me.lazy_assedninja.what_to_eat.ui.base.BaseViewHolder;
import me.lazy_assedninja.what_to_eat.vo.Store;

public class StoreAdapter extends ListAdapter<Store, BaseViewHolder<StoreItemBinding>> {

    private final DataBindingComponent dataBindingComponent;
    private final StoreCallback storeCallback;

    public StoreAdapter(ExecutorUtil executorUtil, DataBindingComponent dataBindingComponent,
                        StoreCallback storeCallback, boolean needCheck) {
        super(new AsyncDifferConfig.Builder<>(new DiffUtil.ItemCallback<Store>() {
            @Override
            public boolean areItemsTheSame(@NonNull Store oldItem, @NonNull Store newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Store oldItem, @NonNull Store newItem) {
                return oldItem.getName().equals(newItem.getName()) &&
                        (!needCheck || oldItem.getUpdateTime().equals(newItem.getUpdateTime()));
            }
        }).setBackgroundThreadExecutor(executorUtil.diskIO()).build());
        this.dataBindingComponent = dataBindingComponent;
        this.storeCallback = storeCallback;
    }

    @NonNull
    @Override
    public BaseViewHolder<StoreItemBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StoreItemBinding binding = createBinding(parent);
        return new BaseViewHolder<>(binding);
    }

    private StoreItemBinding createBinding(ViewGroup parent) {
        return DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.store_item,
                parent,
                false,
                dataBindingComponent
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<StoreItemBinding> holder, int position) {
        bind(holder.binding, getItem(position), position);
        holder.binding.executePendingBindings();
    }

    private void bind(StoreItemBinding binding, Store item, int position) {
        binding.setStore(item);
        binding.ivFavorite.setEnabled(true);
        binding.ivFavorite.setOnClickListener(v -> {
            storeCallback.onFavoriteClick(item.getId(), position, item.isFavorite());
            binding.ivFavorite.setEnabled(false);
        });
        binding.item.setOnClickListener(v -> storeCallback.onInformationClick(binding, position));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<StoreItemBinding> holder, int position,
                                 @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            boolean isFavorite = (boolean) payloads.get(0);
            getItem(position).setFavorite(isFavorite);
            bind(holder.binding, getItem(position), position);
            holder.binding.executePendingBindings();
        }
    }
}