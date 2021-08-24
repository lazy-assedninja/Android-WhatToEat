package me.lazy_assedninja.library.ui;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    /**
     * Dismiss soft keyboard.
     *
     * @param view view
     */
    protected void dismissKeyboard(View view) {
        if (view != null && getActivity() != null) {
            InputMethodManager imm =
                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }

    /**
     * Show toast with resource ID.
     *
     * @param textId string's resource ID
     */
    protected void showToast(int textId) {
        showToast(getString(textId));
    }

    /**
     * Show toast with string.
     *
     * @param text string
     */
    protected void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
}
