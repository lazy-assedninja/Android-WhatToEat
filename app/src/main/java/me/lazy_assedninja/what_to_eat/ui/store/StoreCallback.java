package me.lazy_assedninja.what_to_eat.ui.store;

import me.lazy_assedninja.what_to_eat.databinding.StoreItemBinding;

/**
 * Generic interface for favorite & information buttons.
 */
public interface StoreCallback {

    void onFavoriteClick(int storeID, int position, boolean isFavorite);

    void onInformationClick(StoreItemBinding binding, int position);
}