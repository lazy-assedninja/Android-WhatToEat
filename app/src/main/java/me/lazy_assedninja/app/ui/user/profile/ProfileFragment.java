package me.lazy_assedninja.app.ui.user.profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;

import dagger.hilt.EntryPoints;
import dagger.hilt.android.AndroidEntryPoint;
import me.lazy_assedninja.app.BuildConfig;
import me.lazy_assedninja.app.R;
import me.lazy_assedninja.app.binding.ImageDataBindingComponent;
import me.lazy_assedninja.app.databinding.ProfileFragmentBinding;
import me.lazy_assedninja.app.ui.user.create_report.CreateReportFragment;
import me.lazy_assedninja.app.ui.user.profile.head_portrait.PortraitOptionsCallback;
import me.lazy_assedninja.app.ui.user.profile.head_portrait.PortraitOptionsFragment;
import me.lazy_assedninja.app.vo.GoogleAccount;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.app.vo.Status;
import me.lazy_assedninja.library.ui.BaseFragment;
import me.lazy_assedninja.library.util.LogUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@AndroidEntryPoint
public class ProfileFragment extends BaseFragment {

    private final String[] PERMISSIONS = {
            Manifest.permission.CAMERA
    };

    private ProfileFragmentBinding binding;
    private ProfileViewModel viewModel;

    @Inject
    public LogUtil logUtil;

    private NavController navController;
    private Context context;

    private GoogleSignInClient googleSignInClient;
    private File headPortrait;
    private Uri portraitUri;

    private ActivityResultLauncher<String[]> requestPermissions;
    private ActivityResultLauncher<Uri> takePicture;
    private ActivityResultLauncher<String> getContent;
    private ActivityResultLauncher<Intent> imageCrop;
    private ActivityResultLauncher<Intent> googleSignIn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        DataBindingComponent dataBindingComponent = (getActivity() != null) ?
                EntryPoints.get(getActivity().getApplicationContext(), ImageDataBindingComponent.class) : null;
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.profile_fragment,
                container,
                false,
                dataBindingComponent
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        navController = Navigation.findNavController(view);
        if (getContext() != null) context = getContext();

        initView();
        initGoogleSignIn();
        initData();
        initFile();
        initActivityResult();
    }

    private void initView() {
        binding.ivHeadPortrait.setOnClickListener(v -> {
            PortraitOptionsFragment portraitOptionsFragment = new PortraitOptionsFragment(
                    new PortraitOptionsCallback() {
                        @Override
                        public void takePicture() {
                            if (ContextCompat.checkSelfPermission(context,
                                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                takePicture.launch(portraitUri);
                            } else {
                                requestPermissions.launch(PERMISSIONS);
                            }
                        }

                        @Override
                        public void getContent() {
                            getContent.launch("image/*");
                        }
                    }
            );
            portraitOptionsFragment.show(getParentFragmentManager(), "portrait_options");
        });
        binding.btBindGoogle.setOnClickListener(v ->
                googleSignIn.launch(googleSignInClient.getSignInIntent()));
        binding.btResetPassword.setOnClickListener(v ->
                navController.navigate(R.id.action_to_reset_password_fragment));
        binding.btLogout.setOnClickListener(v -> {
            viewModel.logout();
            navController.navigate(R.id.action_to_home_fragment);
        });
        binding.btReport.setOnClickListener(v -> {
            CreateReportFragment createReportFragment = new CreateReportFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("is_store", false);
            createReportFragment.setArguments(bundle);
            createReportFragment.show(getParentFragmentManager(), "create_report");
        });

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setUser(viewModel.getUser());
        binding.setBindGoogleResult(viewModel.bindGoogleResult);
        binding.setUploadResult(viewModel.uploadResult);
    }

    private void initGoogleSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
        if (getContext() == null) return;
        googleSignInClient = GoogleSignIn.getClient(getContext(), googleSignInOptions);

    }

    private void initData() {
        viewModel.bindGoogleResult.observe(getViewLifecycleOwner(), event -> {
            Resource<Result> resultResource = event.getContentIfNotHandled();
            if (resultResource == null) return;

            if (resultResource.getStatus().equals(Status.SUCCESS)) {
                showToast(resultResource.getData().getResult());

                googleSignInClient.signOut();
            } else if (resultResource.getStatus().equals(Status.ERROR)) {
                showToast(resultResource.getMessage());
            }
        });
        viewModel.uploadResult.observe(getViewLifecycleOwner(), event -> {
            Resource<Result> resultResource = event.getContentIfNotHandled();
            if (resultResource == null) return;

            if (resultResource.getStatus().equals(Status.SUCCESS)) {
                showToast(resultResource.getData().getResult());
            } else if (resultResource.getStatus().equals(Status.ERROR)) {
                showToast(resultResource.getMessage());
            }
        });
    }

    private void initFile() {
        try {
            File imageFolder = new File(context.getExternalFilesDir("images").getPath());
            if (!imageFolder.exists()) {
                boolean isSuccessful = imageFolder.mkdir();
                logUtil.d("ProfileFragment", "Create images folder result: " + isSuccessful);
            }

            headPortrait = new File(imageFolder, "head_portrait.jpg");
            if (!headPortrait.exists()) {
                boolean isSuccessful = headPortrait.createNewFile();
                logUtil.d("ProfileFragment", "Create jpg file result: " + isSuccessful);
            }
            portraitUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID
                    + ".provider", headPortrait);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void initActivityResult() {
        requestPermissions = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                    if (permissions.containsValue(true)) {
                        takePicture.launch(portraitUri);
                    }
                });
        takePicture = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result) {
                imageCrop.launch(getImageCropIntent());
            }
        });
        getContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                    if (getActivity() == null) return;
                    try {
                        InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                        OutputStream outputStream = getActivity().getContentResolver()
                                .openOutputStream(portraitUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    imageCrop.launch(getImageCropIntent());
                }
        );
        imageCrop = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), activityResult -> {
                    String fileName = viewModel.getUserEmail() + ".jpg";
                    viewModel.uploadFile(MultipartBody.Part.createFormData("file",
                            fileName, RequestBody.create(headPortrait,
                                    MediaType.parse("multipart/form-data"))));
                }
        );
        googleSignIn = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), activityResult -> {
                    Task<GoogleSignInAccount> task =
                            GoogleSignIn.getSignedInAccountFromIntent(activityResult.getData());
                    handleSignInResult(task);
                }
        );
    }

    private Intent getImageCropIntent() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(portraitUri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, portraitUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // Aspect of crop
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // output X and Y of image
        intent.putExtra("outputX", 128);
        intent.putExtra("outputY", 128);
        return intent;
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Bind google account
            viewModel.bindGoogleAccount(new GoogleAccount(account.getId(), account.getEmail(),
                    account.getDisplayName(), account.getPhotoUrl() == null ? "" :
                    account.getPhotoUrl().toString()));
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            showToast("Sign In Result: Failed code = " + e.getStatusCode());
        }
    }
}