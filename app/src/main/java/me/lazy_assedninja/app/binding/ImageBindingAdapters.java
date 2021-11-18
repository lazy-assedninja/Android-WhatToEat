package me.lazy_assedninja.app.binding;

import android.graphics.drawable.Drawable;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.signature.ObjectKey;

import javax.inject.Inject;

import me.lazy_assedninja.app.BuildConfig;

/**
 * Binding adapters that work with LogUtils.
 */
@SuppressWarnings("unused")
public class ImageBindingAdapters {

    private static final String STORE_FOLDER = "File/Store/";
    private static final String USER_FOLDER = "File/User/";

    @Inject
    public ImageBindingAdapters() {
    }

    @BindingAdapter(value = {"imageUrl", "requestListener", "placeholder", "error", "fallback"},
            requireAll = false)
    public void bindImage(AppCompatImageView imageView, String picturePath,
                          RequestListener<Drawable> listener, Drawable placeholder,
                          Drawable error, Drawable fallback) {
        Glide.with(imageView.getContext())
                .load(BuildConfig.URL + STORE_FOLDER + picturePath)
                .listener(listener)
                .placeholder(placeholder)
                .error(error)
                .fallback(fallback)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @BindingAdapter(value = {"portraitUrl", "portraitError", "portraitPlaceholder", "portraitFallback",
            "portraitSignature"}, requireAll = false)
    public void bindPortrait(AppCompatImageView imageView, String picturePath, Drawable placeholder,
                             Drawable error, Drawable fallback, String signature) {
        if (signature == null) signature = "";
        Glide.with(imageView.getContext())
                .load(BuildConfig.URL + USER_FOLDER + picturePath)
                .placeholder(placeholder)
                .error(error)
                .fallback(fallback)
                .signature(new ObjectKey(signature))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
}