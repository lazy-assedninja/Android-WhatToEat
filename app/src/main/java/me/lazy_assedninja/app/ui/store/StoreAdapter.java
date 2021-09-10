package me.lazy_assedninja.app.ui.store;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;

import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.StoreItemBinding;
import me.lazy_assedninja.app.ui.base.BaseListAdapter;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class StoreAdapter extends BaseListAdapter<Store, StoreItemBinding> {

    private final DataBindingComponent dataBindingComponent;

    private final StoreCallback storeCallback;

    public StoreAdapter(ExecutorUtils executorUtils, DataBindingComponent dataBindingComponent,
                        StoreCallback storeCallback) {
        super(executorUtils, new DiffUtil.ItemCallback<Store>() {
            @Override
            public boolean areItemsTheSame(@NonNull Store oldItem, @NonNull Store newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Store oldItem, @NonNull Store newItem) {
                return oldItem.getName().equals(newItem.getName()) &&
                        oldItem.getUpdateTime().equals(newItem.getUpdateTime());
            }
        });
        this.dataBindingComponent = dataBindingComponent;
        this.storeCallback = storeCallback;
    }

    @Override
    protected StoreItemBinding createBinding(ViewGroup parent) {
        return DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.store_item,
                parent,
                false,
                dataBindingComponent
        );
    }

    @Override
    protected void bind(StoreItemBinding binding, Store item) {
        binding.setStore(item);
        binding.btFavorite.setOnClickListener(v ->
                storeCallback.onFavoriteClick(item.getId(), item.isFavorite()));
        binding.btStoreInformation.setOnClickListener(v -> storeCallback.onInformationClick(binding));
    }
}