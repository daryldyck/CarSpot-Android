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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.TransitionInflater;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment
{
    private final String TAG = getClass().getCanonicalName();
    private View rootView;

    private FirebaseAuth mAuth;

    private AddLicensePlateFragment addLicensePlateFragment;

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
        if(currentUser != null){
            //if user is already in go to main activity
            Log.d(TAG, "Load fragment_manage_plates");
        } else {
            Log.d(TAG, "Load fragment_add_license_plates");
            loadLicensePlateFragment();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
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


}