package me.lazy_assedninja.app.ui.reservation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;

import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.ItemReservationBinding;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.library.ui.BaseListAdapter;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class ReservationAdapter extends BaseListAdapter<Reservation, ItemReservationBinding> {

    private final ReservationClickCallback reservationClickCallback;

    protected ReservationAdapter(ExecutorUtils executorUtils, ReservationClickCallback reservationClickCallback) {
        super(executorUtils, new DiffUtil.ItemCallback<Reservation>() {
            @Override
            public boolean areItemsTheSame(@NonNull Reservation oldItem, @NonNull Reservation newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Reservation oldItem, @NonNull Reservation newItem) {
                return oldItem.getTime().equals(newItem.getTime());
            }
        });
        this.reservationClickCallback = reservationClickCallback;
    }

    @Override
    protected ItemReservationBinding createBinding(ViewGroup parent) {
        return DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_reservation,
                parent,
                false
        );
    }

    @Override
    protected void bind(ItemReservationBinding binding, Reservation item) {
        binding.setReservation(item);
        binding.floatingActionButton.setOnClickListener(v ->
                reservationClickCallback.onClick(item));
    }
}
