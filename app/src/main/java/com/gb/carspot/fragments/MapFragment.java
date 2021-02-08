package com.gb.carspot.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.transition.TransitionInflater;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.gb.carspot.R;
import com.gb.carspot.activities.MainActivity;
import com.gb.carspot.models.Location;
import com.gb.carspot.models.ParkingTicket;
import com.gb.carspot.models.User;
import com.gb.carspot.utils.Utils;
import com.gb.carspot.viewmodels.MainActivityViewModel;
import com.google.android.gms.maps.MapView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;

import static com.gb.carspot.utils.Constants.INITIAL_FRAGMENT_LOAD;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment
{
    private final String TAG = getClass().getCanonicalName();
    private MainActivityViewModel mainActivityViewModel;

    private View rootView;
    boolean okToError = false;

    private MapView mapView;
    private TextInputLayout buildingCodeTextInputLayout;
    private TextInputLayout hostSuiteTextInputLayout;
    private TextInputLayout addressTextInputLayout;
    private MaterialButtonToggleGroup lengthToggleGroup;
    private Spinner licensePlateSpinner;
    private Button updateMapButton;
    private Button purchaseButton;

    // used to track first load of fragment
    private boolean initialFragmentLoad = true;

    public MapFragment()
    {
    }

    public static MapFragment newInstance(MainActivityViewModel mainActivityViewModel)
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
        okToError = false;

        if (getContext() != null)
        {
            this.mainActivityViewModel = ((MainActivity) getActivity()).getViewModel();

            // listen for views to fully load
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
            {
                @Override
                public void onGlobalLayout()
                {
                    // enable textInput error checking
                    okToError = true;
                }
            });

            mapView = rootView.findViewById(R.id.mapView);

            buildingCodeTextInputLayout = rootView.findViewById(R.id.buildingCode_InputLayout);
            buildingCodeTextInputLayout.getEditText().addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                }

                @Override
                public void afterTextChanged(Editable editable)
                {
                    if (okToError)
                    {
                        Utils.checkBuildingCode(buildingCodeTextInputLayout, getString(R.string.five_characters));
                    }
                }
            });

            hostSuiteTextInputLayout = rootView.findViewById(R.id.hostSuite_InputLayout);
            hostSuiteTextInputLayout.getEditText().addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                }

                @Override
                public void afterTextChanged(Editable editable)
                {
                    if (okToError)
                    {
                        Utils.checkSuiteNumber(hostSuiteTextInputLayout, getString(R.string.two_five_characters));
                    }
                }
            });

            addressTextInputLayout = rootView.findViewById(R.id.address_InputLayout);
            addressTextInputLayout.getEditText().addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                }

                @Override
                public void afterTextChanged(Editable editable)
                {
                    if (okToError)
                    {
                        purchaseButton.setEnabled(false);
//                        Utils.checkStreetAddress(addressTextInputLayout, getString(R.string.invalid_address));
                    }
                }
            });

            lengthToggleGroup = rootView.findViewById(R.id.length_ButtonToggleGroup);
            lengthToggleGroup.check(R.id.length1_button);
            Utils.setHaptic(lengthToggleGroup.getChildAt(0));
            Utils.setHaptic(lengthToggleGroup.getChildAt(1));
            Utils.setHaptic(lengthToggleGroup.getChildAt(2));
            Utils.setHaptic(lengthToggleGroup.getChildAt(3));
            lengthToggleGroup.getChildAt(0).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    lengthToggleGroup.check(view.getId());
                }
            });
            lengthToggleGroup.getChildAt(1).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    lengthToggleGroup.check(view.getId());
                }
            });
            lengthToggleGroup.getChildAt(2).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    lengthToggleGroup.check(view.getId());
                }
            });
            lengthToggleGroup.getChildAt(3).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    lengthToggleGroup.check(view.getId());
                }
            });

            licensePlateSpinner = rootView.findViewById(R.id.license_spinner);

            updateMapButton = rootView.findViewById(R.id.update_button);
            Utils.setHaptic(updateMapButton);
            updateMapButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    purchaseButton.setEnabled(true);
                }
            });

            purchaseButton = rootView.findViewById(R.id.purchase_button);
            Utils.setHaptic(purchaseButton);
            purchaseButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (Utils.checkBuildingCode(buildingCodeTextInputLayout, getString(R.string.five_characters)) &&
                            Utils.checkSuiteNumber(hostSuiteTextInputLayout, getString(R.string.two_five_characters)) &&
                            Utils.checkStreetAddress(addressTextInputLayout, getString(R.string.invalid_address)))
                    {
                        ParkingTicket parkingTicket = new ParkingTicket(
                                buildingCodeTextInputLayout.getEditText().getText().toString().toUpperCase(),
                                getLength(),
                                mainActivityViewModel.getUser().getValue().getLicensePlates().get(licensePlateSpinner.getSelectedItemPosition()),
                                hostSuiteTextInputLayout.getEditText().getText().toString().toUpperCase(),
                                new Location(),
                                new Date(),
                                "imageUrl");

                        //mainActivityViewModel.addParkingTicket(parkingTicket);
                    }
                }
            });

            mainActivityViewModel.getUser().observe(getActivity(), new Observer<User>()
            {
                @Override
                public void onChanged(User user)
                {
                    ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item,
                            mainActivityViewModel.getUser().getValue().getLicensePlates());
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    licensePlateSpinner.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private int getLength()
    {
        int length = 1;
        switch (lengthToggleGroup.getCheckedButtonId())
        {
            case R.id.length1_button:
                length = 1;
                break;
            case R.id.length4_button:
                length = 4;
                break;
            case R.id.length12_button:
                length = 12;
                break;
            case R.id.length24_button:
                length = 24;
                break;
        }
        return length;
    }
}