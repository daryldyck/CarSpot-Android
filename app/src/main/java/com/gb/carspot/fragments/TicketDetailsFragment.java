package com.gb.carspot.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gb.carspot.R;
import com.gb.carspot.activities.MainActivity;

import static com.gb.carspot.utils.Constants.ACTION_DISPLAY_BACK_BUTTON;
import static com.gb.carspot.utils.Constants.INITIAL_FRAGMENT_LOAD;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TicketDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TicketDetailsFragment extends Fragment
{
    private final String TAG = getClass().getCanonicalName();
    private View rootView;

    // used to track first load of fragment
    private boolean initialFragmentLoad = true;

    public TicketDetailsFragment()
    {
    }

    public static TicketDetailsFragment newInstance()
    {
        return new TicketDetailsFragment();
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

        // load values from saved state
        if (savedInstanceState != null)
        {
            initialFragmentLoad = savedInstanceState.getBoolean(INITIAL_FRAGMENT_LOAD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_ticket_details, container, false);
        setup();
        setupBackButton();
        return rootView;
    }

    // setup and load fields with ticket data
    private void setup()
    {

    }

    // turn on back button in toolbar from MainActivity
    private void setupBackButton()
    {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setAction(ACTION_DISPLAY_BACK_BUTTON);
        startActivity(intent);
    }

}