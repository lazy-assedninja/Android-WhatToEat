package me.lazy_assedninja.app.ui.store;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;

import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.ItemStoreBinding;
import me.lazy_assedninja.app.vo.Favorite;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.library.ui.BaseListAdapter;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class StoreAdapter extends BaseListAdapter<Store, ItemStoreBinding> {

    private final int userID;

    private final FavoriteClickCallback favoriteClickCallback;
    private final InformationClickCallback informationClickCallback;

    public StoreAdapter(ExecutorUtils executorUtils,
                        int userID,
                        FavoriteClickCallback favoriteClickCallback,
                        InformationClickCallback informationClickCallback) {
        super(executorUtils, new DiffUtil.ItemCallback<Store>() {
            @Override
            public boolean areItemsTheSame(@NonNull Store oldItem, @NonNull Store newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Store oldItem, @NonNull Store newItem) {
                return oldItem.getName().equals(newItem.getName());
            }
        });
        this.userID = userID;
        this.favoriteClickCallback = favoriteClickCallback;
        this.informationClickCallback = informationClickCallback;
    }

    @Override
    protected ItemStoreBinding createBinding(ViewGroup parent) {
        return DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_store,
                parent,
                false
        );
    }

    @Override
    protected void bind(ItemStoreBinding binding, Store item) {
        binding.setStore(item);
        binding.btFavorite.setOnClickListener(v -> {
            item.changeFavoriteStatus();
            favoriteClickCallback.onClick(binding, new Favorite(userID, item.getId(), item.isFavorite()));
        });
        binding.btStoreInformation.setOnClickListener(v -> informationClickCallback.onClick(binding, item.getId()));
    }
}