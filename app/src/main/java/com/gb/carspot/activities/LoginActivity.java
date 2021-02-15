package com.gb.carspot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import com.gb.carspot.R;
import com.gb.carspot.fragments.LoginFragment;
import com.gb.carspot.fragments.ProfileFragment;
import com.gb.carspot.utils.Utils;
import com.gb.carspot.viewmodels.LoginActivityViewModel;
import com.gb.carspot.viewmodels.MainActivityViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.gb.carspot.utils.Constants.ACTION_LOAD_LOGIN_PAGE;
import static com.gb.carspot.utils.Constants.ACTION_LOAD_MAIN_PAGE;
import static com.gb.carspot.utils.Constants.ACTION_LOAD_PROFILE_PAGE;
import static com.gb.carspot.utils.Constants.LOGIN_REMEMBER_ME;
import static com.gb.carspot.utils.Constants.PAGE_LOGIN;
import static com.gb.carspot.utils.Constants.PAGE_PROFILE;
import static com.gb.carspot.utils.Constants.SHARED_PREF_NAME;
import static com.gb.carspot.utils.Constants.THEME_PREFERENCE;
import static com.gb.carspot.utils.Constants.THEME_PREFERENCE_DEFAULT;

public class LoginActivity extends AppCompatActivity
{
    private final String TAG = getClass().getCanonicalName();
    private static SharedPreferences sharedPrefs;
    private static SharedPreferences.Editor prefEditor;
    public String str;
    private LoginActivityViewModel loginActivityViewModel;

    //Firebase authorization
    private FirebaseAuth mAuth;

    private LoginFragment loginFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        sharedPrefs = getSharedPreferences(SHARED_PREF_NAME, 0);
        prefEditor = sharedPrefs.edit();
        Utils.applyTheme(sharedPrefs.getInt(THEME_PREFERENCE, THEME_PREFERENCE_DEFAULT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginActivityViewModel = new ViewModelProvider(this).get(LoginActivityViewModel.class);

        //initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        if(sharedPrefs.getBoolean(LOGIN_REMEMBER_ME, false) == true) {
            gotoMain();
        } else {
            loadLoginPage();
        }
    }

    public LoginActivityViewModel getLoginActivityViewModel() { return loginActivityViewModel; }

    // load Login fragment
    private void loadLoginPage()
    {
        if (loginFragment == null)
        {
            loginFragment = LoginFragment.newInstance();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().getFragments().size() == 0)
        {
            transaction.add(R.id.login_container, loginFragment).commit();
        }
        else
        {
            transaction.replace(R.id.login_container, loginFragment).commit();
        }
    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        if (intent != null && intent.getAction() != null)
        {
            switch (intent.getAction())
            {
                case ACTION_LOAD_MAIN_PAGE:
                    gotoMain();
                    break;
                case ACTION_LOAD_LOGIN_PAGE:
                    loadLoginPage();
                    break;
                case ACTION_LOAD_PROFILE_PAGE:
                    // load profile page
                    gotoSignUp();
                    Log.d(TAG, "ACTION_LOAD_PROFILE_PAGE");
                    break;
            }
        }
    }

    private void gotoMain()
    {
        Log.d(TAG, "gotoMain: ");
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void gotoSignUp()
    {

        if (loginActivityViewModel.getProfileFragment() == null)
        {
            loginActivityViewModel.setProfileFragment(ProfileFragment.newInstance());
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        loginActivityViewModel.setCurrentPage(PAGE_PROFILE);
        transaction.replace(R.id.login_container, loginActivityViewModel.getProfileFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        if(loginActivityViewModel.getCurrentPage() == PAGE_PROFILE) {
            loadLoginPage();
        } else {
            super.onBackPressed();
        }
    }
}