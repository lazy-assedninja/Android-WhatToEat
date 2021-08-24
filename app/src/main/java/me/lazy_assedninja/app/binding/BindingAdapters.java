package me.lazy_assedninja.app.binding;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.repository.SecretRepository;
import me.lazy_assedninja.library.utils.LogUtils;

import static androidx.core.app.ActivityCompat.startPostponedEnterTransition;

/**
 * Data Binding adapters specific to the app.
 */
@SuppressWarnings("unused")
public class BindingAdapters {

    private static final String LOG_TAG = "BindingAdapters";

    @BindingAdapter("visibleOrGone")
    public static void visibleOrGone(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("headPortraitUrl")
    public static void bindHeadPortraitImage(AppCompatImageView imageView, String picturePath) {
        Context context = imageView.getContext();
        Glide.with(context).load(SecretRepository.URL + picturePath)
                .listener(
                        new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                if (e == null) {
                                    LogUtils.e(LOG_TAG, "Load failed: Exception is null.");
                                    return false;
                                }

                                // Or, to log all root causes locally, you can use the built in helper method.
                                e.logRootCauses(LOG_TAG);
                                return false; // Allow calling onLoadFailed on the Target.
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                return false; // Allow calling onResourceReady on the Target.
                            }
                        }
                ).fallback(R.drawable.icon)
                .placeholder(R.drawable.icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @BindingAdapter("storePictureUrl")
    public static void bindStoreImage(AppCompatImageView imageView, String picturePath) {
        Context context = imageView.getContext();
        Glide.with(context).load(SecretRepository.URL + picturePath)
                .listener(
                        new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                if (e == null) {
                                    LogUtils.e(LOG_TAG, "Load failed: Exception is null.");
                                    return false;
                                }

                                // Or, to log all root causes locally, you can use the built in helper method.
                                e.logRootCauses(LOG_TAG);
                                return false; // Allow calling onLoadFailed on the Target.
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                return false; // Allow calling onResourceReady on the Target.
                            }
                        }
                )
                .fallback(R.drawable.ic_loading_store)
                .placeholder(R.drawable.ic_loading_store)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @BindingAdapter(value = {"storeInformationPictureUrl", "storeInformationRequestListener"}, requireAll = false)
    public static void bindStoreInformationImage(AppCompatImageView imageView, String picturePath, RequestListener<Drawable> listener) {
        Context context = imageView.getContext();
        Glide.with(context).load(SecretRepository.URL + picturePath)
                .listener(listener)
                .fallback(R.drawable.ic_loading_store)
                .placeholder(R.drawable.ic_loading_store)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
}