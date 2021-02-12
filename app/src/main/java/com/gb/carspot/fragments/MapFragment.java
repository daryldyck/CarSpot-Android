//
//  Advanced Android - MADS4006
//  CarSpot
//
//  Group 7
//  Brian Domingo - 101330689
//  Daryl Dyck - 101338429
//

package com.gb.carspot.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
import android.widget.Toast;

import com.gb.carspot.R;
import com.gb.carspot.activities.MainActivity;
import com.gb.carspot.models.Location;
import com.gb.carspot.models.ParkingTicket;
import com.gb.carspot.models.User;
import com.gb.carspot.utils.LocationManager;
import com.gb.carspot.utils.Utils;
import com.gb.carspot.viewmodels.MainActivityViewModel;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;
import java.util.List;

import static com.gb.carspot.utils.Constants.*;

public class MapFragment extends Fragment
{
    private final String TAG = getClass().getCanonicalName();
    private static SharedPreferences sharedPrefs;
    private static SharedPreferences.Editor prefEditor;
    private MainActivityViewModel mainActivityViewModel;

    private View rootView;
    boolean okToError = false;

    private GoogleMap googleMap;
    private final Float DEFAULT_ZOOM = 16.0f;
    private Double mapOffset;
    private LocationManager locationManager;
    private LocationCallback locationCallback;
    private LatLng currentLocation;
    private Location myLocation;
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
        setup(savedInstanceState);
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
    private void setup(Bundle savedInstanceState)
    {
        okToError = false;

        if (getContext() != null)
        {
            mapOffset = Utils.getMapOffset(getActivity());

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

            // callback for users current location
            locationCallback = new LocationCallback()
            {
                @Override
                public void onLocationResult(LocationResult locationResult)
                {
                    if (locationResult == null)
                    {
                        return;
                    }

                    for (android.location.Location loc : locationResult.getLocations())
                    {
                        currentLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
                        myLocation = locationManager.getLocation(getActivity(), loc);

                        if (myLocation != null && googleMap != null)
                        {
                            if (getContext() != null)
                            {
                                addressTextInputLayout.getEditText().setText(myLocation.getStreetAddress());
                                purchaseButton.setEnabled(true);

                                // move camera center point up a bit
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(currentLocation.latitude - mapOffset, currentLocation.longitude), DEFAULT_ZOOM));

                                Marker marker = googleMap.addMarker(
                                        new MarkerOptions().position(currentLocation).title(getString(R.string.current_location)));
                                marker.showInfoWindow();

                                // add current location to sharedPrefs
                                prefEditor.putString(LOCATION_LAT, String.valueOf(loc.getLatitude() - mapOffset)).commit();
                                prefEditor.putString(LOCATION_LON, String.valueOf(loc.getLongitude())).commit();
                            }
                        }
                    }
                    // cancel location updates after received
                    locationManager.stopLocationUpdates(getActivity(), this);
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability)
                {
                    super.onLocationAvailability(locationAvailability);
                }
            };

            locationManager = LocationManager.getInstance();
            locationManager.checkPermissions(getActivity(), this);

            if (locationManager.locationPermissionGranted)
            {
                locationManager.requestLocationUpdates(getActivity(), locationCallback);
            }

