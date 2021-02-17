//
//  Advanced Android - MADS4006
//  CarSpot
//
//  Group 7
//  Brian Domingo - 101330689
//  Daryl Dyck - 101338429
//

package com.gb.carspot.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.gb.carspot.R;
import com.gb.carspot.activities.LoginActivity;
import com.gb.carspot.activities.MainActivity;
import com.gb.carspot.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import static com.gb.carspot.utils.Constants.ACTION_LOAD_LOGIN_PAGE;
import static com.gb.carspot.utils.Constants.ACTION_LOAD_MAIN_PAGE;
import static com.gb.carspot.utils.Constants.ACTION_LOAD_PROFILE_PAGE;
import static com.gb.carspot.utils.Constants.LOGIN_CURRENT_USER;
import static com.gb.carspot.utils.Constants.LOGIN_REMEMBER_ME;
import static com.gb.carspot.utils.Constants.SHARED_PREF_NAME;
import static com.gb.carspot.utils.Constants.THEME_PREFERENCE;
import static com.gb.carspot.utils.Constants.THEME_PREFERENCE_DEFAULT;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment
{
    private final String TAG = getClass().getCanonicalName();
    private View rootView;

    private static SharedPreferences sharedPrefs;
    private static SharedPreferences.Editor prefEditor;

    private FirebaseAuth mAuth;

    private Button loginButton;
    private TextView tvCreateAccount;

    private TextInputEditText editEmail;
    private TextInputEditText editPassword;
    private CheckBox checkRememberMe;


    public LoginFragment()
    {
    }

    public static LoginFragment newInstance()
    {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // set fragment animations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            setEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setExitTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setReenterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.move));
            setSharedElementReturnTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.move));
        }

        sharedPrefs = getActivity().getSharedPreferences(SHARED_PREF_NAME, 0);
        prefEditor = sharedPrefs.edit();
        Utils.applyTheme(sharedPrefs.getInt(THEME_PREFERENCE, THEME_PREFERENCE_DEFAULT));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);


        editEmail = rootView.findViewById(R.id.login_username_textField);
        editPassword = rootView.findViewById(R.id.login_password_textField);
        checkRememberMe = rootView.findViewById(R.id.chkRememberMe);

        setup();
        return rootView;
    }

    private void setup()
    {
        loginButton = rootView.findViewById(R.id.login_button);
        tvCreateAccount = rootView.findViewById(R.id.lblCreateAccount);

        mAuth = FirebaseAuth.getInstance();
        Utils.setHaptic(loginButton);
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                signIn(editEmail.getText().toString(), editPassword.getText().toString());
            }
        });

        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "tvCreateAccount pressed.");
                gotoSignUp();
            }
        });

    }

    private void gotoMain()
    {
        prefEditor.putString(LOGIN_CURRENT_USER, mAuth.getUid());
        if(checkRememberMe.isChecked()) {
            prefEditor.putBoolean(LOGIN_REMEMBER_ME, true);
        }
        prefEditor.apply();

        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setAction(ACTION_LOAD_MAIN_PAGE);
        startActivity(intent);
    }

    private void gotoSignUp()
    {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setAction(ACTION_LOAD_PROFILE_PAGE);
        startActivity(intent);
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Sign in successful");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getContext(), "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                            gotoMain();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Log in failed", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}