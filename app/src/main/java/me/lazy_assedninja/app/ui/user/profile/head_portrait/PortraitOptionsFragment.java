package me.lazy_assedninja.app.ui.user.profile.head_portrait;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.databinding.PortraitOptionsFragmentBinding;
import me.lazy_assedninja.library.ui.BaseBottomSheetDialogFragment;

public class PortraitOptionsFragment extends BaseBottomSheetDialogFragment {

    private PortraitOptionsFragmentBinding binding;

    private final PortraitOptionsCallback callback;

    public PortraitOptionsFragment(PortraitOptionsCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.portrait_options_fragment,
                container,
                false
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
    }

    private void initView() {
        binding.btTakePhotos.setOnClickListener(v -> {
            callback.takePicture();
            dismiss();
        });
        binding.btFromGallery.setOnClickListener(v -> {
            callback.getContent();
            dismiss();
        });
    }
}
