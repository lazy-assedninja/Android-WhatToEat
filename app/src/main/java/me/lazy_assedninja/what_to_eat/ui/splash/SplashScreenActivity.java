package me.lazy_assedninja.what_to_eat.ui.splash;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import me.lazy_assedninja.what_to_eat.ui.index.MainActivity;
import me.lazy_assedninja.library.ui.BaseActivity;

public class SplashScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}