package com.example.pinder99;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pinder99.databinding.FragmentAddPictureBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddPictureBottomSheetFragment extends BottomSheetDialogFragment {
    private FragmentAddPictureBottomSheetBinding binding;
    private SelectMethodListener listener;

    public AddPictureBottomSheetFragment(SelectMethodListener listener) {
        this.listener = listener;
    }

    interface SelectMethodListener {
        void openCamera();
        void openGallery();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddPictureBottomSheetBinding.inflate(inflater, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding.getRoot().setClipToOutline(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        openGallery();
        openCamera();
    }

    private void openGallery() {
        binding.textviewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.openGallery();
                dismiss();
            }
        });
    }

    private void openCamera() {
        binding.textviewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.openCamera();
                dismiss();
            }
        });
    }
}
