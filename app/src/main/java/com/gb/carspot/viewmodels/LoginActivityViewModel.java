package com.gb.carspot.viewmodels;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.gb.carspot.fragments.ProfileFragment;
import com.gb.carspot.models.ParkingTicket;
import com.gb.carspot.models.User;
import com.gb.carspot.repositories.UserRepository;

import static com.gb.carspot.utils.Constants.PAGE_LOGIN;
import static com.gb.carspot.utils.Constants.PAGE_PROFILE;
import static com.gb.carspot.utils.Constants.SHARED_PREF_NAME;

public class LoginActivityViewModel extends AndroidViewModel {
    private final String TAG = getClass().getCanonicalName();
    private static SharedPreferences sharedPreferences;

    public boolean initialized = false;
    private int currentPage = PAGE_LOGIN;
    private ProfileFragment profileFragment;

    private UserRepository userRepository;

    MutableLiveData<User> user;

    public LoginActivityViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = getApplication().getSharedPreferences(SHARED_PREF_NAME, 0);
        userRepository = UserRepository.getInstance();
    }

    public ProfileFragment getProfileFragment()
    {
        return profileFragment;
    }

    public void setProfileFragment(ProfileFragment profileFragment)
    {
        this.profileFragment = profileFragment;
    }

    public void createAccount(User newUser)
    {
        userRepository.addUser(newUser);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
