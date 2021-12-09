package me.lazy_assedninja.what_to_eat.ui.store.reservation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;

import me.lazy_assedninja.what_to_eat.R;
import me.lazy_assedninja.what_to_eat.databinding.ReservationItemBinding;
import me.lazy_assedninja.what_to_eat.ui.base.BaseListAdapter;
import me.lazy_assedninja.what_to_eat.vo.Reservation;
import me.lazy_assedninja.library.util.ExecutorUtil;

public class ReservationAdapter extends BaseListAdapter<Reservation, ReservationItemBinding> {

    private final ReservationCallback reservationCallback;

    protected ReservationAdapter(ExecutorUtil executorUtil, ReservationCallback reservationCallback) {
        super(executorUtil, new DiffUtil.ItemCallback<Reservation>() {
            @Override
            public boolean areItemsTheSame(@NonNull Reservation oldItem, @NonNull Reservation newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Reservation oldItem, @NonNull Reservation newItem) {
                return oldItem.getTime().equals(newItem.getTime());
            }
        });
        this.reservationCallback = reservationCallback;
    }

    @Override
    protected ReservationItemBinding createBinding(ViewGroup parent) {
        return DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.reservation_item,
                parent,
                false
        );
    }

    @Override
    protected void bind(ReservationItemBinding binding, Reservation item) {
        binding.setReservation(item);
        binding.floatingActionButton.setOnClickListener(v -> reservationCallback.onCompleteClick(item));
    }
}