package me.lazy_assedninja.what_to_eat.binding;

import androidx.databinding.DataBindingComponent;

import javax.inject.Singleton;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

/**
 * A Data Binding Component implementation for layout, which needs glide to show image.
 */
@EntryPoint
@InstallIn(SingletonComponent.class)
public interface ImageDataBindingComponent extends DataBindingComponent {

    @Singleton
    @Override
    ImageBindingAdapters getImageBindingAdapters();
}