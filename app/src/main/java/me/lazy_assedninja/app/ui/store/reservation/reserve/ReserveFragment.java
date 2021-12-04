package me.lazy_assedninja.app.ui.store.reservation.reserve;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.ReserveFragmentBinding;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Status;
import me.lazy_assedninja.library.ui.BaseBottomSheetDialogFragment;

@AndroidEntryPoint
public class ReserveFragment extends BaseBottomSheetDialogFragment {

    private ReserveFragmentBinding binding;
    private ReserveViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.reserve_fragment,
                container,
                false
        );
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
        binding.btDateChoose.setOnClickListener(v -> {
            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(getActivity(),
                    (DatePickerDialog.OnDateSetListener) (view, year, monthOfYear, dayOfMonth) -> {
                        if (binding.tilDate.getEditText() == null) return;
                        String date = (monthOfYear + 1) + "-" + dayOfMonth;
                        binding.tilDate.getEditText().setText(date);
                    }, mYear, mMonth, mDay).show();
        });
        binding.btTimeChoose.setOnClickListener(v -> {
            int mHour = calendar.get(Calendar.HOUR_OF_DAY);
            int mMinute = calendar.get(Calendar.MINUTE);

            new TimePickerDialog(getActivity(),
                    (TimePickerDialog.OnTimeSetListener) (view, hourOfDay, minute) -> {
                        if (binding.tilTime.getEditText() == null) return;
                        String time = hourOfDay + ":" + minute;
                        binding.tilTime.getEditText().setText(time);
                    }, mHour, mMinute, false).show();
        });
        binding.btReserve.setOnClickListener(v -> {
            dismissKeyboard(v);
            if (binding.tilName.getEditText() == null || binding.tilPhone.getEditText() == null ||
                    binding.tilAmount.getEditText() == null || binding.tilDate.getEditText() == null ||
                    binding.tilTime.getEditText() == null)
                return;

            // Clear errors
            binding.tilName.setError(null);
            binding.tilPhone.setError(null);
            binding.tilAmount.setError(null);
            binding.tilDate.setError(null);
            binding.tilTime.setError(null);

            String name = binding.tilName.getEditText().getText().toString();
            String phone = binding.tilPhone.getEditText().getText().toString();
            String amount = binding.tilAmount.getEditText().getText().toString();
            String date = binding.tilDate.getEditText().getText().toString();
            String time = binding.tilTime.getEditText().getText().toString();
            if (name.isEmpty()) {
                binding.tilName.setError(getString(R.string.error_name_can_not_be_null));
            } else if (phone.isEmpty()) {
                binding.tilPhone.setError(getString(R.string.error_phone_can_not_be_null));
            } else if (amount.isEmpty()) {
                binding.tilAmount.setError(getString(R.string.error_amount_can_not_be_null));
            } else if (date.isEmpty()) {
                binding.tilDate.setError(getString(R.string.error_date_can_not_be_null));
            } else if (time.isEmpty()) {
                binding.tilTime.setError(getString(R.string.error_time_can_not_be_null));
            } else {
                viewModel.reserve(name, phone, amount, date + " " + time);
            }
        });

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setResult(viewModel.result);
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