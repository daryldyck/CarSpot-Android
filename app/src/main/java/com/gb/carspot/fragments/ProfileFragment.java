package com.gb.carspot.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gb.carspot.R;
import com.gb.carspot.activities.MainActivity;
import com.gb.carspot.models.User;
import com.gb.carspot.viewmodels.MainActivityViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.transition.TransitionInflater;

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
    private View rootView;

    private FirebaseAuth mAuth;

    private TextInputEditText editFirstName;
    private TextInputEditText editLastName;
    private TextInputEditText editEmail;
    private TextInputEditText editPhoneNumber;
    private TextInputEditText editPassword;
    private TextInputEditText editConfirm;
    private TextInputEditText editPlateNumber;
    private AddLicensePlateFragment addLicensePlateFragment;
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

        mAuth = FirebaseAuth.getInstance();
        sharedPrefs = getActivity().getSharedPreferences(SHARED_PREF_NAME, 0);
        prefEditor = sharedPrefs.edit();

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

        if(sharedPrefs.getString(LOGIN_CURRENT_USER, null) != null){
            //if user is already in go to main activity
            Log.d(TAG, "Load fragment_manage_plates");
            setupUserInfo(savedInstanceState);
        } else {
            Log.d(TAG, "Load fragment_add_license_plates");
            loadLicensePlateFragment();
        }

        return rootView;
    }

    // load License Plate fragment
    private void loadLicensePlateFragment() {
        Log.d(TAG, "loadLicensePlateFragment()");
        ViewGroup addPlateFrame = rootView.findViewById(R.id.licensePlate_frame);

        LayoutInflater inflater = (LayoutInflater)getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ConstraintLayout addLicenseFragment = (ConstraintLayout)inflater
                .inflate(R.layout.fragment_add_license_plate, addPlateFrame, false);
        addPlateFrame.addView(addLicenseFragment);
    }

    private void setupUserInfo(Bundle savedInstanceState) {
        if(getContext() != null) {
            this.mainActivityViewModel = ((MainActivity) getActivity()).getViewModel();
        }

        editFirstName = rootView.findViewById(R.id.firstName_textField);
        editLastName = rootView.findViewById(R.id.lastName_textField);
        editEmail = rootView.findViewById(R.id.email_textField);
        editPhoneNumber = rootView.findViewById(R.id.phoneNumber_textField);

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

}