            //setup Google MapView
            mapView = rootView.findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(new OnMapReadyCallback()
            {
                @Override
                public void onMapReady(GoogleMap map)
                {
                    googleMap = map;

                    if (googleMap != null)
                    {
                        // set default map location based on last location
                        LatLng location = new LatLng(
                                Double.parseDouble(sharedPrefs.getString(LOCATION_LAT, LOCATION_LAT_DEFAULT)),
                                Double.parseDouble(sharedPrefs.getString(LOCATION_LON, LOCATION_LON_DEFAULT)));

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        setupMapScreen(googleMap);
                        addTicketMarkers();
                    }
                }
            });

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
                    updateMap();
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
                                myLocation,
                                new Date(),
                                "imageUrl");

                        mainActivityViewModel.addParkingTicket(parkingTicket);
                    }
                }
            });

            // listen for license plate updates
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

            // listen for previous ticket updates - add markers
            mainActivityViewModel.getParkingTicketList().observe(getActivity(), new Observer<List<ParkingTicket>>()
            {
                @Override
                public void onChanged(List<ParkingTicket> parkingTickets)
                {
                    addTicketMarkers();
                }
            });

            // listen for ticket purchase confirmations
            mainActivityViewModel.getTicketAdded().observe(getActivity(), new Observer<Integer>()
            {
                @Override
                public void onChanged(Integer ticketAdded)
                {
                    Log.d(TAG, "getTicketAdded onChanged: ");

                    switch (ticketAdded)
                    {
                        case TICKET_ADDED:
                            Toast.makeText(getActivity(), R.string.ticket_purchase_success, Toast.LENGTH_SHORT).show();
                            mainActivityViewModel.getTicketAdded().setValue(TICKET_DEFAULT);
                            break;
                        case TICKET_FAILED:
                            Toast.makeText(getActivity(), R.string.error_purchasing_ticket, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        }
    }

    // setup mapView
    private void setupMapScreen(GoogleMap googleMap)
    {
        if (googleMap != null)
        {
            Log.d(TAG, "setupMapScreen: ");
            googleMap.setBuildingsEnabled(false);
            googleMap.setIndoorEnabled(false);
            googleMap.setTrafficEnabled(false);

            UiSettings uiSettings = googleMap.getUiSettings();
            uiSettings.setZoomControlsEnabled(true);
            uiSettings.setZoomGesturesEnabled(true);
            uiSettings.setMyLocationButtonEnabled(false);
            uiSettings.setScrollGesturesEnabled(true);
            uiSettings.setRotateGesturesEnabled(true);
        }
    }

    // add markers to map for previous ticket purchases
    private void addTicketMarkers()
    {
        if (mainActivityViewModel.getParkingTicketList().getValue() != null)
        {
            for (ParkingTicket parkingTicket : mainActivityViewModel.getParkingTicketList().getValue())
            {
                if (googleMap != null)
                {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(parkingTicket.getLocation().getLat(), parkingTicket.getLocation().getLon()))
                            .icon(Utils.getBitmapDescriptor(getActivity(), R.drawable.ic_map_ticket_car))
                            .anchor(0f, 0.5f));
                }
            }
        }
    }

    // update map with new user input address
    private void updateMap()
    {
        Location newLocation = locationManager.getLocationFromAddress(getActivity(),
                new Location(
                        0.0,
                        0.0,
                        addressTextInputLayout.getEditText().getText().toString(),
                        myLocation.getCity(),
                        myLocation.getCountry(),
                        false));

        if (newLocation != null && googleMap != null)
        {
            myLocation = newLocation;
            // move map to new location and the camera center point up a bit
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(newLocation.getLat() - mapOffset, newLocation.getLon()), DEFAULT_ZOOM));

            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(newLocation.getLat(), newLocation.getLon()))
                    .title(newLocation.getStreetAddress()));

            purchaseButton.setEnabled(true);
        }
        else
        {
            Utils.setEditTextError(addressTextInputLayout, "Invalid Address");
        }
    }

    // get ticket length from button toggle group
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

    @Override // callback for permission requests
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == locationManager.LOCATION_PERMISSION_REQUEST_CODE)
        {
            locationManager.locationPermissionGranted =
                    (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);

            if (locationManager.locationPermissionGranted)
            {
                if (locationCallback != null)
                {
                    locationManager.requestLocationUpdates(getActivity(), locationCallback);
                }
            }
            return;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();
    }


    @Override
    public void onPause()
    {
        super.onPause();
        locationManager.stopLocationUpdates(getActivity(), locationCallback);
        mapView.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}