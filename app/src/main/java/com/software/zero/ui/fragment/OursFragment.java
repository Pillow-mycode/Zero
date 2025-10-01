package com.software.zero.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.imageview.ShapeableImageView;
import com.software.util.share_preference.EncryptedPrefsHelper;
import com.software.zero.R;
import com.software.zero.enums.UserProperty;
import com.software.zero.ui.activity.FindPeopleActivity;

public class OursFragment extends Fragment {
    private OursFragment() {
    }
    private ShapeableImageView leftImage, rightImage;
    private EncryptedPrefsHelper sharePreference;
    private boolean hasTheOther = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        leftImage = view.findViewById(R.id.leftAvatar);
        rightImage = view.findViewById(R.id.rightAvatar);
        sharePreference = EncryptedPrefsHelper.getInstance();
        String phoneNumber = sharePreference.getString(UserProperty.PHONE_NUMBER.getPropertyName());
        if(phoneNumber == null || phoneNumber.isEmpty()) {
            hasTheOther = false;
        }
        if(!hasTheOther) {
            leftImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_boy));
            leftImage.setOnClickListener(v -> {
                requireActivity().startActivity(new Intent(requireActivity(), FindPeopleActivity.class));
            });
        }
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
