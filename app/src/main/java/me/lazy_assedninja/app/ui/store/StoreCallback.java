package me.lazy_assedninja.app.ui.store;

import me.lazy_assedninja.app.databinding.StoreItemBinding;
import me.lazy_assedninja.app.vo.Favorite;

/**
 * Generic interface for favorite & information buttons.
 */
public interface StoreCallback {

    void onFavoriteClick(int storeID, boolean isFavorite);

    void onInformationClick(StoreItemBinding binding);
}