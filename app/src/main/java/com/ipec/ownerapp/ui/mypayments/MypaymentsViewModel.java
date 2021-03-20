package com.ipec.ownerapp.ui.mypayments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MypaymentsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MypaymentsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}