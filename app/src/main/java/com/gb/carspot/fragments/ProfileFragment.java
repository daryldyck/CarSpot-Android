package com.gb.carspot.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gb.carspot.R;
import com.gb.carspot.activities.LoginActivity;
import com.gb.carspot.activities.MainActivity;
import com.gb.carspot.models.User;
import com.gb.carspot.repositories.UserRepository;
import com.gb.carspot.viewmodels.LoginActivityViewModel;
import com.gb.carspot.viewmodels.MainActivityViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.transition.TransitionInflater;

import java.util.ArrayList;
import java.util.List;

import static com.gb.carspot.utils.Constants.ACTION_LOAD_LOGIN_PAGE;
import static com.gb.carspot.utils.Constants.ACTION_LOAD_MANAGE_PLATES_PAGE;
import static com.gb.carspot.utils.Constants.ACTION_LOAD_PROFILE_PAGE;
import static com.gb.carspot.utils.Constants.LOGIN_CURRENT_USER;
import static com.gb.carspot.utils.Constants.SHARED_PREF_NAME;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment
{
    private final String TAG = getClass().getCanonicalName();
    private static SharedPreferences sharedPrefs;
    private static SharedPreferences.Editor prefEditor;
    private MainActivityViewModel mainActivityViewModel;
    private LoginActivityViewModel loginActivityViewModel;
    private View rootView;

    private FirebaseAuth mAuth;
    private UserRepository userRepository;

    private TextInputEditText editFirstName;
    private TextInputEditText editLastName;
    private TextInputEditText editEmail;
    private TextInputEditText editPhoneNumber;
    private TextInputEditText editPassword;
    private TextInputEditText editConfirm;
    private TextInputEditText editPlateNumber;
    private TextInputLayout inputLayoutEnterPlate;
    private MaterialButton btnManagePlates;
    private MaterialButton btnSignUp;

    public ProfileFragment()
    {
    }

    public static ProfileFragment newInstance()
    {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sharedPrefs = getActivity().getSharedPreferences(SHARED_PREF_NAME, 0);
        prefEditor = sharedPrefs.edit();
        userRepository = new UserRepository();

        // set fragment animations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            setEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setExitTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setReenterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.move));
            setSharedElementReturnTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.move));
        }
        Log.d(TAG, "DONE");

    }

    @Override
    public void onStart() {
        super.onStart();

        //Check to see if there is a current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        editFirstName = rootView.findViewById(R.id.firstName_textField);
        editLastName = rootView.findViewById(R.id.lastName_textField);
        editEmail = rootView.findViewById(R.id.email_textField);
        editPhoneNumber = rootView.findViewById(R.id.phoneNumber_textField);
        editPassword = rootView.findViewById(R.id.password_textField);
        editConfirm = rootView.findViewById(R.id.confirmPassword_textField);
        btnManagePlates = rootView.findViewById(R.id.managePlates_button);
        inputLayoutEnterPlate = rootView.findViewById(R.id.enterPlate_InputLayout);
        editPlateNumber = rootView.findViewById(R.id.enterPlate_textField);
        btnManagePlates = rootView.findViewById(R.id.managePlates_button);
        btnManagePlates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadLicensePlateFragment();
            }
        });

        btnSignUp = rootView.findViewById(R.id.createAccount_button);


        if(sharedPrefs.getString(LOGIN_CURRENT_USER, null) != null){
            //if user is already in go to main activity
            btnManagePlates.setVisibility(View.VISIBLE);
            inputLayoutEnterPlate.setVisibility(View.INVISIBLE);
            setupUserInfo(savedInstanceState);
            btnSignUp.setText("Update Account Info");
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Updating account");

                }
            });
        } else {
            Log.d(TAG, "Load fragment_add_license_plates");
            btnManagePlates.setVisibility(View.INVISIBLE);
            inputLayoutEnterPlate.setVisibility(View.VISIBLE);
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createAccount();
                    addUserToFirebase();
                }
            });
        }

        return rootView;
    }

    // load License Plate fragment
    private void loadLicensePlateFragment() {
        Log.d(TAG, "loadLicensePlateFragment()");
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setAction(ACTION_LOAD_MANAGE_PLATES_PAGE);
        startActivity(intent);
    }

    private void setupUserInfo(Bundle savedInstanceState) {
        if(getContext() != null) {
            this.mainActivityViewModel = ((MainActivity) getActivity()).getViewModel();
        }

        mainActivityViewModel.getUser().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                editFirstName.setText(user.getFirstName());
                editLastName.setText(user.getLastName());
                editEmail.setText(user.getEmail());
                editPhoneNumber.setText(Long.toString(user.getPhone()));
            }
        });
    }

    public void updateAccount() {
        //TODO
    }

    public void createAccount() {
        mAuth.createUserWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            return;
                        }
                    }
                });
    }

    public void addUserToFirebase() {
        List<String> newPlateList = new ArrayList<>();
        newPlateList.add(editPlateNumber.getText().toString().toUpperCase());

        User newUser = new User(editEmail.getText().toString().toLowerCase(), editPassword.getText().toString(),
                Long.parseLong(editPhoneNumber.getText().toString()), editFirstName.getText().toString(),
                editLastName.getText().toString(), newPlateList);

        userRepository.addUser(newUser);

        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setAction(ACTION_LOAD_LOGIN_PAGE);
        startActivity(intent);
    }

}