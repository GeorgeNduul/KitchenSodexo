package com.example.recipeapp2.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// This class extends ViewModel, meaning it is tied to the lifecycle of a Fragment or Activity.
public class HomeViewModel extends ViewModel {

    //- MutableLiveData is a data holder that can be observed by the UI.
    //- When its value changes, any observer (like Fragment) automatically updates.
    private final MutableLiveData<String> mText;
//This sets the initial value of the text.
//If  Fragment observes this LiveData, it will immediately receive "This is home fragment".
    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }
// this returns a livedata which is read only, UI observes viewmodel updates
    public LiveData<String> getText() {
        return mText;
    }
}