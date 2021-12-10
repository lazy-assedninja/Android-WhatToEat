package me.lazy_assedninja.what_to_eat.ui.promotion;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;

import me.lazy_assedninja.library.util.ExecutorUtil;
import me.lazy_assedninja.what_to_eat.R;
import me.lazy_assedninja.what_to_eat.databinding.PromotionItemBinding;
import me.lazy_assedninja.what_to_eat.ui.base.BaseListAdapter;
import me.lazy_assedninja.what_to_eat.vo.Promotion;

public class PromotionAdapter extends BaseListAdapter<Promotion, PromotionItemBinding> {

    private final PromotionCallback promotionCallback;

    protected PromotionAdapter(ExecutorUtil executorUtil,
                               PromotionCallback promotionCallback) {
        super(executorUtil, new DiffUtil.ItemCallback<Promotion>() {
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
        binding.getRoot().setOnClickListener(v -> promotionCallback.onClick(item.getId()));
    }
}