package me.lazy_assedninja.app.binding;

import android.graphics.drawable.Drawable;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;

import javax.inject.Inject;

import me.lazy_assedninja.app.repository.SecretRepository;
import me.lazy_assedninja.library.utils.LogUtils;

/**
 * Binding adapters that work with LogUtils and SecretRepository.
 */
@SuppressWarnings("unused")
public class ImageBindingAdapters {

    private static final String LOG_TAG = "BindingAdapters";

    public final LogUtils logUtils;
    public final SecretRepository secretRepository;

    @Inject
    public ImageBindingAdapters(LogUtils logUtils, SecretRepository secretRepository) {
        this.logUtils = logUtils;
        this.secretRepository = secretRepository;
    }

    @BindingAdapter(value = {"imageUrl", "requestListener", "placeholder", "error", "fallback"},
            requireAll = false)
    public void bindImage(AppCompatImageView imageView, String picturePath,
                          RequestListener<Drawable> listener, Drawable placeholder,
                          Drawable error, Drawable fallback) {
        Glide.with(imageView.getContext())
                .load(secretRepository.URL + picturePath)
                .listener(listener)
                .placeholder(placeholder)
                .error(error)
                .fallback(fallback)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
}