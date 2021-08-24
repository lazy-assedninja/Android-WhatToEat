package me.lazy_assedninja.app.ui.promotion;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;

import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.ItemPromotionBinding;
import me.lazy_assedninja.app.vo.Promotion;
import me.lazy_assedninja.library.ui.BaseListAdapter;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class PromotionAdapter extends BaseListAdapter<Promotion, ItemPromotionBinding> {

    private final PromotionClickCallback promotionClickCallback;

    protected PromotionAdapter(ExecutorUtils executorUtils,
                               PromotionClickCallback promotionClickCallback) {
        super(executorUtils, new DiffUtil.ItemCallback<Promotion>() {
            @Override
            public boolean areItemsTheSame(@NonNull Promotion oldItem, @NonNull Promotion newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Promotion oldItem, @NonNull Promotion newItem) {
                return oldItem.getTitle().equals(newItem.getTitle());
            }
        });
        this.promotionClickCallback = promotionClickCallback;
    }

    @Override
    protected ItemPromotionBinding createBinding(ViewGroup parent) {
        return DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_promotion,
                parent,
                false
        );
    }

    @Override
    protected void bind(ItemPromotionBinding binding, Promotion item) {
        binding.setPromotion(item);
        binding.getRoot().setOnClickListener(v -> promotionClickCallback.onClick(item.getStoreID()));
    }
}