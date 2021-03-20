package com.ipec.ownerapp.ui.mypayments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ipec.ownerapp.R;

public class MypaymentsFragment extends Fragment {

    private MypaymentsViewModel mypaymentsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mypaymentsViewModel =
                new ViewModelProvider(this).get(MypaymentsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mypayments, container, false);
        final TextView textView = root.findViewById(R.id.text_mypayments);
        mypaymentsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}