package me.lazy_assedninja.app.ui.promotion;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;

import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.PromotionItemBinding;
import me.lazy_assedninja.app.ui.base.BaseListAdapter;
import me.lazy_assedninja.app.vo.Promotion;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class PromotionAdapter extends BaseListAdapter<Promotion, PromotionItemBinding> {

    private final PromotionCallback promotionCallback;

    protected PromotionAdapter(ExecutorUtils executorUtils,
                               PromotionCallback promotionCallback) {
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
        this.promotionCallback = promotionCallback;
    }

    @Override
    protected PromotionItemBinding createBinding(ViewGroup parent) {
        return DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.promotion_item,
                parent,
                false
        );
    }

    @Override
    protected void bind(PromotionItemBinding binding, Promotion item) {
        binding.setPromotion(item);
        binding.getRoot().setOnClickListener(v -> promotionCallback.onClick(item.getStoreID()));
    }
}