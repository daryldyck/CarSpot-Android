package com.gb.carspot.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gb.carspot.R;

import static com.gb.carspot.utils.Constants.INITIAL_FRAGMENT_LOAD;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment
{
    private final String TAG = getClass().getCanonicalName();
    private View rootView;

    // used to track first load of fragment
    private boolean initialFragmentLoad = true;

    public MapFragment()
    {
    }

    public static MapFragment newInstance()
    {
        return new MapFragment();
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
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        setup();
        initialFragmentLoad = false;
        return rootView;
    }

    @Override // save values state
    public void onSaveInstanceState(Bundle outState)
    {
        outState.clear();
        outState.putBoolean(INITIAL_FRAGMENT_LOAD, initialFragmentLoad);
        super.onSaveInstanceState(outState);
    }

    // initialize views
    private void setup()
    {
        if (getContext() != null)
        {

        }
    }
}