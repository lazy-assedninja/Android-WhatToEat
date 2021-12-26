package me.lazy_assedninja.library.ui;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    /**
     * Dismiss soft keyboard.
     *
     * @param view view
     */
    protected void dismissKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }
}