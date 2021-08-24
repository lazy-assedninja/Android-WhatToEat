package me.lazy_assedninja.app.ui.store;

import me.lazy_assedninja.app.databinding.ItemStoreBinding;
import me.lazy_assedninja.app.vo.Favorite;

public interface FavoriteClickCallback {

    void onClick(ItemStoreBinding binding, Favorite favorite);
}