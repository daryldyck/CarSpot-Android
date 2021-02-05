package com.gb.carspot.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.gb.carspot.R;
import com.gb.carspot.fragments.LoginFragment;
import com.gb.carspot.utils.Utils;

import static com.gb.carspot.utils.Constants.ACTION_LOAD_LOGIN_PAGE;
import static com.gb.carspot.utils.Constants.ACTION_LOAD_MAIN_PAGE;
import static com.gb.carspot.utils.Constants.ACTION_LOAD_PROFILE_PAGE;
import static com.gb.carspot.utils.Constants.SHARED_PREF_NAME;
import static com.gb.carspot.utils.Constants.THEME_PREFERENCE;
import static com.gb.carspot.utils.Constants.THEME_PREFERENCE_DEFAULT;

public class LoginActivity extends AppCompatActivity
{
    private final String TAG = getClass().getCanonicalName();
    private static SharedPreferences sharedPrefs;
    private static SharedPreferences.Editor prefEditor;

    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        sharedPrefs = getSharedPreferences(SHARED_PREF_NAME, 0);
        prefEditor = sharedPrefs.edit();
        Utils.applyTheme(sharedPrefs.getInt(THEME_PREFERENCE, THEME_PREFERENCE_DEFAULT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadLoginPage();
    }

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
            transaction.add(R.id.main_container, loginFragment).commit();
        }
        else
        {
            transaction.replace(R.id.main_container, loginFragment).commit();
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
}