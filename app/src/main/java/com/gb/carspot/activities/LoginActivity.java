package com.gb.carspot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gb.carspot.R;
import com.gb.carspot.fragments.LoginFragment;
import com.gb.carspot.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    //Firebase authorization
    private FirebaseAuth mAuth;

    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        sharedPrefs = getSharedPreferences(SHARED_PREF_NAME, 0);
        prefEditor = sharedPrefs.edit();
        Utils.applyTheme(sharedPrefs.getInt(THEME_PREFERENCE, THEME_PREFERENCE_DEFAULT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        loadLoginPage();
    }

    //Checks to see if user is already logged in. Uncomment after log in functionality works.
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            //if user is already in go to main activity
//            gotoMain();
//        }
//    }

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
                    gotoSignUp();
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
        Log.d(TAG, "gotoSignUp: ");
        finish();
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}