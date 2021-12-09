package me.lazy_assedninja.what_to_eat.ui.store.reservation.reserve;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.what_to_eat.R;
import me.lazy_assedninja.what_to_eat.databinding.ReserveFragmentBinding;
import me.lazy_assedninja.what_to_eat.util.AutoClearedValue;
import me.lazy_assedninja.what_to_eat.vo.Reservation;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.what_to_eat.vo.Status;
import me.lazy_assedninja.library.ui.BaseBottomSheetDialogFragment;

@AndroidEntryPoint
public class ReserveFragment extends BaseBottomSheetDialogFragment {

    private AutoClearedValue<ReserveFragmentBinding> binding;
    private ReserveViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ReserveFragmentBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.reserve_fragment,
                container,
                false
        );
        this.binding = new AutoClearedValue<>(this, binding);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReserveViewModel.class);

        initView();
        initData();
    }

    private void initView() {
        Calendar calendar = Calendar.getInstance();
        binding.get().btDateChoose.setOnClickListener(v -> {
            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(getActivity(),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        EditText editText = binding.get().tilDate.getEditText();
                        if (editText == null) return;
                        String date = (monthOfYear + 1) + "-" + dayOfMonth;
                        editText.setText(date);
                    }, mYear, mMonth, mDay).show();
        });
        binding.get().btTimeChoose.setOnClickListener(v -> {
            int mHour = calendar.get(Calendar.HOUR_OF_DAY);
            int mMinute = calendar.get(Calendar.MINUTE);

            new TimePickerDialog(getActivity(),
                    (view, hourOfDay, minute) -> {
                        EditText editText = binding.get().tilTime.getEditText();
                        if (editText == null) return;
                        String time = hourOfDay + ":" + minute;
                        editText.setText(time);
                    }, mHour, mMinute, false).show();
        });
        binding.get().btReserve.setOnClickListener(v -> {
            dismissKeyboard(v);
            EditText etName = binding.get().tilName.getEditText();
            EditText etPhone = binding.get().tilPhone.getEditText();
            EditText etAmount = binding.get().tilAmount.getEditText();
            EditText etDate = binding.get().tilDate.getEditText();
            EditText etTime = binding.get().tilTime.getEditText();
            if (etName == null || etPhone == null || etAmount == null || etDate == null ||
                    etTime == null) return;

            // Clear errors
            binding.get().tilName.setError(null);
            binding.get().tilPhone.setError(null);
            binding.get().tilAmount.setError(null);
            binding.get().tilDate.setError(null);
            binding.get().tilTime.setError(null);

            String name = etName.getText().toString();
            String phone = etPhone.toString();
            String amount = etAmount.toString();
            String date = etDate.toString();
            String time = etTime.toString();
            if (name.isEmpty()) {
                binding.get().tilName.setError(getString(R.string.error_name_can_not_be_null));
            } else if (phone.isEmpty()) {
                binding.get().tilPhone.setError(getString(R.string.error_phone_can_not_be_null));
            } else if (amount.isEmpty()) {
                binding.get().tilAmount.setError(getString(R.string.error_amount_can_not_be_null));
            } else if (date.isEmpty()) {
                binding.get().tilDate.setError(getString(R.string.error_date_can_not_be_null));
            } else if (time.isEmpty()) {
                binding.get().tilTime.setError(getString(R.string.error_time_can_not_be_null));
            } else {
                viewModel.reserve(new Reservation(name, phone, amount, date + " " + time));
            }
        });

        binding.get().setLifecycleOwner(getViewLifecycleOwner());
        binding.get().setResult(viewModel.result);
    }

    private void initData() {
        if (getArguments() == null) return;
        int id = getArguments().getInt("store_id");
        viewModel.setId(id);

        viewModel.result.observe(getViewLifecycleOwner(), event -> {
            Resource<Result> resultResource = event.getContentIfNotHandled();
            if (resultResource == null) return;

            if (resultResource.getStatus().equals(Status.SUCCESS)) {
                showToast(resultResource.getData().getResult());
                dismiss();
            } else if (resultResource.getStatus().equals(Status.ERROR)) {
                showToast(resultResource.getMessage());
            }
        });
    }
}