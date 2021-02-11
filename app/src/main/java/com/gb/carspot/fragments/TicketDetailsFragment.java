package com.gb.carspot.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionInflater;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gb.carspot.R;
import com.gb.carspot.activities.MainActivity;
import com.gb.carspot.models.Location;
import com.gb.carspot.models.ParkingTicket;
import com.gb.carspot.utils.LocationManager;
import com.gb.carspot.utils.Utils;
import com.gb.carspot.viewmodels.TicketDetailsFragmentViewModel;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.gb.carspot.utils.Constants.ACTION_DISPLAY_BACK_BUTTON;
import static com.gb.carspot.utils.Constants.EXTRA_PARKING_TICKET;
import static com.gb.carspot.utils.Constants.INITIAL_FRAGMENT_LOAD;
import static com.gb.carspot.utils.Constants.LOCATION_LAT;
import static com.gb.carspot.utils.Constants.LOCATION_LAT_DEFAULT;
import static com.gb.carspot.utils.Constants.LOCATION_LON;
import static com.gb.carspot.utils.Constants.LOCATION_LON_DEFAULT;

public class TicketDetailsFragment extends Fragment
{
    private final String TAG = getClass().getCanonicalName();
    private View rootView;
    private TicketDetailsFragmentViewModel viewModel;

    private GoogleMap googleMap;
    private final Float DEFAULT_ZOOM = 16.0f;
    private LocationManager locationManager;
    private MapView mapView;

    private ImageView background;
    private View gradient;
    private TextView address;
    private TextView address2;
    private TextView buildingCode;
    private TextView aptNo;
    private TextView date;
    private TextView start;
    private TextView end;
    private TextView license;
    private TextView length;

    public TicketDetailsFragment()
    {
    }

    public static TicketDetailsFragment newInstance(ParkingTicket parkingTicket)
    {
        TicketDetailsFragment fragment = new TicketDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_PARKING_TICKET, parkingTicket);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TicketDetailsFragmentViewModel.class);

        // set fragment animations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            setEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setExitTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setReenterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.fade));
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.move));
            setSharedElementReturnTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.move));
        }

        if (getArguments() != null)
        {
            viewModel.setParkingTicket((ParkingTicket) getArguments().getSerializable(EXTRA_PARKING_TICKET));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_ticket_details, container, false);
        setup(savedInstanceState);
        setupBackButton();
        return rootView;
    }

    // setup and load fields with ticket data
    private void setup(Bundle savedInstanceState)
    {
        if (getContext() != null)
        {
            locationManager = LocationManager.getInstance();
            locationManager.checkPermissions(getActivity(), this);

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
                        setupMapScreen(googleMap);

                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(viewModel.getParkingTicket().getLocation().getLat(),
                                        viewModel.getParkingTicket().getLocation().getLon()))
                                .icon(Utils.getBitmapDescriptor(getActivity(), R.drawable.ic_map_ticket_car))
                                .anchor(0f, 0.5f));

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(viewModel.getParkingTicket().getLocation().getLat() - 0.002,
                                        viewModel.getParkingTicket().getLocation().getLon()), DEFAULT_ZOOM));

                    }
                }
            });

            background = rootView.findViewById(R.id.background_imageView);
            gradient = rootView.findViewById(R.id.gradient_view);
            mapView = rootView.findViewById(R.id.mapView);
            address = rootView.findViewById(R.id.address_textView);
            address2 = rootView.findViewById(R.id.address2_textView);
            buildingCode = rootView.findViewById(R.id.buildingCode_textView);
            aptNo = rootView.findViewById(R.id.suiteNo_textView);
            date = rootView.findViewById(R.id.dateValue_textView);
            start = rootView.findViewById(R.id.startValue_textView);
            end = rootView.findViewById(R.id.endValue_textView);
            license = rootView.findViewById(R.id.license_textView);
            length = rootView.findViewById(R.id.length_textView);

            // used for shared element animations
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                background.setTransitionName("background" + "_" + viewModel.getParkingTicket().getDate().getTime());
                gradient.setTransitionName("gradient" + "_" + viewModel.getParkingTicket().getDate().getTime());
                mapView.setTransitionName("mapView" + "_" + viewModel.getParkingTicket().getDate().getTime());
                address.setTransitionName("address" + "_" + viewModel.getParkingTicket().getDate().getTime());
                date.setTransitionName("date" + "_" + viewModel.getParkingTicket().getDate().getTime());
                length.setTransitionName("length" + "_" + viewModel.getParkingTicket().getDate().getTime());
            }

            address.setText(viewModel.getParkingTicket().getLocation().getStreetAddress());
            address2.setText(viewModel.getParkingTicket().getLocation().getCity() + ", " +
                    viewModel.getParkingTicket().getLocation().getCountry());
            buildingCode.setText(viewModel.getParkingTicket().getBuildingCode());
            aptNo.setText(viewModel.getParkingTicket().getHostSuite());
            date.setText(viewModel.getParkingTicket().getDateString());
            start.setText(viewModel.getParkingTicket().getStartString());
            end.setText(viewModel.getParkingTicket().getEndString());
            license.setText(viewModel.getParkingTicket().getLicensePlate());
            length.setText(viewModel.getParkingTicket().getLength());
        }
    }

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

    // turn on back button in toolbar from MainActivity
    private void setupBackButton()
    {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setAction(ACTION_DISPLAY_BACK_BUTTON);
        startActivity(intent);
    }

    @Override
    public void onResume()
    {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause()
    {
        super.onPause();
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