package com.software.zero.ui.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.imageview.ShapeableImageView;
import com.software.util.share_preference.EncryptedPrefsHelper;
import com.software.util.share_preference.TokenPrefsHelper;
import com.software.zero.R;
import com.software.zero.enums.UserProperty;
import com.software.zero.repository.MessageRepository;
import com.software.zero.ui.activity.FindPeopleActivity;
import com.software.zero.ui.activity.InterceptorActivity;

import java.util.Base64;

public class OursFragment extends Fragment {
    public OursFragment() {
    }
    private ShapeableImageView leftImage, rightImage;
    private EncryptedPrefsHelper sharePreference;
    private MessageRepository messageRepository = new MessageRepository();
    private boolean hasTheOther = false;
    private View red_dot;
    private Button exit;
    private io.reactivex.rxjava3.disposables.Disposable resumeDisposable;

    @Override
    public void onResume() {
        super.onResume();
        String phoneNumber = sharePreference.getString(UserProperty.PHONE_NUMBER.getPropertyName());
        if(phoneNumber == null || phoneNumber.isEmpty()) {
            hasTheOther = false;
        } else {
            hasTheOther = true;
        }
        
        resumeDisposable = io.reactivex.rxjava3.core.Single.fromCallable(() -> messageRepository.checkNewMessage())
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(hasNewMessage -> {
                    if(hasNewMessage && !hasTheOther) {
                        red_dot.setVisibility(VISIBLE);
                    } else {
                        red_dot.setVisibility(GONE);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    red_dot.setVisibility(GONE);
                });

        if(!hasTheOther) {
            leftImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.add_friend));
            leftImage.setOnClickListener(v -> {
                requireActivity().startActivity(new Intent(requireActivity(), FindPeopleActivity.class));
            });
        } else {
            String picture = sharePreference.getString(UserProperty.PROFILE_PICTURE.getPropertyName());
            byte[] decode = Base64.getDecoder().decode(picture.getBytes());
            Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
            leftImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (resumeDisposable != null && !resumeDisposable.isDisposed()) {
            resumeDisposable.dispose();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        leftImage = view.findViewById(R.id.leftAvatar);
        rightImage = view.findViewById(R.id.rightAvatar);
        red_dot = view.findViewById(R.id.redDot);
        sharePreference = EncryptedPrefsHelper.getInstance();
        exit = view.findViewById(R.id.btn_logout);
        exit.setOnClickListener(v->{
            TokenPrefsHelper tokenPrefsHelper = TokenPrefsHelper.getInstance();
            tokenPrefsHelper.clearAuthToken();
            startActivity(new Intent(getContext(), InterceptorActivity.class));
            // 销毁当前Activity
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_ours_page, container, false);
    }

    public static OursFragment newInstance() {
        return new OursFragment();
    }
}
