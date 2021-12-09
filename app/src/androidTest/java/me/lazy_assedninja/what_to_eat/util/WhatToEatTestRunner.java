package me.lazy_assedninja.what_to_eat.util;

import android.app.Application;
import android.content.Context;

import androidx.test.runner.AndroidJUnitRunner;

import dagger.hilt.android.testing.HiltTestApplication;

@SuppressWarnings("unused")
public class WhatToEatTestRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return super.newApplication(cl, HiltTestApplication.class.getName(), context);
    }
}